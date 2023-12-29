package com.varabyte.kobweb.serialization

/**
 * A serializer which is responsible for converting input and output values to and from strings.
 */
interface IOSerializer<I, O> {
    fun serializeInput(input: I): String
    fun deserializeInput(input: String): I
    fun serializeOutput(output: O): String
    fun deserializeOutput(output: String): O
}
