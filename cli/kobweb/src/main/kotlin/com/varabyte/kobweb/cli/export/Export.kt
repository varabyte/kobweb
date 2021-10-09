package com.varabyte.kobweb.cli.export

import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.consumeProcessOutput
import com.varabyte.kobweb.cli.common.kobwebProject
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.input.Keys
import com.varabyte.konsole.foundation.input.onKeyPressed
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.text.textLine
import com.varabyte.konsole.foundation.text.yellow
import kotlinx.coroutines.delay

private enum class ExportState {
    EXPORTING,
    FINISHING,
    FINISHED,
    CANCELLING,
    CANCELLED,
}

@Suppress("BlockingMethodInNonBlockingContext")
fun handleExport() = konsoleApp {
    kobwebProject ?: return@konsoleApp

    newline() // Put space between user prompt and eventual first line of Gradle output

    // Useful to add space below output Gradle text and our main block
    var shouldAddNewline by konsoleVarOf(false)
    var exportState by konsoleVarOf(ExportState.EXPORTING)
    lateinit var serverState: ServerState // Set if ExportState ever hits ExportState.EXPORTING; otherwise, don't use!

    var cancelReason by konsoleVarOf("")
    val ellipsis = konsoleAnimOf(Anims.ELLIPSIS)
    konsole {
        if (shouldAddNewline) textLine()
        when (exportState) {
            ExportState.EXPORTING -> textLine("Exporting$ellipsis")
            ExportState.FINISHING -> textLine("Cleaning up$ellipsis")
            ExportState.FINISHED -> textLine("Export finished successfully")
            ExportState.CANCELLING -> yellow { textLine("Cancelling export: $cancelReason$ellipsis") }
            ExportState.CANCELLED -> yellow { textLine("Export cancelled: $cancelReason") }
        }
    }.run {
        val exportProcess = Runtime.getRuntime()
            .exec(arrayOf("./gradlew", "-PkobwebReuseServer=false", "kobwebExport"))
        consumeProcessOutput(exportProcess) { shouldAddNewline = true }

        onKeyPressed {
            if (exportState == ExportState.EXPORTING && key == Keys.Q) {
                cancelReason = "User requested cancellation"
                exportProcess.destroy()
                exportState = ExportState.CANCELLING
            }
        }

        while (exportProcess.isAlive) {
            delay(300)
        }

        if (exportProcess.exitValue() != 0) {
            if (exportState != ExportState.CANCELLING) {
                cancelReason =
                    "Server failed to build. Please check Gradle output and fix the errors before continuing."
                exportState = ExportState.CANCELLING
            }
        }

        if (exportState == ExportState.EXPORTING) {
            exportState = ExportState.FINISHING
        }
        check(exportState in listOf(ExportState.FINISHING, ExportState.CANCELLING))

        val stopProcess = Runtime.getRuntime().exec(arrayOf("./gradlew", "kobwebStop"))
        consumeProcessOutput(exportProcess) { shouldAddNewline = true }
        stopProcess.waitFor()

        exportState = if (exportState == ExportState.FINISHING) ExportState.FINISHED else ExportState.CANCELLED
    }
}