package com.varabyte.kobweb.browser.http

import com.varabyte.kobweb.browser.http.tryPost
import com.varabyte.kobweb.browser.tryPost
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

/**
 * Convert the receiving [Response] into an object of type [R] (where that class should be marked `@Serializable`).
 *
 * This method will throw if the response body cannot be converted (usually a deserialization issue).
 *
 * For example, if you had two serializable classes, `EchoRequset` and `EchoReply`, your calling code might look
 * something like this:
 *
 * ```kotlin
 * val echoReply = window.http.post("echo", EchoRequest("hello")).bodyAs<EchoReply>()
 * console.log("Echo: ${echoReply.message}")
 * ```
 *
 * @param requireOk If true and [Response.ok] is false, this method will throw. Even responses that are `!ok` can still
 *   have bodies, so you may still want to opt in to reading the body. However, while a successful response will have a
 *   valid value designed for deserialization, error responses sometimes have generic error messages instead, which is
 *   why this value defaults to true.
 */
suspend inline fun <reified R> Response.bodyAs(requireOk: Boolean = true, strategy: DeserializationStrategy<R> = serializer()): R {
    if (R::class == Unit::class) return Unit as R

    return Json.decodeFromString(strategy, bodyAsBytes(requireOk).decodeToString())
}

// Users won't need to use this version, but it's a convenient form for deprecated methods below. I intend to remove it
// after the deprecated methods are eventually removed.
suspend inline fun <reified R> Response.bodyAs(strategy: DeserializationStrategy<R>): R = bodyAs(requireOk = true, strategy)

/**
 * Call GET on a target resource with [R] as the expected return type.
 *
 * See also [tryGet], which will return null if the request fails.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "get(resource, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.get",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.get(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R = get(resource, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * Like [get], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryGet(resource, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryGet",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.tryGet(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? = tryGet(resource, headers, redirect, abortController) { bodyAs(responseDeserializer) }

/**
 * A version of [post] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `post<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 */
suspend inline fun <reified B> HttpFetcher.post(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = post(resource, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [post] that accepts a serializable body and returns its response as a raw byte array.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "post(resource, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()",
        "com.varabyte.kobweb.browser.http.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> HttpFetcher.postBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = post(resource, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()

/**
 * Call POST on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPost], which will return null if the request fails.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "post(resource, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [post] that has no body but provides a serialized response.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "post(resource, body = null, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.post(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = post(
    resource,
    body = null,
    headers,
    redirect,
    abortController
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryPost] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
 * the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPost] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPost<RequestClass>().bodyAs<ReplyClass>()`
 */
suspend inline fun <reified B, T> HttpFetcher.tryPost(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryPost(resource, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [post] but returns null instead of throwing if the request fails.
 *
 * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPost] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()`.
 */
suspend inline fun <reified B> HttpFetcher.tryPost(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPost(resource, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A version of [tryPost] that returns its response as a raw byte array.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryPost(resource, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.browser.http.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> HttpFetcher.tryPostBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPost(resource, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [post], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPost(resource, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
    bodySerializer
) { bodyAs(responseDeserializer) }

/**
 * A serialize-friendly version of [tryPost] that has no body but provides a serialized response.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPost(resource, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.tryPost(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPost(
    resource,
    body = null,
    headers,
    redirect,
    abortController
) { bodyAs(responseDeserializer) }

/**
 * A version of [put] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `put<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 */
suspend inline fun <reified B> HttpFetcher.put(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = put(resource, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [put] that accepts a serializable body and returns its response as a raw byte array.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "put(resource, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()",
        "com.varabyte.kobweb.browser.http.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> HttpFetcher.putBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = put(resource, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()

/**
 * Call PUT on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPut], which will return null if the request fails.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "put(resource, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [put] that has no body but provides a serialized response.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "put(resource, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.put(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = put(
    resource,
    body = null,
    headers,
    redirect,
    abortController
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryPut] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
 * the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPut] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPut<RequestClass>().bodyAs<ReplyClass>()`
 */
suspend inline fun <reified B, T> HttpFetcher.tryPut(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryPut(resource, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [put] but returns null instead of throwing if the request fails.
 *
 * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPut] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()`.
 */
suspend inline fun <reified B> HttpFetcher.tryPut(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPut(resource, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A serialize-friendly version of [put] that accepts a serializable body but returns raw bytes instead of a serialized
 * response.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryPut(resource, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.browser.http.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> HttpFetcher.tryPutBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? =
    tryPut(resource, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [put], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPut(resource, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
) { bodyAs(responseDeserializer) }

/**
 * A serialize-friendly version of [tryPut] that has no body but provides a serialized response.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPut(resource, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.tryPut(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPut(
    resource,
    body = null,
    headers,
    redirect,
    abortController
) { bodyAs(responseDeserializer) }

/**
 * A version of [patch] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `patch<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 */
suspend inline fun <reified B> HttpFetcher.patch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = patch(resource, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [patch] that accepts a serializable body and returns its response as a raw byte array.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "patch(resource, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()",
        "com.varabyte.kobweb.browser.http.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> HttpFetcher.patchBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = patch(resource, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()

/**
 * Call PATCH on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPatch], which will return null if the request fails.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "patch(resource, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [patch] that has no body but provides a serialized response.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "patch(resource, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.patch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = patch(
    resource,
    body = null,
    headers,
    redirect,
    abortController
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryPatch] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
 * the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPatch] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPatch<RequestClass>().bodyAs<ReplyClass>()`
 */
suspend inline fun <reified B, T> HttpFetcher.tryPatch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryPatch(resource, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [patch] but returns null instead of throwing if the request fails.
 *
 * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPatch] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()`.
 */
suspend inline fun <reified B> HttpFetcher.tryPatch(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPatch(resource, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A serialize-friendly version of [patch] that accepts a serializable body but returns raw bytes instead of a
 * serialized response.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryPatch(resource, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.browser.http.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> HttpFetcher.tryPatchBytes(
    resource: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPatch(resource, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [patch], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if [HttpFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPatch(resource, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
    bodySerializer
) { bodyAs(responseDeserializer) }

/**
 * A serialize-friendly version of [tryPatch] that has no body but provides a serialized response.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPatch(resource, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.tryPatch(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPatch(
    resource,
    body = null,
    headers,
    redirect,
    abortController
) { bodyAs(responseDeserializer) }

/**
 * Call DELETE on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "delete(resource, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.delete",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.delete(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = delete(resource, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryDelete].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryDelete(resource, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryDelete",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
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
) { bodyAs(responseDeserializer) }

/**
 * Call DELETE on a target resource with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "options(resource, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.http.options",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.options(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = options(resource, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryDelete].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryOptions(resource, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.tryOptions",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> HttpFetcher.tryOptions(
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryOptions(resource, headers, redirect, abortController) { bodyAs(responseDeserializer) }

