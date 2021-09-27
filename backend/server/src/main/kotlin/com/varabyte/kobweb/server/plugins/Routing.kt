package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.common.conf.KobwebConf
import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.server.api.ServerEnvironment
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.io.path.Path
import kotlin.io.path.name

fun Application.configureRouting(env: ServerEnvironment, conf: KobwebConf) {
    when (env) {
        ServerEnvironment.DEV -> configureDevRouting(conf)
        ServerEnvironment.PROD -> throw KobwebException("PROD env is not yet supported") // Very temporary
    }
}

private fun Application.configureDevRouting(conf: KobwebConf) {
    // TODO: Create our own in-memory copy of the script with the kobweb hook updated so we show a loading spinner
    val script = Path(conf.server.locations.dev.script)
    val contentRoot = Path(conf.server.locations.dev.contentRoot)

    routing {
        get("/${script.name}") {
            call.respondFile(script.toFile())
        }
        val contentRootFile = contentRoot.toFile()
        contentRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
            get("/${file.relativeTo(contentRootFile)}") {
                call.respondFile(file)
            }
        }
        get("{...}") {
            call.respondFile(contentRoot.resolve("index.html").toFile())
        }
    }
}