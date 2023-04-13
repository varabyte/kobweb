package com.varabyte.kobweb.server

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.*
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
import kotlin.io.path.*

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

    val env = ServerEnvironment.get()

    with(conf.server.logging) {
        val logRootPath = Path("").resolve(logRoot)
        if (clearLogsOnStart && logRootPath.exists()) {
            logRootPath.listDirectoryEntries().forEach { child ->
                try {
                    child.deleteExisting()
                } catch (_: IOException) {
                    println("Failed to delete file: ${child.absolutePathString()}")
                }
            }
        }

        System.setProperty("LOG_LEVEL", level.name)
        System.setProperty("LOG_DEST", logRootPath.absolutePathString())
        System.setProperty("LOG_NAME", logFileBaseName)
        System.setProperty("LOG_SUFFIX", ".log")
        System.setProperty("LOG_ROLLOVER_SUFFIX", if (compressHistory) ".gz" else ".log")
        // For some reason, logback ignores size capacity if max files are left unbounded. This doesn't make sense in my
        // opinion -- it can be useful to have unbounded files but still have a size cap. So we trick logback here if
        // the user sets a size cap but not a max file count.
        System.setProperty("LOG_MAX_HISTORY", maxFileCount?.takeIf { it > 0 }?.toString() ?: if (totalSizeCap?.takeIf { it.inWholeBytes > 0 } == null) "0" else Int.MAX_VALUE.toString())
        System.setProperty("LOG_SIZE_CAP", totalSizeCap?.inWholeBytes?.toString() ?: "0")
    }

    val stateFile = ServerStateFile(folder)
    stateFile.content?.let { serverState ->
        if (ProcessHandle.of(serverState.pid).isPresent) {
            throw KobwebException("Server cannot start as one is already running at http://localhost:${serverState.port} with PID ${serverState.pid}")
        }
        stateFile.content = null
    }

    var port = conf.server.port
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