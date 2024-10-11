package com.varabyte.kobweb.browser.http

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.khronos.webgl.get

/**
 * Call GET on a target resource with [T] as the expected return type.
 *
 * See also [tryGet], which will return null if the request fails for any reason.
 */
suspend inline fun <reified T> HttpFetcher.get(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T {
    val response = get(resource, headers, abortController)
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Like [get], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified T> HttpFetcher.tryGet(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T? {
    val response = tryGet(resource, headers, abortController) ?: return null
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Call POST on a target resource with [R] as the expected return type.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * See also [tryPost], which will return null if the request fails for any reason.
 */
suspend inline fun <reified B, reified R> HttpFetcher.post(
    resource: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R {
    val response = post(
        resource,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController
    )
    return Json.decodeFromString(responseSerializer, response.decodeToString())
}

/**
 * Like [post], but returns null if the request failed for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPost(
    resource: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryPost(
        resource,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController
    ) ?: return null
    return Json.decodeFromString(responseSerializer, response.decodeToString())
}

/**
 * Call PUT on a target resource with [R] as the expected return type.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * See also [tryPut], which will return null if the request fails for any reason.
 */
suspend inline fun <reified B, reified R> HttpFetcher.put(
    resource: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R {
    val response = put(
        resource,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController
    )
    return Json.decodeFromString(responseSerializer, response.decodeToString())
}

/**
 * Like [put], but returns null if the request failed for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified B, reified R> HttpFetcher.tryPut(
    resource: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryPut(
        resource,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController
    ) ?: return null
    return Json.decodeFromString(responseSerializer, response.decodeToString())
}

/**
 * Call PATCH on a target resource with [R] as the expected return type.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * See also [tryPatch], which will return null if the request fails for any reason.
 */
suspend inline fun <reified B, reified R> HttpFetcher.patch(
    resource: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R {
    val response = patch(
        resource,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController
    )
    return Json.decodeFromString(responseSerializer, response.decodeToString())
}

/**
 * Like [patch], but returns null if the request failed for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified B,reified R> HttpFetcher.tryPatch(
    resource: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R? {
    val response = tryPatch(
        resource,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController
    ) ?: return null
    return Json.decodeFromString(responseSerializer, response.decodeToString())
}

/**
 * Call DELETE on a target resource with [T] as the expected return type.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 */
suspend inline fun <reified T> HttpFetcher.delete(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T {
    val response = delete(resource, headers, abortController)
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Like [delete], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified T> HttpFetcher.tryDelete(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T? {
    val response = tryDelete(resource, headers, abortController) ?: return null
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Call HEAD on a target resource with [T] as the expected return type.
 *
 * See also [tryHead], which will return null if the request fails for any reason.
 */
suspend inline fun <reified T> HttpFetcher.head(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T {
    val response = head(resource, headers, abortController)
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Like [head], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified T> HttpFetcher.tryHead(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T? {
    val response = tryHead(resource, headers, abortController) ?: return null
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Call OPTIONS on a target resource with [T] as the expected return type.
 *
 * See also [tryOptions], which will return null if the request fails for any reason.
 */
suspend inline fun <reified T> HttpFetcher.options(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T {
    val response = options(resource, headers, abortController)
    return Json.decodeFromString(serializer, response.decodeToString())
}

/**
 * Like [options], but returns null if the request failed for any reason.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console.
 */
suspend inline fun <reified T> HttpFetcher.tryOptions(
    resource: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    serializer: DeserializationStrategy<T> = serializer()
): T? {
    val response = tryOptions(resource, headers, abortController) ?: return null
    return Json.decodeFromString(serializer, response.decodeToString())
}