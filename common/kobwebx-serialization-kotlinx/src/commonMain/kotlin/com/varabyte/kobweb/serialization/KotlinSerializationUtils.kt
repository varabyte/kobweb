package com.varabyte.kobweb.serialization

import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * A convenience utility method for creating an [IOSerializer] for projects using Kotlinx Serialization.
 *
 * In order to use this, both your worker strategy's input and output types must be classes annotated with
 * [`@Serializable`][kotlinx.serialization.Serializable].
 *
 * You can use it as in the following code example:
 *
 * ```
 * @Serializable
 * class InputParams(...)
 *
 * @Serializable
 * class OutputParams(...)
 *
 * class ExampleWorkerStrategy: WorkerStrategy<InputParams, OutputParams>() {
 *    override val ioSerializer = Json.createIOSerializer<InputParams, OutputParams>()
 * }
 * ```
 */
inline fun <reified I, reified O> StringFormat.createIOSerializer() = object : IOSerializer<I, O> {
    override fun serializeInput(input: I): String = encodeToString(input)
    override fun deserializeInput(input: String): I = decodeFromString(input)
    override fun serializeOutput(output: O): String = encodeToString(output)
    override fun deserializeOutput(output: String): O = decodeFromString(output)
}
