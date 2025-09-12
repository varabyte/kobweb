package com.varabyte.kobweb.gradle.application.buildservices

import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FailureResult
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener

/**
 * A build service which listens to task states and communicates information to a running Kobweb server about them.
 */
abstract class KobwebTaskListener : BuildService<KobwebTaskListener.Parameters>, OperationCompletionListener {
    interface Parameters : BuildServiceParameters {
        /** The fully qualified path of the project's `kobwebStart` task, e.g. ":site:kobwebStart". */
        val kobwebStartTaskPath: Property<String>
        val kobwebFolderFile: DirectoryProperty
    }

    val kobwebFolder = KobwebFolder(parameters.kobwebFolderFile.get().asFile.toPath())
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
        val taskFailed = event.result is FailureResult
        val serverRequestsFile = ServerRequestsFile(kobwebFolder)
        val isProjectStartTask = event.descriptor.name == parameters.kobwebStartTaskPath.get()

        if (!taskFailed && !isBuilding) {
            isBuilding = true
            if (isServerRunning) {
                serverRequestsFile.enqueueRequest(ServerRequest.SetStatus("Building..."))
            }
        }

        if (isServerRunning) {
            if (taskFailed) {
                serverRequestsFile.enqueueRequest(
                    ServerRequest.SetStatus("Failed.", isError = true, timeoutMs = 500)
                )
            } else if (isProjectStartTask) {
                serverRequestsFile.enqueueRequest(ServerRequest.ClearStatus())
                serverRequestsFile.enqueueRequest(ServerRequest.IncrementVersion())
            }
        } else if (!taskFailed && isProjectStartTask) {
            isServerRunning = true
        }
    }
}
