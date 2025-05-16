package com.varabyte.kobweb.browser

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.FetchDefaults
import com.varabyte.kobweb.browser.http.put
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect
import kotlin.collections.orEmpty

/**
 * Call GET on a target API path with [R] as the expected return type.
 *
 * See also [tryGet], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> ApiFetcher.get(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        get(apiPath, headers, redirect, abortController).decodeToString()
    )
}

/**
 * Like [get], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified R> ApiFetcher.tryGet(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryGet(apiPath, headers, redirect, abortController)
        ?.decodeToString()
        ?.let { Json.decodeFromString(responseDeserializer, it) }
}

/**
 * Call POST on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPost], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.post(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val responseBytes = post(
        apiPath,
        body,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        redirect,
        abortController,
        bodySerializer
    )

    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [post] that expects a body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.post(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray {
    return post(
        apiPath,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController
    )
}

/**
 * A serialize-friendly version of [post] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.post(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = post(
        apiPath,
        headers,
        body = null,
        redirect,
        abortController
    )

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Like [post], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPost(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val responseBytes = tryPost(
        apiPath,
        body,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        redirect,
        abortController,
        bodySerializer,
    )

    if (responseBytes == null) return null
    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [tryPost] that expects a body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.tryPost(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? {
    return tryPost(
        apiPath,
        headers,
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController,
    )
}

/**
 * A serialize-friendly version of [tryPost] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.tryPost(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryPost(
        apiPath,
        headers,
        body = null,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}


/**
 * Call PUT on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPut], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.put(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val responseBytes = put(
        apiPath,
        body,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        redirect,
        abortController,
        bodySerializer
    )

    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [put] that expects a body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.put(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray {
    return put(
        apiPath,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController
    )
}

/**
 * A serialize-friendly version of [put] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.put(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = put(
        apiPath,
        headers,
        body = null,
        redirect,
        abortController
    )

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Like [put], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPut(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val responseBytes = tryPut(
        apiPath,
        body,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        redirect,
        abortController,
        bodySerializer,
    )

    if (responseBytes == null) return null
    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [tryPut] that expects a body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.tryPut(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? {
    return tryPut(
        apiPath,
        headers,
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController,
    )
}

/**
 * A serialize-friendly version of [tryPut] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.tryPut(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryPut(
        apiPath,
        headers,
        body = null,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Call PATCH on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPatch], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.patch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val responseBytes = patch(
        apiPath,
        body,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        redirect,
        abortController,
        bodySerializer
    )

    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [patch] that expects a body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.patch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray {
    return patch(
        apiPath,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController
    )
}

/**
 * A serialize-friendly version of [patch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.patch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = patch(
        apiPath,
        headers,
        body = null,
        redirect,
        abortController
    )

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Like [patch], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPatch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val responseBytes = tryPatch(
        apiPath,
        body,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        redirect,
        abortController,
        bodySerializer,
    )

    if (responseBytes == null) return null
    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [tryPatch] that expects a body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.tryPatch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? {
    return tryPatch(
        apiPath,
        headers,
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController,
    )
}

/**
 * A serialize-friendly version of [tryPatch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.tryPatch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryPatch(
        apiPath,
        headers,
        body = null,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Call DELETE on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> ApiFetcher.delete(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = delete(
        apiPath,
        headers,
        redirect,
        abortController
    )

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [tryDelete].
 */
suspend inline fun <reified R> ApiFetcher.tryDelete(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryDelete(
        apiPath,
        headers,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

