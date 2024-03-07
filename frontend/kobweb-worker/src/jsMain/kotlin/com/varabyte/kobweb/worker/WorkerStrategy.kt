package com.varabyte.kobweb.worker

import kotlinx.browser.window
import org.w3c.dom.DedicatedWorkerGlobalScope

private external val self: DedicatedWorkerGlobalScope
// Create a non-conflicting name for the `self` property so WorkerStrategy can expose it.
// Otherwise, `protected val self = self` confuses the compiler.

@Suppress("ObjectPropertyName")
private val _self = self

/**
 * A worker strategy represents the core logic that a web worker performs given some input.
 */
abstract class WorkerStrategy<I> {
    /**
     * The [DedicatedWorkerGlobalScope] that this worker is running in.
     *
     * Inside a worker, the [window] property is not available; it is common to use `self` instead, which provides some
     * useful functionality that users usually get from `window` (like `setTimeout`).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/WorkerGlobalScope/self">WorkerGlobalScope: self property</a>
     */
    protected val self = _self

    /**
     * Receive and handle a message from the application.
     */
    abstract fun onInput(inputMessage: InputMessage<I>)
}

class WorkerStrategyContext(val self: DedicatedWorkerGlobalScope, val transferables: Transferables)

/**
 * Convenience method to create a [WorkerStrategy] from a lambda.
 */
fun <I> WorkerStrategy(handleInput: WorkerStrategyContext.(I) -> Unit) = object : WorkerStrategy<I>() {
    override fun onInput(inputMessage: InputMessage<I>) =
        WorkerStrategyContext(self, inputMessage.transferables).handleInput(inputMessage.input)
}
