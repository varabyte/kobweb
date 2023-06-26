package com.varabyte.kobweb.gradle.application.buildservices

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A build service which listens to task states and communicates information to a running Kobweb server about them
 */
abstract class KobwebTaskListener : BuildService<BuildServiceParameters.None>, OperationCompletionListener,
    AutoCloseable {
    val onFinishCallbacks = CopyOnWriteArrayList<(FinishEvent) -> Unit>()

    override fun onFinish(event: FinishEvent) {
        onFinishCallbacks.forEach { it.invoke(event) }
    }

    override fun close() {
        onFinishCallbacks.clear()
    }
}
