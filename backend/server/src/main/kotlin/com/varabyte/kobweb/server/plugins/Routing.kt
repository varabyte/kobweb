package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.common.conf.KobwebConf
import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.server.ServerGlobals
import com.varabyte.kobweb.server.api.ServerEnvironment
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.io.path.Path
import kotlin.io.path.name

fun Application.configureRouting(env: ServerEnvironment, conf: KobwebConf, globals: ServerGlobals) {
    when (env) {
        ServerEnvironment.DEV -> configureDevRouting(conf, globals)
        ServerEnvironment.PROD -> throw KobwebException("PROD env is not yet supported") // Very temporary
    }
}

private fun Application.configureDevRouting(conf: KobwebConf, globals: ServerGlobals) {
    // TODO: Create our own in-memory copy of the script with the kobweb hook updated so we show a loading spinner
    val script = Path(conf.server.files.dev.script)
    val scriptMap = Path(conf.server.files.dev.script + ".map")
    val contentRoot = Path(conf.server.files.dev.contentRoot)

    routing {
        get("/api/kobweb/version") {
            call.respondText(globals.version.toString())
        }
        get("/api/kobweb/status") {
            call.respondText(globals.status.orEmpty())
        }
        val contentRootFile = contentRoot.toFile()
        contentRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
            get("/${file.relativeTo(contentRootFile)}") {
                call.respondFile(file)
            }
        }
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

            if (!handled) {
                call.respondFile(contentRoot.resolve("index.html").toFile())
            }
        }
    }
}