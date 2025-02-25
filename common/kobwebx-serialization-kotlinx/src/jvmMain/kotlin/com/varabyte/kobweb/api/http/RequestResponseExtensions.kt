package com.varabyte.kobweb.api.http

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * A serialization-aware convenience method layered on top of [Request.readBodyText].
 *
 * If no body is set, this will return null. However, an exception will be thrown if the body type cannot be
 * deserialized (either due to body text that is not valid JSON or valid JSON that cannot be converted into the
 * requested type).
 *
 * See also the ApiFetcher extension methods provided by this library for examples of how to send requests with a
 * serialized body, e.g. `window.api.post<ExampleRequest, ExampleResponse>(body = ...)`.
 */
inline fun <reified T> Request.readBody(bodySerializer: DeserializationStrategy<T> = serializer()): T? {
    return readBodyText()?.let { bodyText ->
        Json.decodeFromString(bodySerializer, bodyText)
    }
}

/**
 * A serialization-aware convenience method layered on top of [Response.setBodyText].
 */
inline fun <reified T> Response.setBody(body: T, bodySerializer: SerializationStrategy<T> = serializer()) {
    setBodyText(Json.encodeToString(bodySerializer, body))
}
