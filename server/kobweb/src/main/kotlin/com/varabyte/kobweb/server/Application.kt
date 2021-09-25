package com.varabyte.kobweb.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.varabyte.kobweb.server.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureHTTP()
        configureSecurity()
    }.start(wait = true)
}