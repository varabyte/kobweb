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
 * A class to hold information about a Kobweb project that is being built.
 *
 * @param startTask The fully qualified name of the task that starts the Kobweb server (e.g. `:site:kobwebStart`).
 * @param kobwebFolder The [KobwebFolder] instance representing the project's `.kobweb` folder.
 */
private class KobwebSiteProject(
    val startTask: String,
    val kobwebFolder: KobwebFolder,
) {
    val serverRequestsFile = ServerRequestsFile(kobwebFolder)
}

/**
 * A build service which listens to task states and communicates information to running Kobweb servers about them.
 */
abstract class KobwebTaskListener : BuildService<KobwebTaskListener.Parameters>, OperationCompletionListener {
    interface Parameters : BuildServiceParameters {
        // Use a Map instead of KobwebSiteProject as parameters must be serializable
        /**
         * A map of fully qualified paths of `kobwebStart` tasks with their corresponding Kobweb folder.
         *
         * This should contain exactly the start tasks that will run in the current build.
         */
        var kobwebFolderFiles: Map<String, File>
    }

    // The build service is shared across all projects in the build. Thus, if the user's project has multiple
    // Kobweb sites, potentially running at the same time, it must track each such site so that live reloading
    // can work for all of them
    private val kobwebProjects = parameters.kobwebFolderFiles
        .map { KobwebSiteProject(it.key, KobwebFolder(it.value.toPath())) }
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
    private val runningServerPaths = mutableSetOf<String>().apply {
        kobwebProjects.forEach { project ->
            if (ServerStateFile(project.kobwebFolder).content?.isRunning() == true)
                add(project.startTask)
        }
    }

    override fun onFinish(event: FinishEvent) {
        val taskFailed = event.result is FailureResult
        if (!taskFailed && !isBuilding) {
            isBuilding = true
            kobwebProjects.forEach { project ->
                if (project.startTask in runningServerPaths) {
                    project.serverRequestsFile.enqueueRequest(ServerRequest.SetStatus("Building..."))
                }
            }
        }

        kobwebProjects.forEach { project ->
            // update each project's server status individually so that each site is reloaded only when built
            val isProjectStartTask = event.descriptor.name == project.startTask
            if (project.startTask in runningServerPaths) {
                if (taskFailed) {
                    project.serverRequestsFile.enqueueRequest(
                        ServerRequest.SetStatus("Failed.", isError = true, timeoutMs = 500)
                    )
                } else if (isProjectStartTask) {
                    project.serverRequestsFile.enqueueRequest(ServerRequest.ClearStatus())
                    project.serverRequestsFile.enqueueRequest(ServerRequest.IncrementVersion())
                }
            } else if (!taskFailed && isProjectStartTask) {
                runningServerPaths += project.startTask
            }
        }
    }
}
