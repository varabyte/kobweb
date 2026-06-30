package com.varabyte.kobweb.browser

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.FetchDefaults
import com.varabyte.kobweb.browser.http.bodyAsBytes
import com.varabyte.kobweb.browser.http.bodyAs
import com.varabyte.kobweb.browser.http.toRequestBody
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response

/**
 * Call GET on a target API path with [R] as the expected return type.
 *
 * See also [tryGet], which will return null if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "get(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.get",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.get(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R = get(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * Like [get], but returns null if the request fails.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryGet(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryGet",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryGet(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? = tryGet(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }

/**
 * A version of [post] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `post<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.post(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = post(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [post] that accepts a serializable body and returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "post(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()",
        "com.varabyte.kobweb.browser.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.postBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = post(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()

/**
 * Call POST on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPost], which will return null if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "post(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.post(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R = post(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [post] that has no body but expects a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "post(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.post(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = post(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)

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
suspend inline fun <reified B, T> ApiFetcher.tryPost(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryPost(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [post] but returns null instead of throwing if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryPost(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A serialize-friendly version of [tryPost] that accepts a serializable body and returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are migrating away from returning raw bytes to a more proper Response object instead.",
    ReplaceWith(
        "tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.browser.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.tryPostBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [post], but returns null if the request fails.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.tryPost(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? = tryPost(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
) { bodyAs(responseDeserializer) }

/**
 * A serialize-friendly version of [tryPost] that has no body but expects a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPost(apiPath, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryPost(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPost(
    apiPath,
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
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.put(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = put(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [put] that accepts a serializable body and returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "put(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()",
        "com.varabyte.kobweb.browser.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.putBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = put(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()

/**
 * Call PUT on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPut], which will return null if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "put(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.put(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R = put(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [put] that has no body but expects a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "put(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.put(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = put(
    apiPath,
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
suspend inline fun <reified B, T> ApiFetcher.tryPut(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryPut(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [put] but returns null instead of throwing if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryPut(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A serialize-friendly version of [tryPut] that accepts a serializable body and returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.browser.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.tryPutBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [put], but returns null if the request fails.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.tryPut(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? = tryPut(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
) { bodyAs(responseDeserializer) }

/**
 * A serialize-friendly version of [tryPut] that has no body but expects a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPut(apiPath, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryPut(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPut(
    apiPath,
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
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.patch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = patch(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [patch] that accepts a serializable body and returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "patch(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()",
        "com.varabyte.kobweb.browser.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.patchBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = patch(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAsBytes()

/**
 * Call PATCH on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryPatch], which will return null if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "patch(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.patch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R = patch(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [patch] that has no body but expects a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "patch(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.patch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = patch(
    apiPath,
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
suspend inline fun <reified B, T> ApiFetcher.tryPatch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryPatch(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [patch] but returns null instead of throwing if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryPatch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A serialize-friendly version of [tryPatch] that accepts a serializable body and returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.browser.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.tryPatchBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [patch], but returns null if the request fails.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this will
 * be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 *
 * @param body The body to send with the request. Make sure your class is marked with @Serializable or provide a custom
 *  [bodySerializer].
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.tryPatch(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R? = tryPatch(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
) { bodyAs(responseDeserializer) }

/**
 * A serialize-friendly version of [tryPatch] that has no body but expects a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPatch(apiPath, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryPatch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPatch(
    apiPath,
    body = null,
    headers,
    redirect,
    abortController
) { bodyAs(responseDeserializer) }

/**
 * Call DELETE on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "delete(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.delete",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.delete(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = delete(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryDelete].
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryDelete(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryDelete",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryDelete(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryDelete(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }

/**
 * Call OPTIONS on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryOptions], which will return null if the request fails.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "options(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.browser.options",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.options(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = options(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * A serialize-friendly version of [tryOptions].
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryOptions(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.tryOptions",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryOptions(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryOptions(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }