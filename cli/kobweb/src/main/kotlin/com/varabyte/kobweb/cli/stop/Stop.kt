package com.varabyte.kobweb.cli.stop

import com.varabyte.kobweb.cli.common.Anims
import com.varabyte.kobweb.cli.common.KobwebGradle
import com.varabyte.kobweb.cli.common.assertKobwebProject
import com.varabyte.kobweb.cli.common.consumeProcessOutput
import com.varabyte.kobweb.cli.common.handleConsoleOutput
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kotter.foundation.anim.textAnimOf
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.textLine

private enum class StopState {
    STOPPING,
    STOPPED,
}

fun handleStop(
    isInteractive: Boolean
) {
    assertKobwebProject()
    // Server environment doesn't really matter for "stop". Still, let's default to prod because that's usually the case
    // where a server is left running for a long time.
    val kobwebGradle = KobwebGradle(ServerEnvironment.PROD)

    if (isInteractive) session {
        newline() // Put space between user prompt and eventual first line of Gradle output

        val ellipsisAnim = textAnimOf(Anims.ELLIPSIS)
        var stopState by liveVarOf(StopState.STOPPING)
        section {
            textLine() // Add text line between this block and Gradle output above

            when (stopState) {
                StopState.STOPPING -> {
                    textLine("Stopping a Kobweb server$ellipsisAnim")
                }
                StopState.STOPPED -> {
                    textLine("Server stopped gracefully (or couldn't find one that was running).")
                }
            }
        }.run {
            val stopServerProcess = kobwebGradle.stopServer()
            stopServerProcess.consumeProcessOutput(::handleConsoleOutput)
            stopServerProcess.waitFor()
            stopState = StopState.STOPPED
        }
    }
    else {
        kobwebGradle.stopServer().also { it.consumeProcessOutput(); it.waitFor() }
    }
}