package com.varabyte.kobweb.cli.run

import RunEnvironment
import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.informError
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.render.aside
import com.varabyte.konsole.foundation.text.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


fun handleRun(env: RunEnvironment) = konsoleApp {
    val kobwebFolder = KobwebFolder.inWorkingDirectory()
        ?: run {
            informError("This command must be called in the root of a Kobweb project.")
            return@konsoleApp
        }

    val serverStateFile = ServerStateFile(kobwebFolder)

    val ellipsisAnim = konsoleAnimOf(Anims.ELLIPSIS)
    var finished by konsoleVarOf(false)
    konsole {
        textLine()
        if (!finished) {
            text("Starting the Kobweb server$ellipsisAnim")
        }
        else {
            val serverState = serverStateFile.content
            if (serverState != null) {
                text("Kobweb server is serving at http://0.0.0.0:${serverState.port}")
            }
            else {
                textError("Kobweb server failed to start")
            }
        }
    }.
    run {
        @Suppress("BlockingMethodInNonBlockingContext")
        val process = Runtime.getRuntime()
            .exec(arrayOf("./gradlew", if (env == RunEnvironment.DEV) "kobwebStartDev" else "kobwebStartProd"))

        val stdoutProcessor = CoroutineScope(Dispatchers.IO).launch {
            val isr = InputStreamReader(process.inputStream)
            val br = BufferedReader(isr)
            lateinit var line: String
            while (br.readLine().also { line = it } != null) {
                aside {
                    textLine(line)
                }
            }
        }
        val stderrProcessor = CoroutineScope(Dispatchers.IO).launch {
            val isr = InputStreamReader(process.errorStream)
            val br = BufferedReader(isr)
            lateinit var line: String
            while (br.readLine().also { line = it } != null) {
                aside {
                    textLine(line)
                }
            }
        }
        process.waitFor()
        listOf(stdoutProcessor, stderrProcessor).joinAll()

        finished = true
    }
}