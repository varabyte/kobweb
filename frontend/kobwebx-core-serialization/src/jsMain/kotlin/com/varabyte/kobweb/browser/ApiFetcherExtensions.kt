package com.varabyte.kobweb.browser

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.delete
import com.varabyte.kobweb.browser.http.get
import com.varabyte.kobweb.browser.http.head
import com.varabyte.kobweb.browser.http.options
import com.varabyte.kobweb.browser.http.patch
import com.varabyte.kobweb.browser.http.post
import com.varabyte.kobweb.browser.http.put
import com.varabyte.kobweb.browser.http.tryDelete
import com.varabyte.kobweb.browser.http.tryGet
import com.varabyte.kobweb.browser.http.tryHead
import com.varabyte.kobweb.browser.http.tryOptions
import com.varabyte.kobweb.browser.http.tryPatch
import com.varabyte.kobweb.browser.http.tryPost
import com.varabyte.kobweb.browser.http.tryPut
import kotlinx.browser.window
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer


/**
 * Call GET on a target API path with [T] as the expected return type (deserializable by kotlinx-serialization using the given [serializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * See also [tryGet], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified T> ApiFetcher.get(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T = Json.decodeFromString(serializer, window.api.get(apiPath, headers, abortController, autoPrefix).decodeToString())

/**
 * Like [get], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified T> ApiFetcher.tryGet(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T? = window.api.tryGet(apiPath, headers, abortController, autoPrefix)?.decodeToString()
    ?.let { Json.decodeFromString(serializer, it) }

/**
 * Call POST on a target API path with [R] as the expected return type (deserializable by kotlinx-serialization using the given [responseSerializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 *
 * See also [tryPost], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B, reified R> ApiFetcher.post(
    apiPath: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R = Json.decodeFromString(
    responseSerializer,
    window.api.post(
        apiPath,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController,
        autoPrefix
    ).decodeToString()
)

/**
 * Like [post], but returns null if the request failed for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPost(
    apiPath: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R? = window.api.tryPost(
    apiPath,
    headers,
    body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
    abortController,
    autoPrefix
)
    ?.let { Json.decodeFromString(responseSerializer, it.decodeToString()) }

/**
 * Call PUT on a target API path with [R] as the expected return type (deserializable by kotlinx-serialization using the given [responseSerializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * See also [tryPut], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B, reified R> ApiFetcher.put(
    apiPath: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R = Json.decodeFromString(
    responseSerializer,
    window.api.put(
        apiPath,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController,
        autoPrefix
    ).decodeToString()
)

/**
 * Like [put], but returns null if the request failed for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified B, reified T> ApiFetcher.tryPut(
    apiPath: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<T> = serializer()
): T? = window.api.tryPut(
    apiPath,
    headers,
    body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
    abortController,
    autoPrefix
)
    ?.let { Json.decodeFromString(responseSerializer, it.decodeToString()) }

/**
 * Call PATCH on a target API path with [R] as the expected return type (deserializable by kotlinx-serialization using the given [responseSerializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * See also [tryPatch], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B, reified R> ApiFetcher.patch(
    apiPath: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R = Json.decodeFromString(
    responseSerializer,
    window.api.patch(
        apiPath,
        headers,
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        abortController,
        autoPrefix
    ).decodeToString()
)

/**
 * Like [patch], but returns null if the request failed for any reason.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPatch(
    apiPath: String,
    headers: Map<String, Any>? = null,
    body: B? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseSerializer: DeserializationStrategy<R> = serializer()
): R? = window.api.tryPatch(
    apiPath,
    headers,
    body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
    abortController,
    autoPrefix
)
    ?.let { Json.decodeFromString(responseSerializer, it.decodeToString()) }

/**
 * Call DELETE on a target API path with [T] as the expected return type (deserializable by kotlinx-serialization using the given [serializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified T> ApiFetcher.delete(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T =
    Json.decodeFromString(serializer, window.api.delete(apiPath, headers, abortController, autoPrefix).decodeToString())

/**
 * Like [delete], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified T> ApiFetcher.tryDelete(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T? = window.api.tryDelete(apiPath, headers, abortController, autoPrefix)
    ?.let { Json.decodeFromString(serializer, it.decodeToString()) }


/**
 * Call HEAD on a target API path with [T] as the expected return type (deserializable by kotlinx-serialization using the given [serializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * See also [tryHead], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified T> ApiFetcher.head(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T =
    Json.decodeFromString(serializer, window.api.head(apiPath, headers, abortController, autoPrefix).decodeToString())

/**
 * Like [head], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified T> ApiFetcher.tryHead(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T? = window.api.tryHead(apiPath, headers, abortController, autoPrefix)
    ?.let { Json.decodeFromString(serializer, it.decodeToString()) }

/**
 * Call OPTIONS on a target API path with [T] as the expected return type (deserializable by kotlinx-serialization using the given [serializer]).
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
 *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
 *   staying in the same domain.
 *
 * See also [tryOptions], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified T> ApiFetcher.options(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T = Json.decodeFromString(
    serializer, window.api.options(apiPath, headers, abortController, autoPrefix).decodeToString()
)

/**
 * Like [options], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified T> ApiFetcher.tryOptions(
    apiPath: String,
    headers: Map<String, Any>? = null,
    abortController: AbortController? = null,
    autoPrefix: Boolean = true,
    serializer: DeserializationStrategy<T> = serializer()
): T? = window.api.tryOptions(apiPath, headers, abortController, autoPrefix)
    ?.let { Json.decodeFromString(serializer, it.decodeToString()) }