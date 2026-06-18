package com.varabyte.kobweb.api.http

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * A serialization-aware convenience method layered on top of [ContentSource.text].
 *
 * An exception will be thrown if the body type cannot be deserialized (either due to body text that is not valid JSON
 * or valid JSON that cannot be converted into the requested type).
 *
 * See also the ApiFetcher extension methods provided by this library for examples of how to send requests with a
 * serialized body, e.g. `window.api.post<ExampleRequest, ExampleResponse>(body = ...)`.
 */
suspend inline fun <reified T> ContentSource.decode(bodyDeserializer: DeserializationStrategy<T> = serializer()): T? {
    return text().let { bodyText ->
        Json.decodeFromString(bodyDeserializer, bodyText)
    }
}

/**
 * A serialization-aware convenience factory method that can be used to create [Body] instances.
 */
inline fun <reified T> Body.Companion.encode(body: T, bodySerializer: SerializationStrategy<T> = serializer()): Body {
    return json(Json.encodeToString(bodySerializer, body))
}
