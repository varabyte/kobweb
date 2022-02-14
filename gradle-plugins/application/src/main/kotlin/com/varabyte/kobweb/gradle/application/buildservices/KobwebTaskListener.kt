package com.varabyte.kobweb.gradle.application.buildservices

import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FailureResult
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener

/**
 * A build service which listens to task states and communicates information to a running Kobweb server about them
 */
abstract class KobwebTaskListener : BuildService<KobwebTaskListener.Parameters>, OperationCompletionListener {

    // I think we have to pass this in directly? If I try to use parameters, I get a serialization error
    lateinit var kobwebFolder: KobwebFolder

    interface Parameters : BuildServiceParameters {
        val genSiteTasks: ListProperty<String>
        val startSiteTasks: ListProperty<String>
    }

    override fun onFinish(event: FinishEvent) {
        val taskName = event.descriptor.name.substringAfterLast(":")
        val serverRequestsFile = ServerRequestsFile(kobwebFolder)
        if (parameters.genSiteTasks.get().any { it == taskName }) {
            serverRequestsFile.enqueueRequest(ServerRequest.SetStatus("Building..."))
        }

        if (event.result is FailureResult) {
            serverRequestsFile.enqueueRequest(
                ServerRequest.SetStatus(
                    "Failed.",
                    isError = true,
                    timeoutMs = 500
                )
            )
        }
        else {
            if (parameters.startSiteTasks.get().any { it == taskName }) {
                serverRequestsFile.enqueueRequest(ServerRequest.ClearStatus())
                serverRequestsFile.enqueueRequest(ServerRequest.IncrementVersion())
            }
        }
    }
}
