package com.varabyte.kobweb.browser.http

import com.varabyte.kobweb.browser.tryPost
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.khronos.webgl.get
import org.w3c.fetch.RequestRedirect

/**
 * Call GET on a target resource with [R] as the expected return type.
 *
 * See also [tryGet], which will return null if the request fails for any reason.
 */
suspend inline fun <reified R> HttpFetcher.get(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = get(resource, headers, redirect, abortController)
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [get], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified R> HttpFetcher.tryGet(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryGet(resource, headers, redirect, abortController) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Call POST on a target resource with [B] as the body type and [R] as the expected return type.
 *
 * See also [tryPost], which will return null if the request fails for any reason.

 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer]. Note that JSON is used as the serialization format, and if body is non-null, the `Content-type`
 * will automatically be set to `application/json` (unless explicitly set by the user).
 */
suspend inline fun <reified B, reified R> HttpFetcher.post(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return post(
        resource,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer
    )
}

/**
 * A serialize-friendly version of [post] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> HttpFetcher.post(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = post(
        resource,
        headers,
        body,
        redirect,
        abortController
    )
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [post], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer]. Note that JSON is used as the serialization format, and if body is non-null, the `Content-type`
 * will automatically be set to `application/json` (unless explicitly set by the user).
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPost(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPost(
        resource,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer
    )
}

/**
 * A serialize-friendly version of [tryPost] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> HttpFetcher.tryPost(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryPost(
        resource,
        headers,
        body,
        redirect,
        abortController
    ) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Call PUT on a target resource with [B] as the body type and [R] as the expected return type.
 *
 * See also [tryPut], which will return null if the request fails for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer]. Note that JSON is used as the serialization format, and if body is non-null, the `Content-type`
 * will automatically be set to `application/json` (unless explicitly set by the user).
 */
suspend inline fun <reified B, reified R> HttpFetcher.put(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return put(
        resource,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer,
    )
}

/**
 * A serialize-friendly version of [put] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> HttpFetcher.put(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = put(
        resource,
        headers,
        body,
        redirect,
        abortController
    )
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [put], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer]. Note that JSON is used as the serialization format, and if body is non-null, the `Content-type`
 * will automatically be set to `application/json` (unless explicitly set by the user).
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPut(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPut(
        resource,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer
    )
}

/**
 * A serialize-friendly version of [tryPut] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> HttpFetcher.tryPut(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryPut(
        resource,
        headers,
        body,
        redirect,
        abortController
    ) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Call PATCH on a target resource with [B] as the body type and [R] as the expected return type.
 *
 * See also [tryPatch], which will return null if the request fails for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer]. Note that JSON is used as the serialization format, and if body is non-null, the `Content-type`
 * will automatically be set to `application/json` (unless explicitly set by the user).
 */
suspend inline fun <reified B, reified R> HttpFetcher.patch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return patch(
        resource,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer
    )
}

/**
 * A serialize-friendly version of [patch] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> HttpFetcher.patch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = patch(
        resource,
        headers,
        body,
        redirect,
        abortController
    )
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [patch], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer]. Note that JSON is used as the serialization format, and if body is non-null, the `Content-type`
 * will automatically be set to `application/json` (unless explicitly set by the user).
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPatch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPatch(
        resource,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer
    )
}

/**
 * A serialize-friendly version of [tryPatch] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> HttpFetcher.tryPatch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryPatch(
        resource,
        headers,
        body,
        redirect,
        abortController
    ) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Call DELETE on a target resource with [R] as the expected return type.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 */
suspend inline fun <reified R> HttpFetcher.delete(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = delete(resource, headers, redirect, abortController)
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [delete], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified R> HttpFetcher.tryDelete(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryDelete(resource, headers, redirect, abortController) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Call HEAD on a target resource with [R] as the expected return type.
 *
 * See also [tryHead], which will return null if the request fails for any reason.
 */
suspend inline fun <reified R> HttpFetcher.head(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = head(resource, headers, redirect, abortController)
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [head], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified R> HttpFetcher.tryHead(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryHead(resource, headers, redirect, abortController) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Call OPTIONS on a target resource with [R] as the expected return type.
 *
 * See also [tryOptions], which will return null if the request fails for any reason.
 */
suspend inline fun <reified R> HttpFetcher.options(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    val response = options(resource, headers, redirect, abortController)
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}

/**
 * Like [options], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified R> HttpFetcher.tryOptions(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryOptions(resource, headers, redirect, abortController) ?: return null
    return Json.decodeFromString(responseDeserializer, response.decodeToString())
}