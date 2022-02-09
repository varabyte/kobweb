package com.varabyte.kobweb.cli.run

import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.KobwebGradle
import com.varabyte.kobweb.cli.common.assertKobwebProject
import com.varabyte.kobweb.cli.common.consumeProcessOutput
import com.varabyte.kobweb.cli.common.findKobwebProject
import com.varabyte.kobweb.cli.common.handleConsoleOutput
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.cli.common.showStaticSiteLayoutWarning
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.kotter.foundation.anim.textAnimOf
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.cyan
import com.varabyte.kotter.foundation.text.green
import com.varabyte.kotter.foundation.text.red
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.foundation.timer.addTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

private enum class RunState {
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED,
    CANCELLING,
    CANCELLED,
    INTERRUPTED,
}

fun handleRun(
    env: ServerEnvironment,
    siteLayout: SiteLayout,
    isInteractive: Boolean,
) {
    val kobwebGradle = KobwebGradle(env)
    if (isInteractive) session {
        val kobwebFolder = findKobwebProject()?.kobwebFolder ?: return@session

        newline() // Put space between user prompt and eventual first line of Gradle output

        if (siteLayout == SiteLayout.STATIC) {
            showStaticSiteLayoutWarning()
        }

        val serverStateFile = ServerStateFile(kobwebFolder)

        val envName = when (env) {
            ServerEnvironment.DEV -> "development"
            ServerEnvironment.PROD -> "production"
        }
        var serverState: ServerState? = null // Set on and after RunState.RUNNING
        val ellipsisAnim = textAnimOf(Anims.ELLIPSIS)
        var runState by liveVarOf(RunState.STARTING)
        var cancelReason by liveVarOf("")
        var exception by liveVarOf<Exception?>(null) // Set if RunState.INTERRUPTED
        section {
            textLine() // Add text line between this block and Gradle output above

            when (runState) {
                RunState.STARTING -> {
                    textLine("Starting a Kobweb server ($envName)$ellipsisAnim")
                    textLine()
                    yellow { textLine("This may take a while if it needs to download dependencies.") }
                    textLine()
                    textLine("Press Q anytime to cancel.")
                }
                RunState.RUNNING -> {
                    serverState!!.let { serverState ->
                        green {
                            text("Kobweb server ($envName) is running at ")
                            cyan { text("http://localhost:${serverState.port}") }
                        }
                        textLine(" (PID = ${serverState.pid})")
                        textLine()
                        textLine("Press Q anytime to stop it.")
                    }
                }
                RunState.STOPPING -> {
                    text("Server is stopping")
                    serverState?.let { serverState ->
                        text(" (PID = ${serverState.pid})")
                    }
                    textLine(ellipsisAnim)
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
                RunState.INTERRUPTED -> {
                    red { textLine("Interrupted by exception:") }
                    textLine()
                    textLine(exception!!.stackTraceToString())
                }
            }
        }.runUntilSignal {
            val startServerProcess = try {
                kobwebGradle.startServer(enableLiveReloading = (env == ServerEnvironment.DEV), siteLayout)
            }
            catch (ex: Exception) {
                exception = ex
                runState = RunState.INTERRUPTED
                return@runUntilSignal
            }
            startServerProcess.consumeProcessOutput(::handleConsoleOutput)

            Runtime.getRuntime().addShutdownHook(Thread {
                if (runState == RunState.RUNNING || runState == RunState.STOPPING) {
                    cancelReason =
                        "CTRL-C received. We already kicked off a request to stop the server but we have to exit NOW."
                    runState = RunState.CANCELLED

                    ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.Stop())
                } else {
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

                            val stopServerProcess = kobwebGradle.stopServer()
                            stopServerProcess.consumeProcessOutput(::handleConsoleOutput)
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
                            cancelReason =
                                "A server is already running using a different environment configuration (want = $env, current = ${it.env})"
                            runState = RunState.CANCELLED
                            signal()
                        } else {
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
                        serverState!!.let { serverState ->
                            if (!serverState.isRunning() || serverStateFile.content != serverState) {
                                cancelReason = "It seems like the server was stopped by a separate process."
                                runState = RunState.CANCELLED
                                signal()
                            }
                        }
                    } else {
                        repeat = false
                    }
                }
            }
        }
    } else {
        assert(!isInteractive)
        assertKobwebProject()
        // If we're non-interactive, it means we just want to start the Kobweb server and exit without waiting for
        // for any additional changes. (This is essentially used when run in a web server environment)
        kobwebGradle.startServer(enableLiveReloading = false, siteLayout).also { it.consumeProcessOutput(); it.waitFor() }
    }
}