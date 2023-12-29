package com.varabyte.kobweb.worker

import com.varabyte.kobweb.serialization.IOSerializer
import org.w3c.dom.DedicatedWorkerGlobalScope

private external val self: DedicatedWorkerGlobalScope
// Create a non-conflicting name for the `self` property so WorkerStrategy can expose it.
// Otherwise, `protected val self = self` confuses the compiler.
@Suppress("ObjectPropertyName")
private val _self = self

/**
 * A worker strategy represents all the core logic that a web worker needs to perform.
 *
 * Anyone defining a Kobweb worker module is expected to declare exactly ONE implementation of this class.
 *
 * The two things that a user implementing a worker strategy needs to do are:
 *
 * * Implement the `onMessage` method. This takes in values sent over from the application and performs some
 *   (presumably expensive) logic on it.
 * * Implement serialization / deserialization logic, which is needed to pass messages between the separated application
 *   domain and the worker domain.
 *
 * If you don't care about passing rich objects back and forth, you can use the [SimpleWorkerStrategy] class instead,
 * which just passes raw strings back and forth.
 *
 * If you are using Kotlinx Serialization, consider adding a dependency on the
 * `com.varabyte.kobwebx:kobwebx-serialization-kotlinx` helper library, which provides a convenience utility method to
 * create a message serializer for you:
 *
 * ```
 * // Json.createIOSerializer comes from the kobwebx-serialization-kotlinx library.
 * // InParams and OutParams must be marked @Serializable or else a runtime error will occur
 * class ExampleWorkerStrategy : WorkerStrategy<InParams, OutParams>() {
 *    override val ioSerializer = Json.createIOSerializer<InParams, OutParams>()
 * }
 * ```
 *
 * Within a `WorkerStrategy`, you can use the `postMessage` method to send messages back to the application domain.
 *
 * The Kobweb Worker Gradle plugin will fine this implementation and generate code that wraps it inside a worker class
 * that the application will interact with:
 *
 * ```
 * // In the worker module:
 * class ExampleWorkerStrategy : WorkerStrategy<InParams, OutParams>() { ... }
 *
 * // In the application module:
 * val worker = remember { ExampleWorker() { outParams -> ... } }
 * ```
 *
 * The name of the final class should end with "WorkerStrategy" i(since a correlated "Worker" class will
 * automatically be generated with its name derived from it, e.g. "ExampleWorkerStrategy" to "ExampleWorker". It should
 * also be marked `internal` because there shouldn't be a reason for the application to ever interact with it.
 *
 * @see SimpleWorkerStrategy
 */
abstract class WorkerStrategy<I, O> {
    @Suppress("MemberVisibilityCanBePrivate") // API designed for subclasses
    protected val self = _self

    init {
        self.onmessage = { e ->
            // If `IOSerializer` throws, that means the message was invalid. Ignore it.
            val inputDeserialized = try {
                ioSerializer.deserializeInput(e.data as String)
            } catch (e: Throwable) {
                null
            }
            if (inputDeserialized != null) {
                onInput(inputDeserialized)
            }
        }
    }

    /**
     * Receive and handle a message from the application.
     */
    abstract fun onInput(input: I)

    /**
     * Send a message back to the application.
     */
    protected fun postOutput(output: O) {
        // If `IOSerializer` throws, that means the message was invalid. Ignore it.
        val outputSerialized = try {
            ioSerializer.serializeOutput(output)
        } catch(e: Throwable) {
            null
        }
        if (outputSerialized != null) {
            self.postMessage(outputSerialized)
        }
    }

    /**
     * The serializer for converting the input and output types used by this worker strategy.
     */
    abstract val ioSerializer: IOSerializer<I, O>
}
