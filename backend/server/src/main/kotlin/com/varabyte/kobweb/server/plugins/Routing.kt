package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.server.ServerGlobals
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.io.ApiJarFile
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

fun Application.configureRouting(env: ServerEnvironment, conf: KobwebConf, globals: ServerGlobals) {
    when (env) {
        ServerEnvironment.DEV -> configureDevRouting(conf, globals)
        ServerEnvironment.PROD -> configureProdRouting(conf)
    }
}

private fun Routing.configureApiRouting(apiJar: ApiJarFile) {
    get("/api/{params...}") {
        call.parameters["params"]?.takeIf { it.isNotBlank() }?.let { pathStr ->
            val response = apiJar.apis.handle("/$pathStr")
            if (response != null) {
                call.respondBytes(
                    response.payload,
                    status = HttpStatusCode.fromValue(response.status),
                    contentType = response.contentType?.let { ContentType.parse(it) }
                )
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

// Note: This should be defined LAST in the routing { ... } block.
private fun Routing.configureCatchAllRouting(script: Path, index: Path) {
    val scriptMap = Path("$script.map")
    get("{params...}") {
        var handled = false
        call.parameters["params"]?.let {
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
    val apiJar = conf.server.files.dev.api.takeIf { it.isNotBlank() }?.let { ApiJarFile(Path(it)) }

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

    val script = systemRoot.resolve(conf.server.files.dev.script.substringAfterLast("/"))
    val fallbackIndex = systemRoot.resolve("index.html")
    val apiJar = conf.server.files.dev.api.substringAfterLast("/")
        .takeIf { it.isNotBlank() }
        ?.let { ApiJarFile(systemRoot.resolve(it)) }

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