package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.api.http.EMPTY_BODY
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.server.ServerGlobals
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.io.ApiJarFile
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

/** Somewhat uniqueish parameter key name so it's unlikely to clash with anything a user would choose by chance. */
private const val KOBWEB_PARAMS = "kobweb-params"

fun Application.configureRouting(env: ServerEnvironment, conf: KobwebConf, globals: ServerGlobals) {
    val logger = object : Logger {
        override fun trace(message: String) = log.trace(message)
        override fun debug(message: String) = log.debug(message)
        override fun info(message: String) = log.info(message)
        override fun warn(message: String) = log.warn(message)
        override fun error(message: String) = log.error(message)
    }

    when (env) {
        ServerEnvironment.DEV -> configureDevRouting(conf, globals, logger)
        ServerEnvironment.PROD -> configureProdRouting(conf, logger)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleApiCall(
    apiJar: ApiJarFile,
    httpMethod: HttpMethod,
) {
    call.parameters[KOBWEB_PARAMS]?.takeIf { it.isNotBlank() }?.let { pathStr ->
        val body: ByteArray? = when (httpMethod) {
            HttpMethod.PATCH, HttpMethod.POST, HttpMethod.PUT -> {
                withContext(Dispatchers.IO) { call.receiveStream().readAllBytes() }.takeIf { it.isNotEmpty() }
            }
            else -> null
        }
        val bodyContentType = if (body != null) call.request.contentType().toString() else null

        val query = call.request.queryParameters
            .flattenEntries()
            .toMap()

        val request = Request(httpMethod, query, body, bodyContentType)
        val response = apiJar.apis.handle("/$pathStr", request)
        if (response != null) {
            call.respondBytes(
                response.body.takeIf { httpMethod != HttpMethod.HEAD } ?: EMPTY_BODY,
                status = HttpStatusCode.fromValue(response.status),
                contentType = response.contentType?.takeIf { httpMethod != HttpMethod.HEAD }
                    ?.let { ContentType.parse(it) }
            )
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}


private fun Routing.configureApiRouting(apiJar: ApiJarFile) {
    val path = "/api/{$KOBWEB_PARAMS...}"
    HttpMethod.values().forEach { httpMethod ->
        when (httpMethod) {
            HttpMethod.DELETE -> delete(path) { handleApiCall(apiJar, httpMethod) }
            HttpMethod.GET -> get(path) { handleApiCall(apiJar, httpMethod) }
            HttpMethod.HEAD -> head(path) { handleApiCall(apiJar, httpMethod) }
            HttpMethod.OPTIONS -> options(path) { handleApiCall(apiJar, httpMethod) }
            HttpMethod.PATCH -> patch(path) { handleApiCall(apiJar, httpMethod) }
            HttpMethod.POST -> post(path) { handleApiCall(apiJar, httpMethod) }
            HttpMethod.PUT -> put(path) { handleApiCall(apiJar, httpMethod) }
        }
    }
}

private suspend fun PipelineContext<*, ApplicationCall>.handleCatchAllRouting(script: Path, scriptMap: Path, index: Path, filename: String?) {
    var handled = false
    if (filename != null) {
        handled = true
        when(filename) {
            script.name -> call.respondFile(script.toFile())
            scriptMap.name -> call.respondFile(scriptMap.toFile())
            else -> {
                // Abort early on resources, so we don't serve giant html pages simply because a favicon.ico
                // file is missing, for example.
                val ext = File(filename).extension.takeIf { it.isNotEmpty() }
                if (ext != null && ext != "html") {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    handled = false
                }
            }
        }
    }

    // If unhandled at this point, we have a URL that should either generate a real page or an error page. Return
    // the main index.html which, by referencing our site's script, should have the logic which handles this.
    if (!handled) {
        call.respondFile(index.toFile())
    }
}

// Note: This should be defined LAST in the routing { ... } block and it used to handle general URLs. The site script
// itself looks at the user's current URL to figure out how to route itself, so in many cases, just returning
// "index.html" most of the time is enough for the client to figure out what to render next.
private fun Routing.configureCatchAllRouting(script: Path, index: Path) {
    val scriptMap = Path("$script.map")

    // Catch URLs of the form a/b/c/
    get("{$KOBWEB_PARAMS...}/") {
        handleCatchAllRouting(script, scriptMap, index, null)
    }

    // Catch URLs of the form a/b/c/slug
    get("{$KOBWEB_PARAMS...}") {
        val pathParts = call.parameters.getAll(KOBWEB_PARAMS)!!
        handleCatchAllRouting(script, scriptMap, index, pathParts.lastOrNull())
    }

}

private fun Application.configureDevRouting(conf: KobwebConf, globals: ServerGlobals, logger: Logger) {
    val script = Path(conf.server.files.dev.script)
    val contentRoot = Path(conf.server.files.dev.contentRoot)
    val apiJar = conf.server.files.dev.api.takeIf { it.isNotBlank() }?.let { ApiJarFile(Path(it), logger) }

    routing {
        // Set up SSE (server-sent events) for the client to hear about the state of our server
        get("/api/kobweb-status") {
            println("Client connected requesting kobweb status events")

            try {
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                    var lastVersion: Int? = null
                    var lastStatus: String? = null
                    while (true) {
                        write(": keepalive\n")
                        write("\n")

                        if (lastVersion != globals.version) {
                            lastVersion = globals.version
                            write("event: version\n")
                            write("data: $lastVersion\n")
                            write("\n")
                        }

                        if (lastStatus != globals.status) {
                            lastStatus = globals.status
                            val statusData = mapOf(
                                "text" to globals.status.orEmpty(),
                                "isError" to globals.isStatusError.toString(),
                            )
                            write("event: status\n")
                            write("data: ${Json.encodeToString(statusData)}\n")
                            write("\n")
                        }

                        flush()
                        delay(300)
                    }
                }
            } catch (ex: IOException) {
                println("Closing socket because client disconnected")
                // Expected eventually - client connection closed
            }
        }

        if (apiJar != null) {
            configureApiRouting(apiJar)
        }

        val contentRootFile = contentRoot.toFile()
        contentRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
            get("/${file.relativeTo(contentRootFile)}") {
                call.respondFile(file)
            }
        }

        configureCatchAllRouting(script, contentRoot.resolve("index.html"))
    }
}

private fun Application.configureProdRouting(conf: KobwebConf, logger: Logger) {
    val siteRoot = Path(conf.server.files.prod.siteRoot)
    val systemRoot = siteRoot.resolve("system")
    val resourcesRoot = siteRoot.resolve("resources")
    val pagesRoot = siteRoot.resolve("pages")

    val script = systemRoot.resolve(conf.server.files.dev.script.substringAfterLast("/"))
    val fallbackIndex = systemRoot.resolve("index.html")
    val apiJar = conf.server.files.dev.api.substringAfterLast("/")
        .takeIf { it.isNotBlank() }
        ?.let { ApiJarFile(systemRoot.resolve(it), logger) }

    routing {
        if (apiJar != null) {
            configureApiRouting(apiJar)
        }

        resourcesRoot.toFile().let { resourcesRootFile ->
            resourcesRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                get("/${file.relativeTo(resourcesRootFile)}") {
                    call.respondFile(file)
                }
            }
        }
        pagesRoot.toFile().let { pagesRootFile ->
            pagesRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                val relativeFile = file.relativeTo(pagesRootFile)
                val name = relativeFile.nameWithoutExtension
                if (name != "index") {
                    get("/${relativeFile.parent}/$name") {
                        call.respondFile(file)
                    }
                } else {
                    get("/${relativeFile.parent}") {
                        call.respondFile(file)
                    }
                }
            }
        }

        configureCatchAllRouting(script, fallbackIndex)
    }
}