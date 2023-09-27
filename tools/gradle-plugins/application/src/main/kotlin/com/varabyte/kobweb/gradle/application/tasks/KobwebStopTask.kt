package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.util.toDisplayText
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.time.Duration

private val STOP_TIMEOUT_MS = Duration.ofSeconds(10).toMillis()

/**
 * Stop a Kobweb web server started by the [KobwebStartTask].
 *
 * This task will block until it can confirm the server is no longer running.
 */
abstract class KobwebStopTask : KobwebTask("Stop a Kobweb server if one is running") {
    @TaskAction
    fun execute() {
        val kobwebFolder = kobwebApplication.kobwebFolder
        val stateFile = ServerStateFile(kobwebFolder)
        stateFile.content?.let { serverState ->
            ProcessHandle.of(serverState.pid).ifPresent { processHandle ->
                val requestsFile = ServerRequestsFile(kobwebFolder)
                requestsFile.enqueueRequest(ServerRequest.Stop())

                val startTime = System.currentTimeMillis()
                while (stateFile.content != null) {
                    Thread.sleep(300)
                    if (System.currentTimeMillis() - startTime >= STOP_TIMEOUT_MS) {
                        println("A Kobweb server running at ${serverState.toDisplayText()} is taking longer than expected to shut down. Attempting to force stop it...")
                        processHandle.destroyForcibly()
                        // This may be overkill, but let's make sure it's really dead before continuing. This should
                        // barely block for any at all, as far as I'm aware.
                        processHandle.onExit().get()
                        break
                    }
                }
                println("A Kobweb server running at ${serverState.toDisplayText()} was stopped")
            }

            // Occasionally a stale file can get left over from a previous server crash or from being forcibly killed
            Files.deleteIfExists(stateFile.path)
        }
    }
}
