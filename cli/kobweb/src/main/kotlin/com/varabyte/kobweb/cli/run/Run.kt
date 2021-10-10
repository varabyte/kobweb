package com.varabyte.kobweb.cli.run

import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.consumeProcessOutput
import com.varabyte.kobweb.cli.common.kobwebFolder
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.server.api.*
import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.input.Keys
import com.varabyte.konsole.foundation.input.onKeyPressed
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.runUntilSignal
import com.varabyte.konsole.foundation.text.*
import com.varabyte.konsole.foundation.timer.addTimer
import kotlinx.coroutines.*
import java.time.Duration

private enum class RunState {
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED,
    CANCELLING,
    CANCELLED,
}

fun handleRun(env: ServerEnvironment) = konsoleApp {
    val kobwebFolder = kobwebFolder ?: return@konsoleApp

    newline() // Put space between user prompt and eventual first line of Gradle output

    val serverStateFile = ServerStateFile(kobwebFolder)

    val envName = when (env) {
        ServerEnvironment.DEV -> "development"
        ServerEnvironment.PROD -> "production"
    }
    lateinit var serverState: ServerState // Set if RunState ever hits RunState.RUNNING; otherwise, don't use!
    val ellipsisAnim = konsoleAnimOf(Anims.ELLIPSIS)
    var runState by konsoleVarOf(RunState.STARTING)
    var cancelReason by konsoleVarOf("")
    var addOutputSeparator by konsoleVarOf(false) // Separate active block from Gradle output above if any
    konsole {
        if (addOutputSeparator) {
            textLine()
        }

        when (runState) {
            RunState.STARTING -> {
                textLine("Starting a Kobweb server ($envName)$ellipsisAnim")
                textLine()
                textLine("Press Q anytime to cancel.")
            }
            RunState.RUNNING -> {
                green {
                    text("Kobweb server ($envName) is running at ")
                    cyan { text("http://localhost:${serverState.port}") }
                }
                textLine(" (PID = ${serverState.pid})")
                textLine()
                textLine("Press Q anytime to stop it.")
            }
            RunState.STOPPING -> {
                textLine("Server is stopping$ellipsisAnim")
            }
            RunState.STOPPED -> {
                textLine("Server stopped gracefully.")
            }
            RunState.CANCELLING -> {
                check(cancelReason.isNotBlank())
                yellow { textLine("Server startup cancelling: $cancelReason$ellipsisAnim") }
            }
            RunState.CANCELLED -> {
                yellow { textLine("Server startup cancelled: $cancelReason") }
            }
        }
    }.runUntilSignal {
        @Suppress("BlockingMethodInNonBlockingContext")
        val args = mutableListOf("./gradlew", "-PkobwebEnv=$env", "kobwebStart")
        if (env == ServerEnvironment.DEV) {
            args.add("-t") // Enable live reloading only while in dev mode
        }
        val startServerProcess = Runtime.getRuntime().exec(args.toTypedArray())
        consumeProcessOutput(startServerProcess) { addOutputSeparator = true }

        Runtime.getRuntime().addShutdownHook(Thread {
            if (runState == RunState.RUNNING || runState == RunState.STOPPING) {
                cancelReason = "CTRL-C received. We already kicked off a request to stop the server but we have to exit NOW."
                runState = RunState.CANCELLED

                ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.Stop())
            }
            else {
                cancelReason = "CTRL-C received. Server startup cancelled."
                runState = RunState.CANCELLED
            }
            signal()
        })

        onKeyPressed {
            if (key in listOf(Keys.EOF, Keys.Q)) {
                if (runState == RunState.STARTING) {
                    runState = RunState.STOPPING
                    CoroutineScope(Dispatchers.IO).launch {
                        startServerProcess.destroy()
                        startServerProcess.waitFor()

                        cancelReason = "User quit before server could finish starting up"
                        runState = RunState.CANCELLED
                        signal()
                    }
                } else if (runState == RunState.RUNNING) {
                    runState = RunState.STOPPING
                    CoroutineScope(Dispatchers.IO).launch {
                        startServerProcess.destroy()
                        startServerProcess.waitFor()

                        val stopServerProcess = Runtime.getRuntime().exec(arrayOf("./gradlew", "kobwebStop"))
                        consumeProcessOutput(stopServerProcess) { addOutputSeparator = true }
                        stopServerProcess.waitFor()

                        runState = RunState.STOPPED
                        signal()
                    }
                }
            }
        }

        coroutineScope {
            while (runState == RunState.STARTING) {
                serverStateFile.content?.takeIf { it.isRunning() }?.let {
                    if (it.env != env) {
                        cancelReason = "A server is already running using a different environment configuration (want = $env, current = ${it.env})"
                        runState = RunState.CANCELLED
                        signal()
                    }
                    else {
                        serverState = it
                        runState = RunState.RUNNING
                    }
                    return@coroutineScope
                }
                delay(300)
            }
        }

        if (runState == RunState.RUNNING) {
            addTimer(Duration.ofMillis(500), repeat = true) {
                if (runState == RunState.RUNNING) {
                    if (!serverState.isRunning() || serverStateFile.content != serverState) {
                        cancelReason = "It seems like the server was stopped by a separate process."
                        runState = RunState.CANCELLED
                        signal()
                    }
                } else {
                    repeat = false
                }
            }
        }
    }
}