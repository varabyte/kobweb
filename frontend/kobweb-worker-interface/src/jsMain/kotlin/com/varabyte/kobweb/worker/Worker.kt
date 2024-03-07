package com.varabyte.kobweb.worker

class WorkerContext(val transferables: Transferables)

/**
 * Interface for a Kobweb worker.
 *
 * This is the API for a worker from the outside-in. That is, it is the view of a worker from the application's
 * point of view, where it submits work to the worker and then waits for a response. (This is in contrast to the
 * `WorkerStrategy` class, which is the view of a worker from the inside-out).
 *
 * The Kobweb Worker Gradle plugin implements this interface. See that module (and WorkerProcessor.kt) for more details.
 *
 * Normally, users are not expected to interact with this interface directly; rather, they should work directly with the
 * generated worker implementation. However, by extracting this interface, we can provide utility methods that work with
 * any worker implementation.
 */
interface Worker<I, O> {
    /**
     * A callback that will be invoked when the worker posts a message communicating finished work.
     */
    var onOutput: WorkerContext.(O) -> Unit

    /**
     * Send a message to the worker.
     */
    fun postInput(input: I, transferables: Transferables = Transferables.Empty)

    /**
     * Immediately terminate the worker, interrupting any processing it might still be doing.
     *
     * It can be a good practice to explicitly terminate your worker when you're sure you're done
     * with it, as otherwise it may keep running even if you navigate to a different part of your
     * site.
     */
    fun terminate()
}
