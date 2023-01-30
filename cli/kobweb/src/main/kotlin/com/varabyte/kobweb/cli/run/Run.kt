package com.varabyte.kobweb.cli.run

import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.GradleAlertBundle
import com.varabyte.kobweb.cli.common.KobwebGradle
import com.varabyte.kobweb.cli.common.assertKobwebApplication
import com.varabyte.kobweb.cli.common.assertServerNotAlreadyRunning
import com.varabyte.kobweb.cli.common.findKobwebApplication
import com.varabyte.kobweb.cli.common.handleConsoleOutput
import com.varabyte.kobweb.cli.common.handleGradleOutput
import com.varabyte.kobweb.cli.common.isServerAlreadyRunningFor
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.cli.common.showStaticSiteLayoutWarning
import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.kotter.foundation.anim.textAnimOf
import com.varabyte.kotter.foundation.anim.textLine
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
    val originalEnv = env

    @Suppress("NAME_SHADOWING") // We're intentionally intercepting the original value
    val env = env.takeIf { siteLayout != SiteLayout.STATIC } ?: ServerEnvironment.PROD
    KobwebGradle(env).use { kobwebGradle -> handleRun(originalEnv, env, siteLayout, isInteractive, kobwebGradle) }
}

private fun handleRun(
    originalEnv: ServerEnvironment,
    env: ServerEnvironment,
    siteLayout: SiteLayout,
    isInteractive: Boolean,
    kobwebGradle: KobwebGradle
) {
    if (isInteractive) session {
        val kobwebApplication = findKobwebApplication() ?: return@session
        if (isServerAlreadyRunningFor(kobwebApplication)) return@session

        val kobwebFolder = kobwebApplication.kobwebFolder
        val conf = KobwebConfFile(kobwebFolder).content!!

        newline() // Put space between user prompt and eventual first line of Gradle output

        if (siteLayout == SiteLayout.STATIC) {
            showStaticSiteLayoutWarning()

            if (originalEnv == ServerEnvironment.DEV) {
                section {
                    // Brighten the color to contrast with the warning above
                    yellow(isBright = true) {
                        textLine(
                            """
                            Note: Development mode is not designed to work with static layouts, so the
                            server will run in production mode instead.

                            To avoid seeing this message, use `--env prod` explicitly.
                            """.trimIndent()
                        )
                    }
                    textLine()
                }.run()
            }
        }

        val envName = when (env) {
            ServerEnvironment.DEV -> "development"
            ServerEnvironment.PROD -> "production"
        }
        var serverState: ServerState? = null // Set on and after RunState.RUNNING
        val ellipsisAnim = textAnimOf(Anims.ELLIPSIS)
        var runState by liveVarOf(RunState.STARTING)
        var cancelReason by liveVarOf("")
        var exception by liveVarOf<Exception?>(null) // Set if RunState.INTERRUPTED
        val gradleAlertBundle = GradleAlertBundle(this)
        // If a route prefix is set, we'll add it to the server URL (at which point we'll need to add slash dividers)
        val routePrefix = RoutePrefix(conf.site.routePrefix)
        section {
            textLine() // Add text line between this block and Gradle output above

            when (runState) {
                RunState.STARTING -> {
                    textLine("Starting a Kobweb server ($envName)$ellipsisAnim")
                    textLine()
                    gradleAlertBundle.renderInto(this)
                    textLine("Press Q anytime to cancel.")
                }
                RunState.RUNNING -> {
                    serverState!!.let { serverState ->
                        green {
                            text("Kobweb server ($envName) is running at ")
                            cyan { text("http://localhost:${serverState.port}$routePrefix") }
                        }
                        textLine(" (PID = ${serverState.pid})")
                        textLine()
                        gradleAlertBundle.renderInto(this)
                        textLine("Press Q anytime to stop the server.")
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
                    textLine("The server has stopped.")
                }
                RunState.CANCELLING -> {
                    check(cancelReason.isNotBlank())
                    yellow { textLine("Cancelling: $cancelReason$ellipsisAnim") }
                }
                RunState.CANCELLED -> {
                    yellow { textLine("Cancelled: $cancelReason") }
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
            startServerProcess.lineHandler = { line, isError ->
                handleGradleOutput(line, isError) { alert -> gradleAlertBundle.handleAlert(alert) }
            }

            Runtime.getRuntime().addShutdownHook(Thread {
                if (runState == RunState.RUNNING || runState == RunState.STOPPING) {
                    cancelReason =
                        "CTRL-C received. We kicked off a request to stop the server but we have to exit NOW before waiting for a confirmation."
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
                            startServerProcess.cancel()
                            startServerProcess.waitFor()

                            cancelReason = "User quit before server could finish starting up"
                            runState = RunState.CANCELLED
                            signal()
                        }
                    } else if (runState == RunState.RUNNING) {
                        runState = RunState.STOPPING
                        CoroutineScope(Dispatchers.IO).launch {
                            startServerProcess.cancel()
                            startServerProcess.waitFor()

                            val stopServerProcess = kobwebGradle.stopServer()
                            stopServerProcess.lineHandler = ::handleConsoleOutput
                            stopServerProcess.waitFor()

                            runState = RunState.STOPPED
                            signal()
                        }
                    }
                }
                else {
                    gradleAlertBundle.handleKey(key)
                }
            }

            val serverStateFile = ServerStateFile(kobwebFolder)
            coroutineScope {
                while (runState == RunState.STARTING) {
                    serverStateFile.content?.takeIf { it.isRunning() }?.let {
                        serverState = it
                        runState = RunState.RUNNING
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
        assertKobwebApplication()
            .also { kobwebApplication -> kobwebApplication.assertServerNotAlreadyRunning() }

        // If we're non-interactive, it means we just want to start the Kobweb server and exit without waiting for
        // for any additional changes. (This is essentially used when run in a web server environment)
        kobwebGradle.startServer(enableLiveReloading = false, siteLayout).also { it.waitFor() }
    }
}