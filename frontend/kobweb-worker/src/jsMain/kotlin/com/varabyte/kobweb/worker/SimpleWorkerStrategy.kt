package com.varabyte.kobweb.worker

import com.varabyte.kobweb.serialization.IOSerializer

/**
 * A simple implementation of [WorkerStrategy] that just uses strings as input and output.
 *
 * Type-safe worker strategies are generally more recommended, but when prototyping or for very simple use cases, this
 * can be easier to use because you don't have to worry about serialization / deserialization nor have to provide a
 * message converter.
 *
 * ```
 * // Worker
 * class EchoWorkerStrategy : SimpleWorkerStrategy() {
 *   override fun onInput(input: String) {
 *     postOutput("Echoed: $input")
 *   }
 * }
 *
 * // Application
 * val worker = rememberWorker { ExampleWorker() { output -> console.log(output) } }
 * // Later, say in a button's onClick callback:
 * console.log("Sending: Hello world!")
 * worker.postInput("Hello world!")
 * ```
 */
abstract class SimpleWorkerStrategy : WorkerStrategy<String, String>() {
    override val ioSerializer = object : IOSerializer<String, String> {
        override fun serializeInput(input: String) = input
        override fun deserializeInput(input: String) = input
        override fun serializeOutput(output: String) = output
        override fun deserializeOutput(output: String) = output
    }
}
