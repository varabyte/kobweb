package com.varabyte.kobweb.cli.export

import com.varabyte.kobweb.cli.common.*
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kotter.foundation.anim.textAnimOf
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.text.red
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow

private enum class ExportState {
    EXPORTING,
    FINISHING,
    FINISHED,
    CANCELLING,
    CANCELLED,
    INTERRUPTED,
}

fun handleExport(siteLayout: SiteLayout, useAnsi: Boolean) {
    // exporting is a production-only action
    KobwebGradle(ServerEnvironment.PROD).use { kobwebGradle ->
        handleExport(siteLayout, useAnsi, kobwebGradle)
    }
}

private fun handleExport(siteLayout: SiteLayout, useAnsi: Boolean, kobwebGradle: KobwebGradle) {
    var runInPlainMode = !useAnsi

    if (useAnsi && !trySession {
        val kobwebApplication = findKobwebApplication() ?: return@trySession
        if (isServerAlreadyRunningFor(kobwebApplication)) return@trySession

        newline() // Put space between user prompt and eventual first line of Gradle output

        if (siteLayout == SiteLayout.STATIC) {
            showStaticSiteLayoutWarning()
        }

        var exportState by liveVarOf(ExportState.EXPORTING)
        val gradleAlertBundle = GradleAlertBundle(this)

        var cancelReason by liveVarOf("")
        val ellipsis = textAnimOf(Anims.ELLIPSIS)
        var exception by liveVarOf<Exception?>(null) // Set if ExportState.INTERRUPTED
        section {
            textLine() // Add space between this block and Gradle text which will appear above
            gradleAlertBundle.renderInto(this)
            when (exportState) {
                ExportState.EXPORTING -> textLine("Exporting$ellipsis")
                ExportState.FINISHING -> textLine("Finishing up$ellipsis")
                ExportState.FINISHED -> textLine("Export finished successfully")
                ExportState.CANCELLING -> yellow { textLine("Cancelling export: $cancelReason$ellipsis") }
                ExportState.CANCELLED -> yellow { textLine("Export cancelled: $cancelReason") }
                ExportState.INTERRUPTED -> {
                    red { textLine("Interrupted by exception:") }
                    textLine()
                    textLine(exception!!.stackTraceToString())
                }
            }
        }.run {
            val exportProcess = try {
                kobwebGradle.export(siteLayout)
            }
            catch (ex: Exception) {
                exception = ex
                exportState = ExportState.INTERRUPTED
                return@run
            }
            exportProcess.lineHandler = { line, isError ->
                handleGradleOutput(line, isError) { alert -> gradleAlertBundle.handleAlert(alert) }
            }
            exportProcess.onCompleted = { failure ->
                if (failure != null) {
                    if (exportState != ExportState.CANCELLING) {
                        cancelReason =
                            "Server failed to build. Please check Gradle output and fix the errors before retrying."
                        exportState = ExportState.CANCELLING
                    }
                }
            }

            onKeyPressed {
                if (exportState == ExportState.EXPORTING && key == Keys.Q) {
                    cancelReason = "User requested cancellation"
                    exportProcess.cancel()
                    exportState = ExportState.CANCELLING
                }
                else {
                    gradleAlertBundle.handleKey(key)
                }
            }

            exportProcess.waitFor()
            if (exportState == ExportState.EXPORTING) {
                exportState = ExportState.FINISHING
            }
            check(exportState in listOf(ExportState.FINISHING, ExportState.CANCELLING))

            val stopProcess = kobwebGradle.stopServer()
            stopProcess.lineHandler = ::handleConsoleOutput
            stopProcess.waitFor()

            exportState = if (exportState == ExportState.FINISHING) ExportState.FINISHED else ExportState.CANCELLED
        }
    }) {
        warnFallingBackToPlainText()
        runInPlainMode = true
    }

    if (runInPlainMode) {
        assertKobwebApplication()
            .also { kobwebApplication -> kobwebApplication.assertServerNotAlreadyRunning() }

        kobwebGradle.export(siteLayout).also { it.waitFor() }
        kobwebGradle.stopServer().also { it.waitFor() }
    }
}