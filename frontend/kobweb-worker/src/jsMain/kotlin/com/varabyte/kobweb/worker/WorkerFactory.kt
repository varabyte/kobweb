package com.varabyte.kobweb.worker

import com.varabyte.kobweb.serialization.IOSerializer

/**
 * A worker factory creates all objects necessary to construct a worker.
 *
 * Anyone defining a Kobweb worker module is expected to declare exactly ONE implementation of this class.
 *
 * By implementing this interface, the user will:
 *
 * * Define a `WorkerStrategy`, which represents the logic that handles values sent over from the application and
performs some (presumably expensive) logic on it.
 * * Implement serialization / deserialization logic, which is needed to pass messages between the separated application
 *   domain and the worker domain.
 *
 * If you don't care about passing rich objects around and are happy with just using raw strings to communicate, you can
 * implement a `WorkerFactory<String, String>` and then use the [createPassThroughSerializer] method when overriding
 * the [createIOSerializer] method.
 *
 * If you are using Kotlinx Serialization, consider adding a dependency on the
 * `com.varabyte.kobwebx:kobwebx-serialization-kotlinx` helper library, which provides a convenience utility method to
 * create a message serializer for you:
 *
 * ```
 * // Json.createIOSerializer comes from the kobwebx-serialization-kotlinx library.
 * // InParams and OutParams must be marked @Serializable or else a runtime error will occur
 * class ExampleWorkerFactory : WorkerFactory<InParams, OutParams>() {
 *    override fun createIOSerializer() = Json.createIOSerializer<InParams, OutParams>()
 * }
 * ```
 *
 * Meanwhile, the [createStrategy] method takes in a `postOutput` method which can be used to send messages back to the
 * application domain.
 *
 * The Kobweb Worker Gradle plugin will find this implementation and generate code that wraps it inside a worker class
 * that the application will interact with:
 *
 * ```
 * // In the worker module:
 * class ExampleWorkerFactory : WorkerFactor<InParams, OutParams>() { ... }
 *
 * // In the application module:
 * val worker = rememberWorker { ExampleWorker() { outParams -> ... } }
 * ```
 *
 * The name of the final class should end with "WorkerFactory" (since a correlated "Worker" class will
 * automatically be generated with its name derived from it, e.g. "ExampleWorkerFactory" to "ExampleWorker". The
 * factory implementation should also be marked `internal` because there shouldn't be a reason for the application to
 * ever interact with it.
 */
interface WorkerFactory<I, O> {
    fun createStrategy(postOutput: (output: O) -> Unit): WorkerStrategy<I>
    fun createIOSerializer(): IOSerializer<I, O>
}

/**
 * Create a no-op [IOSerializer] used for worker strategies that sends and receive raw strings.
 */
@Suppress("UnusedReceiverParameter") // Receiver parameter is useful for scoping context
fun WorkerFactory<String, String>.createPassThroughSerializer() = object : IOSerializer<String, String> {
    override fun serializeInput(input: String) = input
    override fun deserializeInput(input: String) = input
    override fun serializeOutput(output: String) = output
    override fun deserializeOutput(output: String) = output
}
