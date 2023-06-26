package com.varabyte.kobweb.server

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.io.ServerStateFile
import com.varabyte.kobweb.server.plugin.KobwebServerPlugin
import com.varabyte.kobweb.server.plugins.configureHTTP
import com.varabyte.kobweb.server.plugins.configureRouting
import com.varabyte.kobweb.server.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.ServerSocket
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.system.exitProcess

private fun isPortInUse(port: Int): Boolean {
    try {
        ServerSocket(port).use {
            return false
        }
    } catch (ex: IOException) {
        return true
    }
}

fun main(): Unit = runBlocking {
    val folder = KobwebFolder.inWorkingDirectory()
        ?: throw KobwebException("Server must be started in the root of a Kobweb project")

    val confFile = KobwebConfFile(folder)
    val conf = confFile.content
        ?: throw KobwebException("Server cannot start without configuration: ${confFile.path} is missing")

    val env = ServerEnvironment.get()

    with(conf.server.logging) {
        val logRootPath = Path(logRoot)
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
        System.setProperty(
            "LOG_MAX_HISTORY",
            maxFileCount?.takeIf { it > 0 }?.toString()
                ?: if (totalSizeCap?.takeIf { it.inWholeBytes > 0 } == null) "0" else Int.MAX_VALUE.toString())
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
    } else {
        assert(env == ServerEnvironment.PROD)
        if (isPortInUse(port)) {
            throw KobwebException("Production server can't start as port $port is already occupied. If you need a different port number, consider modifying ${confFile.path}")
        }
    }

    // Prepare classloader for plugin jars
    val pluginClassloader = URLClassLoader(
        try {
            folder.resolve("server/plugins")
                .listDirectoryEntries("*.jar")
                .map { it.toUri().toURL() }
                .toTypedArray()
        } catch (_: IOException) {
            emptyArray()
        },
    )

    val globals = ServerGlobals()
    val siteLayout = SiteLayout.get()
    val engine = embeddedServer(Netty, port) {
        configureRouting(env, siteLayout, conf, globals)
        configureSerialization()
        configureHTTP(conf)

        val loader = ServiceLoader.load(KobwebServerPlugin::class.java, pluginClassloader)
        loader.forEach { kobwebServerPlugin -> kobwebServerPlugin.configure(this) }
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


    val log = engine.application.log // Extract out a reference to the logger before we kill the engine
    log.info("Kobweb server shutting down...")
    engine.stop(1, 5, TimeUnit.SECONDS)
    requestsFile.path.deleteIfExists()
    stateFile.content = null
    log.info("Server finished shutting down.")

    // Hack??? Somehow, due to the hanging get for streaming Kobweb status events in dev mode, occasionally the server
    // won't finish exiting. It seems like something about our coroutines is keeping the process alive. To repro:
    //  * Remove / comment out the exitProcess call below
    //  * Use `kobweb run` in a Kobweb project
    //  * When the URL comes up (e.g. http://localhost:8080), ctrl click on it many times (5-6?)
    //  * While the pages are opening up, press Q to start canceling the server
    //  * Use jps to see that the server process is still alive
    // I'm not sure if I'm doing anything wrong with how I'm handling stream events, but it seems like `engine.stop`
    // above doesn't finish cleaning up some live coroutine resource if things are still being cancelled while they are
    // shutting down. This, in turn, leaves the process alive (which keeps the log file open too).
    // Exiting may be a hack, but worst case it does nothing and best case it helps us avoid a hanging process that may
    // or may not be caused by a bug in ktor.
    exitProcess(0)
}
