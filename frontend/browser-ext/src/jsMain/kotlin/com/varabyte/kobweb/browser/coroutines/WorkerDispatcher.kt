package com.varabyte.kobweb.browser.coroutines

import kotlinx.coroutines.*
import org.w3c.dom.*
import kotlin.coroutines.*

//----------------------------------
// This code is inspired by
// https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/js/src/Window.kt
//----------------------------------

fun WorkerGlobalScope.asCoroutineDispatcher(): CoroutineDispatcher =
    @Suppress("UnsafeCastFromDynamic")
    asDynamic().coroutineDispatcher ?: WorkerDispatcher(this).also { asDynamic().coroutineDispatcher = it }

@OptIn(InternalCoroutinesApi::class)
private class WorkerDispatcher(private val workerScope: WorkerGlobalScope) : CoroutineDispatcher(), Delay {
    private val queue = WorkerMessageQueue()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.enqueue(block)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val handle = workerScope.setTimeout({
            with(continuation) { resumeUndispatched(Unit) }
        }, timeMillis.cappedAsInt)
        continuation.invokeOnCancellation { workerScope.clearTimeout(handle) }
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
        val handle = workerScope.setTimeout({ block.run() }, timeMillis.cappedAsInt)
        return DisposableHandle { workerScope.clearTimeout(handle) }
    }

    private val Long.cappedAsInt get() = this.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    override fun toString(): String = "WorkerDispatcher($workerScope)"
}

/**
 * Backing Queue using a MessageChannel to bypass the 4ms setTimeout limit in the worker.
 */
private class WorkerMessageQueue : Runnable {
    private val queue = ArrayDeque<Runnable>()
    private var scheduled = false

    // A message channel used as a fancy way to signal when work is done
    private val channel = MessageChannel()

    init {
        // Triggered by `schedule`
        channel.port1.onmessage = { run() }
    }

    fun enqueue(element: Runnable) {
        queue.addLast(element)
        if (!scheduled) {
            scheduled = true
            schedule()
        }
    }

    private fun schedule() {
        // Post an empty message to port2, forcing port1's onmessage to fire later, after whatever current coroutine
        // work is done running
        channel.port2.postMessage(null)
    }

    override fun run() {
        scheduled = false
        process()
    }

    private fun process() {
        while (queue.isNotEmpty()) {
            val element = queue.removeFirst()
            element.run()
        }
    }
}