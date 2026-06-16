package com.varabyte.kobweb.browser.http

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response

// Needs to be public so inline methods can access it, but users probably won't ever need to call this themselves
fun <B> B.toRequestBody(strategy: SerializationStrategy<B>): RequestBody {
    return bodyOf(Json.encodeToString(strategy, this), "application/json")
}

// Needs to be public so inline methods can access it, but users probably won't ever need to call this themselves
suspend inline fun <reified R> Response.tryDeserializeResponseBody(strategy: DeserializationStrategy<R>): R? {
    if (R::class == Unit::class) return Unit as R
    val responseBytes = this.getBodyBytes() ?: return null

    return Json.decodeFromString(strategy, responseBytes.decodeToString())
}

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
): R = get(
    resource,
    headers,
    redirect,
    abortController,
).tryDeserializeResponseBody(responseDeserializer)!!

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
): R? = tryGet(
    resource,
    headers,
    redirect,
    abortController,
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [post] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> HttpFetcher.post(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = post(resource, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [post] that expects a serializable body but returns raw bytes instead of a serialized response.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "post(resource, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.http.post",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> HttpFetcher.postBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = post(resource, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()

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
): R = post(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [post] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.post(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = post(
    resource,
    headers,
    body = null,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [tryPost] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> HttpFetcher.tryPost(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPost(resource, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [post] that expects a serializable body but returns raw bytes instead of a serialized response.
 */
@Deprecated(
    "We are migrating away from returning raw bytes to a more proper Respose object instead.",
    ReplaceWith(
        "tryPost(resource, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.http.tryPost",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> HttpFetcher.tryPostBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPost(resource, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()


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
): R? = tryPost(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer,
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [tryPost] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.tryPost(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPost(
    resource,
    headers,
    body = null,
    redirect,
    abortController
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [put] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> HttpFetcher.put(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = put(
    resource,
    headers,
    body.toRequestBody(bodySerializer),
    redirect,
    abortController
)

/**
 * A serialize-friendly version of [put] that expects a serializable body but returns raw bytes instead of a serialized response.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "put(resource, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.http.put",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> HttpFetcher.putBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = put(
    resource,
    headers,
    body.toRequestBody(bodySerializer),
    redirect,
    abortController
).getBodyBytes().orEmpty()

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
): R = put(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [put] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.put(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = put(
    resource,
    headers,
    body = null,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [tryPut] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> HttpFetcher.tryPut(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPut(resource, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [put] that expects a serializable body but returns raw bytes instead of a serialized response.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "tryPut(resource, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()",
        "com.varabyte.kobweb.browser.http.tryPut",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> HttpFetcher.tryPutBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPut(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
)?.getBodyBytes()?.orEmpty()

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
): R? = tryPut(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [tryPut] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.tryPut(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPut(
    resource,
    headers,
    body = null,
    redirect,
    abortController
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [patch] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> HttpFetcher.patch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = patch(resource, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [patch] that expects a serializable body but returns raw bytes instead of a serialized response.
 */
@Deprecated(
    "We are migrating away from returning raw bytes to a more proper Respose object instead.",
    ReplaceWith(
        "patch(resource, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.http.patch",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> HttpFetcher.patchBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = patch(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).getBodyBytes().orEmpty()

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
): R = patch(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [patch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.patch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = patch(
    resource,
    headers,
    body = null,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [tryPatch] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> HttpFetcher.tryPatch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPatch(
    resource,
    headers,
    bodyOf(Json.encodeToString(bodySerializer, body).encodeToByteArray()),
    redirect,
    abortController
)

/**
 * A serialize-friendly version of [patch] that expects a serializable body but returns raw bytes instead of a serialized response.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "tryPatch(resource, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()",
        "com.varabyte.kobweb.browser.http.tryPatch",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> HttpFetcher.tryPatchBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPatch(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
)?.getBodyBytes()?.orEmpty()

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
): R? = tryPatch(
    resource,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer,
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [tryPatch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> HttpFetcher.tryPatch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPatch(
    resource,
    headers,
    body = null,
    redirect,
    abortController
)?.tryDeserializeResponseBody(responseDeserializer)

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
): R = delete(
    resource,
    headers,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [tryDelete].
 */
suspend inline fun <reified R> HttpFetcher.tryDelete(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryDelete(
    resource,
    headers,
    redirect,
    abortController
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * Call DELETE on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> HttpFetcher.options(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = options(
    resource,
    headers,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [tryDelete].
 */
suspend inline fun <reified R> HttpFetcher.tryOptions(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryOptions(
    resource,
    headers,
    redirect,
    abortController
)?.tryDeserializeResponseBody(responseDeserializer)

