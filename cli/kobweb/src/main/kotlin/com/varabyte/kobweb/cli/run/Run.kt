package com.varabyte.kobweb.cli.run

import RunEnvironment
import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.informError
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.input.Keys
import com.varabyte.konsole.foundation.input.onKeyPressed
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.render.aside
import com.varabyte.konsole.foundation.runUntilSignal
import com.varabyte.konsole.foundation.text.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

sealed interface RunState {
    object STARTING : RunState
    object FAILED_TO_START : RunState
    class RUNNING(val serverState: ServerState) : RunState
    object STOPPING_GRACEFULLY : RunState
    object STOPPING_VIA_INTERRUPT : RunState
    object STOPPED : RunState
}

fun handleRun(env: RunEnvironment) = konsoleApp {
    val kobwebFolder = KobwebFolder.inWorkingDirectory()
        ?: run {
            informError("This command must be called in the root of a Kobweb project.")
            return@konsoleApp
        }

    newline() // Put space above first line of Gradle output

    val serverStateFile = ServerStateFile(kobwebFolder)

    val envName = when(env) {
        RunEnvironment.DEV -> "development"
        RunEnvironment.PROD -> "production"
    }
    val ellipsisAnim = konsoleAnimOf(Anims.ELLIPSIS)
    var runState by konsoleVarOf<RunState>(RunState.STARTING)
    konsole {
        textLine()
        when (runState) {
            is RunState.STARTING -> {
                textLine("Starting a Kobweb server ($envName)$ellipsisAnim")
            }
            is RunState.FAILED_TO_START -> {
                textError("Kobweb server failed to start")
            }
            is RunState.RUNNING -> {
                val serverState = (runState as RunState.RUNNING).serverState
                green {
                    text("Kobweb server ($envName) is running at ")
                    cyan { text("http://0.0.0.0:${serverState.port}") }
                }
                textLine(" (PID = ${serverState.pid})")
                textLine()
                textLine("Press Q anytime to stop it.")
            }
            is RunState.STOPPING_GRACEFULLY -> {
                textLine("Server is stopping$ellipsisAnim")
            }
            is RunState.STOPPED -> {
                textLine("Server stopped gracefully.")
            }
            is RunState.STOPPING_VIA_INTERRUPT -> {
                yellow { textLine("CTRL-C received. Kicking off a request to stop the server but we have to exit NOW.") }
            }
        }
    }.runUntilSignal {
        fun consumeStream(stream: InputStream) {
            val isr = InputStreamReader(stream)
            val br = BufferedReader(isr)
            lateinit var line: String
            while (br.readLine().also { line = it } != null) {
                aside {
                    black(isBright = true) {
                        textLine(line)
                    }
                }
            }
        }

        run {
            @Suppress("BlockingMethodInNonBlockingContext")
            val process = Runtime.getRuntime()
                .exec(arrayOf("./gradlew", if (env == RunEnvironment.DEV) "kobwebStartDev" else "kobwebStartProd"))

            CoroutineScope(Dispatchers.IO).launch { consumeStream(process.inputStream) }
            CoroutineScope(Dispatchers.IO).launch { consumeStream(process.errorStream) }

            process.waitFor()
        }

        val serverState = serverStateFile.content
        if (serverState != null) {
            runState = RunState.RUNNING(serverState)
        }
        else {
            runState = RunState.FAILED_TO_START
            signal()
        }

        onKeyPressed {
            if (key == Keys.Q && runState is RunState.RUNNING) {
                runState = RunState.STOPPING_GRACEFULLY
                CoroutineScope(Dispatchers.IO).launch {
                    val process = Runtime.getRuntime().exec(arrayOf("./gradlew", "kobwebStop"))
                    CoroutineScope(Dispatchers.IO).launch { consumeStream(process.inputStream) }
                    CoroutineScope(Dispatchers.IO).launch { consumeStream(process.errorStream) }
                    process.waitFor()

                    runState = RunState.STOPPED
                    signal()
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            if (runState is RunState.RUNNING) {
                runState = RunState.STOPPING_VIA_INTERRUPT

                val process = Runtime.getRuntime().exec(arrayOf("./gradlew", "kobwebStop"))
                process.waitFor()

                signal()
            }
        })
    }
}