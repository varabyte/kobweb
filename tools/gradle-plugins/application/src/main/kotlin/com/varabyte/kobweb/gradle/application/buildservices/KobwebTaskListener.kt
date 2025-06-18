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
        // take a `File` instead of a `Path` as parameters must be serializable
        /**
         * A map of fully qualified paths of `kobwebStart` tasks with their corresponding Kobweb folder.
         *
         * This should only contain tasks that will run in the current build.
         */
        var kobwebFolderFiles: Map<String, File>
    }

    private val kobwebFolders = parameters.kobwebFolderFiles.mapValues { (_, file) -> KobwebFolder(file.toPath()) }
    private var isBuilding = false

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
    private val runningServers = mutableSetOf<String>().apply {
        kobwebFolders.forEach { (startTask, folder) ->
            if (ServerStateFile(folder).content?.isRunning() == true)
                add(startTask)
        }
    }

    override fun onFinish(event: FinishEvent) {
        val taskFailed = event.result is FailureResult
        if (!taskFailed && !isBuilding) {
            isBuilding = true
            kobwebFolders.forEach { (startTask, kobwebFolder) ->
                if (startTask in runningServers) {
                    ServerRequestsFile(kobwebFolder)
                        .enqueueRequest(ServerRequest.SetStatus("Building..."))
                }
            }
        }

        kobwebFolders.forEach { (startTask, kobwebFolder) ->
            val isStartTask = event.descriptor.name == startTask
            if (startTask in runningServers) {
                val serverRequestsFile = ServerRequestsFile(kobwebFolder)
                if (taskFailed) {
                    serverRequestsFile.enqueueRequest(
                        ServerRequest.SetStatus("Failed.", isError = true, timeoutMs = 500)
                    )
                } else if (isStartTask) {
                    serverRequestsFile.enqueueRequest(ServerRequest.ClearStatus())
                    serverRequestsFile.enqueueRequest(ServerRequest.IncrementVersion())
                }
            } else if (!taskFailed && isStartTask) {
                runningServers += startTask
            }
        }
    }
}
