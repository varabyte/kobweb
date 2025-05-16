package com.varabyte.kobweb.browser.http

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect
import kotlin.collections.orEmpty

/**
 * Call GET on a target resource with [R] as the expected return type.
 *
 * See also [tryGet], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> HttpFetcher.get(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        get(resource, headers, redirect, abortController).decodeToString()
    )
}

/**
 * Like [get], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified R> HttpFetcher.tryGet(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryGet(resource, headers, redirect, abortController)
        ?.decodeToString()
        ?.let { Json.decodeFromString(responseDeserializer, it) }
}

/**
 * Call POST on a target resource with [R] as the expected return type.
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
suspend inline fun <reified B, reified R> HttpFetcher.post(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val responseBytes = post(
        resource,
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
suspend inline fun <reified B> HttpFetcher.post(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray {
    return post(
        resource,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController
    )
}

/**
 * A serialize-friendly version of [post] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.post(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = post(
        resource,
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
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPost(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val responseBytes = tryPost(
        resource,
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
suspend inline fun <reified B> HttpFetcher.tryPost(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? {
    return tryPost(
        resource,
        headers,
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController,
    )
}

/**
 * A serialize-friendly version of [tryPost] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.tryPost(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryPost(
        resource,
        headers,
        body = null,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}


/**
 * Call PUT on a target resource with [R] as the expected return type.
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
suspend inline fun <reified B, reified R> HttpFetcher.put(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val responseBytes = put(
        resource,
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
suspend inline fun <reified B> HttpFetcher.put(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray {
    return put(
        resource,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController
    )
}

/**
 * A serialize-friendly version of [put] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.put(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = put(
        resource,
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
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPut(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val responseBytes = tryPut(
        resource,
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
suspend inline fun <reified B> HttpFetcher.tryPut(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? {
    return tryPut(
        resource,
        headers,
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController,
    )
}

/**
 * A serialize-friendly version of [tryPut] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.tryPut(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryPut(
        resource,
        headers,
        body = null,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Call PATCH on a target resource with [R] as the expected return type.
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
suspend inline fun <reified B, reified R> HttpFetcher.patch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val responseBytes = patch(
        resource,
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
suspend inline fun <reified B> HttpFetcher.patch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray {
    return patch(
        resource,
        mapOf("Content-type" to "application/json") + headers.orEmpty(),
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController
    )
}

/**
 * A serialize-friendly version of [patch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.patch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = patch(
        resource,
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
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPatch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val responseBytes = tryPatch(
        resource,
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
suspend inline fun <reified B> HttpFetcher.tryPatch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? {
    return tryPatch(
        resource,
        headers,
        Json.encodeToString(bodySerializer, body).encodeToByteArray(),
        redirect,
        abortController,
    )
}

/**
 * A serialize-friendly version of [tryPatch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.tryPatch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryPatch(
        resource,
        headers,
        body = null,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * Call DELETE on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> HttpFetcher.delete(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R {
    val responseBytes = delete(
        resource,
        headers,
        redirect,
        abortController
    )

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

/**
 * A serialize-friendly version of [tryDelete].
 */
suspend inline fun <reified R> HttpFetcher.tryDelete(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? {
    val responseBytes = tryDelete(
        resource,
        headers,
        redirect,
        abortController
    )

    if (responseBytes == null) return null

    return Json.decodeFromString(responseDeserializer, responseBytes.decodeToString())
}

