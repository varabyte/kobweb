package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.api.http.EMPTY_BODY
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.server.ServerGlobals
import com.varabyte.kobweb.server.api.SiteLayout
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
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

/** Somewhat uniqueish parameter key name so it's unlikely to clash with anything a user would choose by chance. */
private const val KOBWEB_PARAMS = "kobweb-params"

fun Application.configureRouting(env: ServerEnvironment, siteLayout: SiteLayout, conf: KobwebConf, globals: ServerGlobals) {
    val logger = object : Logger {
        override fun trace(message: String) = log.trace(message)
        override fun debug(message: String) = log.debug(message)
        override fun info(message: String) = log.info(message)
        override fun warn(message: String) = log.warn(message)
        override fun error(message: String) = log.error(message)
    }

    if (siteLayout == SiteLayout.STATIC && env != ServerEnvironment.PROD) {
        log.warn("""
            Static site layout is configured for a development server.

            This isn't expected, as development servers expect to read their values from the user's project. Static
            layouts are really only designed to be used in production. The server will still run in static mode as
            requested, but live-reloading, server APIs, etc. will not work with this configuration.
        """.trimIndent())
    }

    when (siteLayout) {
        SiteLayout.KOBWEB -> {
            when (env) {
                ServerEnvironment.DEV -> configureDevRouting(conf, globals, logger)
                ServerEnvironment.PROD -> configureProdRouting(conf, logger)
            }
        }
        SiteLayout.STATIC -> configureStaticRouting(conf)
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

/**
 * Common handler used by [configureCatchAllRouting] since we have multiple route patterns which need the same handling
 */
private suspend fun PipelineContext<*, ApplicationCall>.handleCatchAllRouting(script: Path, scriptMap: Path, index: Path, pathParts: List<String>, extraHandler: suspend PipelineContext<*, ApplicationCall>.(String) -> Boolean) {
    var handled = false
    val filename = pathParts.lastOrNull()

    // Add special handling for script requests, since they may live in a totally different path based on server config
    if (filename != null) {
        handled = true
        when (filename) {
            script.name -> call.respondFile(script.toFile())
            scriptMap.name -> call.respondFile(scriptMap.toFile())
            else -> handled = false
        }
    }

    if (!handled) {
        handled = extraHandler(pathParts.joinToString("/"))
    }

    if (!handled) {
        if (filename != null) {
            // Abort early on missing resources, so we don't serve giant html pages simply because someone forgot to
            // add a favicon.ico file, for example.
            val ext = File(filename).extension.takeIf { it.isNotEmpty() }
            if (ext != null && ext != "html") {
                call.respond(HttpStatusCode.NotFound)
                handled = true
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
/**
 * @param script The path to the script.js file, which may be in a custom location depending on server configuration
 * @param index The path to the index.html file, which may be in a custom location depending on server configuration
 * @param extraHandler An optional handler so callers can configure additional, one-off handling.
 */
private fun Routing.configureCatchAllRouting(script: Path, index: Path, extraHandler: suspend PipelineContext<*, ApplicationCall>.(String) -> Boolean = { false }) {
    val scriptMap = Path("$script.map")

    // Catch URLs of the form a/b/c/
    get("{$KOBWEB_PARAMS...}/") {
        val pathParts = call.parameters.getAll(KOBWEB_PARAMS)!!
        handleCatchAllRouting(script, scriptMap, index, pathParts, extraHandler)
    }

    // Catch URLs of the form a/b/c/slug
    get("{$KOBWEB_PARAMS...}") {
        val pathParts = call.parameters.getAll(KOBWEB_PARAMS)!!
        handleCatchAllRouting(script, scriptMap, index, pathParts, extraHandler)
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
            } catch (ex: Exception) {
                // Use println instead of log because logging may cause issues if this is disconnecting due to the
                // server shutting down
                println("Closing socket because client disconnected (probably). Exception:")
                println("  $ex")
                // Expected eventually - client connection closed
            }
        }

        if (apiJar != null) {
            configureApiRouting(apiJar)
        }

        val contentRootFile = contentRoot.toFile()
        configureCatchAllRouting(script, contentRoot.resolve("index.html")) { path ->
            contentRootFile.resolve(path).let { contentFile ->
                if (contentFile.isFile && contentFile.exists()) {
                    call.respondFile(contentFile)
                    true
                } else {
                    false
                }
            }
        }
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

/**
 * Run a Kobweb server as a dumb, static server.
 *
 * This is kind of a waste of a Kobweb server, since it has all the smarts removed, but at the same time, it's supported
 * so a user can test-run the static site experience which will ultimately be provided by some external provider.
 */
private fun Application.configureStaticRouting(conf: KobwebConf) {
    val siteRoot = Path(conf.server.files.prod.siteRoot)
    routing {
        siteRoot.toFile().let { siteRootFile ->
            siteRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                val relativeFile = file.relativeTo(siteRootFile)
                val name = relativeFile.name.removeSuffix(".html")
                val parent = relativeFile.parent?.let { "$it/" } ?: ""
                if (name != "index") {
                    get("/$parent$name") {
                        call.respondFile(file)
                    }
                } else {
                    get("/$parent") {
                        call.respondFile(file)
                    }
                }
            }

            // Anything not found is an error
            val errorFile = siteRootFile.resolve("404.html")
            if (errorFile.exists()) {
                // Catch URLs of the form a/b/c/
                get("{...}/") {
                    call.respondFile(errorFile)
                }

                // Catch URLs of the form a/b/c/slug
                get("{...}") {
                    call.respondFile(errorFile)
                }
            }
        }
    }
}