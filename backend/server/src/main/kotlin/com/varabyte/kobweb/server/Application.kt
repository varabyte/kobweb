package com.varabyte.kobweb.server

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.kobweb.server.io.ServerStateFile
import com.varabyte.kobweb.server.plugins.configureHTTP
import com.varabyte.kobweb.server.plugins.configureRouting
import com.varabyte.kobweb.server.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.TimeUnit
import kotlin.io.path.deleteIfExists

private fun isPortInUse(port: Int): Boolean {
    try {
        ServerSocket(port).use {
            return false
        }
    } catch (ex: IOException) {
        return true
    }
}

fun main() = runBlocking {
    val folder = KobwebFolder.inWorkingDirectory()
        ?: throw KobwebException("Server must be started in the root of a Kobweb project")

    val confFile = KobwebConfFile(folder)
    val conf = confFile.content
            ?: throw KobwebException("Server cannot start without configuration: ${confFile.path} is missing")

    val stateFile = ServerStateFile(folder)
    stateFile.content?.let { serverState ->
        if (ProcessHandle.of(serverState.pid).isPresent) {
            throw KobwebException("Server cannot start as one is already running at http://localhost:${serverState.port} with PID ${serverState.pid}")
        }
        stateFile.content = null
    }

    var port = conf.server.port
    val env = ServerEnvironment.get()
    if (env == ServerEnvironment.DEV) {
        while (isPortInUse(port)) {
            ++port
        }
    }
    else {
        assert(env == ServerEnvironment.PROD)
        if (isPortInUse(port)) {
            throw KobwebException("Production server can't start as port $port is already occupied. If you need a different port number, consider modifying ${confFile.path}")
        }
    }
    val globals = ServerGlobals()
    val siteLayout = SiteLayout.get()
    val engine = embeddedServer(Netty, port) {
        configureRouting(env, siteLayout, conf, globals)
        configureSerialization()
        configureHTTP(conf)
    }

    engine.start()

    val requestsFile = ServerRequestsFile(folder)
    requestsFile.path.deleteIfExists()

    val serverState = ServerState(
        env,
        port,
        ProcessHandle.current().pid()
    )
    stateFile.content = serverState

    var shouldStop = false
    while (!shouldStop) {
        requestsFile.removeRequests().forEach { request ->
            when (request) {
                is ServerRequest.Stop -> shouldStop = true
                is ServerRequest.IncrementVersion -> globals.version++
                is ServerRequest.SetStatus -> {
                    globals.status = request.message
                    globals.isStatusError = request.isError
                    globals.timeout = request.timeoutMs?.let { System.currentTimeMillis() + it } ?: Long.MAX_VALUE
                }
                is ServerRequest.ClearStatus -> {
                    globals.status = null
                    globals.isStatusError = false
                    globals.timeout = Long.MAX_VALUE
                }
            }
        }
        if (globals.status != null && System.currentTimeMillis() > globals.timeout) {
            requestsFile.enqueueRequest(ServerRequest.ClearStatus())
        }

        delay(300)
    }

    println("Kobweb server shutting down...")
    engine.stop(1, 5, TimeUnit.SECONDS)
    println("Ktor server stopped, cleaning up...")
    requestsFile.path.deleteIfExists()
    stateFile.content = null
    println("Server finished shutting down.")
}