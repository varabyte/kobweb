package com.varabyte.kobweb.browser

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.FetchDefaults
import com.varabyte.kobweb.browser.http.put
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect

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
 * See also [tryPost], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.post(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return post(
        apiPath,
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
suspend inline fun <reified R> ApiFetcher.post(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        post(apiPath, headers, body, redirect, abortController).decodeToString()
    )
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
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPost(
        apiPath,
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
suspend inline fun <reified R> ApiFetcher.tryPost(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPost(
        apiPath,
        headers,
        body,
        redirect,
        abortController,
    )
        ?.let { Json.decodeFromString(responseDeserializer, it.decodeToString()) }
}

/**
 * Call PUT on a target API path with [B] as the body type and [R] as the expected return type.
 *
 * See also [tryPut], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.put(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return put(
        apiPath,
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
suspend inline fun <reified R> ApiFetcher.put(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        put(
            apiPath,
            headers,
            body,
            redirect,
            abortController,
        ).decodeToString()
    )
}

/**
 * Like [put], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPut(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPut(
        apiPath,
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
suspend inline fun <reified R> ApiFetcher.tryPut(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPut(
        apiPath,
        headers,
        body,
        redirect,
        abortController,
    )
        ?.let { Json.decodeFromString(responseDeserializer, it.decodeToString()) }
}

/**
 * Call PATCH on a target API path with [B] as the body type and [R] as the expected return type.
 *
 * See also [tryPatch], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.patch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return patch(
        apiPath,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer,
    )
}

/**
 * A serialize-friendly version of [patch] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> ApiFetcher.patch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        patch(
            apiPath,
            headers,
            body,
            redirect,
            abortController,
        ).decodeToString()
    )
}

/**
 * Like [patch], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 * [bodySerializer].
 */
suspend inline fun <reified B, reified R> ApiFetcher.tryPatch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: B? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPatch(
        apiPath,
        if (body == null) headers else (mapOf("Content-type" to "application/json") + headers.orEmpty()),
        body?.let { Json.encodeToString(bodySerializer, it).encodeToByteArray() },
        redirect,
        abortController,
        responseDeserializer,
    )
}

/**
 * A serialize-friendly version of [tryPatch] that doesn't put any type constraints on the body.
 *
 * This is useful if your request doesn't require a body to be included, so there shouldn't be a need to specify a
 * body type constraint in that case, or if it is easier to use a custom, handcrafted byte array message instead.
 */
suspend inline fun <reified R> ApiFetcher.tryPatch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryPatch(
        apiPath,
        headers,
        body,
        redirect,
        abortController,
    )
        ?.let { Json.decodeFromString(responseDeserializer, it.decodeToString()) }
}

/**
 * Call DELETE on a target API path with [R] as the expected return type.
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
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        delete(apiPath, headers, redirect, abortController).decodeToString()
    )
}

/**
 * Like [delete], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified R> ApiFetcher.tryDelete(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryDelete(apiPath, headers, redirect, abortController)
        ?.let { Json.decodeFromString(responseDeserializer, it.decodeToString()) }
}


/**
 * Call HEAD on a target API path with [R] as the expected return type.
 *
 * See also [tryHead], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> ApiFetcher.head(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(responseDeserializer, head(apiPath, headers, redirect, abortController).decodeToString())
}

/**
 * Like [head], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified R> ApiFetcher.tryHead(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryHead(apiPath, headers, redirect, abortController)
        ?.let { Json.decodeFromString(responseDeserializer, it.decodeToString()) }
}

/**
 * Call OPTIONS on a target API path with [R] as the expected return type.
 *
 * See also [tryOptions], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> ApiFetcher.options(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R {
    return Json.decodeFromString(
        responseDeserializer,
        options(apiPath, headers, redirect, abortController).decodeToString()
    )
}

/**
 * Like [options], but returns null if the request failed for any reason.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
suspend inline fun <reified R> ApiFetcher.tryOptions(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? {
    return tryOptions(apiPath, headers, redirect, abortController)
        ?.let { Json.decodeFromString(responseDeserializer, it.decodeToString()) }
}