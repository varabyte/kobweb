package com.varabyte.kobweb.gradle.application.buildservices

import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FailureResult
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import java.io.File

/**
 * A build service which listens to task states and communicates information to a running Kobweb server about them
 */
abstract class KobwebTaskListener : BuildService<KobwebTaskListener.Parameters>, OperationCompletionListener {
    interface Parameters : BuildServiceParameters {
        var kobwebStartTaskName: String

        // take a `File` instead of a `Path` as parameters must be serializable
        var kobwebFolderFile: File
    }

    val kobwebFolder = KobwebFolder(parameters.kobwebFolderFile.toPath())
    var isBuilding = false

    // NOTE: When a project uses an `includeBuild` for a plugin that needs to be built only after the kobweb application
    // plugin is applied, the tasks for building that plugin will be caught by this TaskListener, and thus the code
    // below which reads the server `state.yaml` file will run during the project's configuration phase. This will
    // cause the file to be considered a configuration cache input, which is not ideal, as changes to the file will
    // invalidate the configuration cache, even though there is no need to do so. To the best of our knowledge, there
    // are no Gradle APIs to avoid detecting the file as a configuration cache input. However, one workaround is to
    // ensure the `includeBuild` plugin(s) are built earlier than project configuration by including them in the root
    // build script (e.g. `my-project/build.gradle.kts`) `plugins {}` declaration:
    // plugins {
    //     alias(libs.plugins.kotlin.multiplatform) apply false
    //     id("<includeBuild plugin>") apply false
    // }
    // Warning: The above approach may cause unnecessary builds depending on project setup, use at your own discretion.
    var isServerRunning = ServerStateFile(kobwebFolder).content?.isRunning() ?: false

    override fun onFinish(event: FinishEvent) {
        val taskName = event.descriptor.name.substringAfterLast(":")
        val taskFailed = event.result is FailureResult

        if (isServerRunning) {
            val serverRequestsFile = ServerRequestsFile(kobwebFolder)
            if (taskFailed) {
                serverRequestsFile.enqueueRequest(
                    ServerRequest.SetStatus(
                        "Failed.",
                        isError = true,
                        timeoutMs = 500
                    )
                )
            } else {
                if (taskName == parameters.kobwebStartTaskName) {
                    serverRequestsFile.enqueueRequest(ServerRequest.ClearStatus())
                    serverRequestsFile.enqueueRequest(ServerRequest.IncrementVersion())
                } else if (!isBuilding) {
                    serverRequestsFile.enqueueRequest(ServerRequest.SetStatus("Building..."))
                    isBuilding = true
                }
            }
        } else {
            if (!taskFailed && taskName == parameters.kobwebStartTaskName) {
                isServerRunning = true
            }
        }
    }
}
