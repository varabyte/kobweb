package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.api.EMPTY_PAYLOAD
import com.varabyte.kobweb.api.HttpMethod
import com.varabyte.kobweb.api.Request
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
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

/** Somewhat uniqueish parameter key name so it's unlikely to clash with anything a user would choose by chance. */
private const val KOBWEB_PARAMS = "kobweb-params"

fun Application.configureRouting(env: ServerEnvironment, conf: KobwebConf, globals: ServerGlobals) {
    when (env) {
        ServerEnvironment.DEV -> configureDevRouting(conf, globals)
        ServerEnvironment.PROD -> configureProdRouting(conf)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleApiCall(
    apiJar: ApiJarFile,
    httpMethod: HttpMethod,
) {
    call.parameters[KOBWEB_PARAMS]?.takeIf { it.isNotBlank() }?.let { pathStr ->
        val body: ByteArray? = when(httpMethod) {
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
                response.payload?.takeIf { httpMethod != HttpMethod.HEAD } ?: EMPTY_PAYLOAD,
                status = HttpStatusCode.fromValue(response.status),
                contentType = response.contentType?.takeIf { httpMethod != HttpMethod.HEAD }?.let { ContentType.parse(it) }
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

// Note: This should be defined LAST in the routing { ... } block.
private fun Routing.configureCatchAllRouting(script: Path, index: Path) {
    val scriptMap = Path("$script.map")
    get("{$KOBWEB_PARAMS...}") {
        var handled = false
        call.parameters[KOBWEB_PARAMS]?.let {
            val path = it.split("/")
            if (path.isNotEmpty()) {
                handled = true
                when (path.last()) {
                    script.name -> call.respondFile(script.toFile())
                    scriptMap.name -> call.respondFile(scriptMap.toFile())
                    else -> handled = false
                }
            }
        }

        // If unhandled at this point, we probably want to serve an error page. The index file (plus script) should have
        // the logic which does this.
        if (!handled) {
            call.respondFile(index.toFile())
        }
    }

}

private fun Application.configureDevRouting(conf: KobwebConf, globals: ServerGlobals) {
    val script = Path(conf.server.files.dev.script)
    val contentRoot = Path(conf.server.files.dev.contentRoot)
    val dataRoot = conf.server.files.dev.dataRoot
        .takeIf { it.isNotBlank() }
        ?.let { Path(it).also { path -> path.toFile().mkdirs() } }
        ?: Files.createTempDirectory("kobweb-server-data").also { it.toFile().deleteOnExit() }
    val apiJar = conf.server.files.dev.api.takeIf { it.isNotBlank() }?.let { ApiJarFile(Path(it), dataRoot) }

    routing {
        get("/api/kobweb/version") {
            call.respondText(globals.version.toString())
        }
        get("/api/kobweb/status") {
            call.respond(mapOf("text" to globals.status.orEmpty(), "isError" to globals.isStatusError.toString()))
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

private fun Application.configureProdRouting(conf: KobwebConf) {
    val siteRoot = Path(conf.server.files.prod.siteRoot)
    val systemRoot = siteRoot.resolve("system")
    val resourcesRoot = siteRoot.resolve("resources")
    val pagesRoot = siteRoot.resolve("pages")
    val dataRoot = siteRoot.resolve("data")

    val script = systemRoot.resolve(conf.server.files.dev.script.substringAfterLast("/"))
    val fallbackIndex = systemRoot.resolve("index.html")
    val apiJar = conf.server.files.dev.api.substringAfterLast("/")
        .takeIf { it.isNotBlank() }
        ?.let { ApiJarFile(systemRoot.resolve(it), dataRoot) }

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
                }
                else {
                    get("/${relativeFile.parent}") {
                        call.respondFile(file)
                    }
                }
            }
        }

        configureCatchAllRouting(script, fallbackIndex)
    }
}