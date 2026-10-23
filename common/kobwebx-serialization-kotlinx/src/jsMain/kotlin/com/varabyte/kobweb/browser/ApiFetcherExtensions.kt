// Everything in this file is deprecated and only kept around for migration. And also, replaceWith doesn't work great
// for extension functions.
@file:Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.varabyte.kobweb.browser

import com.varabyte.kobweb.api.ApiPath
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
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.get(
    @ApiPath apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer()
): R = get(apiPath, headers, redirect, abortController).bodyAs(responseDeserializer)

/**
 * Like [get], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryGet(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryGet(
    @ApiPath apiPath: String,
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
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.post` with `import com.varabyte.kobweb.api.post`.")
suspend inline fun <reified B> ApiFetcher.post(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.api.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.postBytes(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.api.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.post(
    @ApiPath apiPath: String,
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
 * A serialize-friendly version of [post] that has no body but provides a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "post(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.api.post",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.post(
    @ApiPath apiPath: String,
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
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPost] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPost<RequestClass>().bodyAs<ReplyClass>()`
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryPost` with `import com.varabyte.kobweb.api.tryPost`.")
suspend inline fun <reified B, T> ApiFetcher.tryPost(
    @ApiPath apiPath: String,
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
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPost] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryPost` with `import com.varabyte.kobweb.api.tryPost`.")
suspend inline fun <reified B> ApiFetcher.tryPost(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A version of [tryPost] that returns its response as a raw byte array.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated(
    "We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }",
        "com.varabyte.kobweb.api.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.tryPostBytes(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [post], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.api.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.tryPost(
    @ApiPath apiPath: String,
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
 * A serialize-friendly version of [tryPost] that has no body but provides a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPost(apiPath, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.api.tryPost",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryPost(
    @ApiPath apiPath: String,
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
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.put` with `import com.varabyte.kobweb.api.put`.")
suspend inline fun <reified B> ApiFetcher.put(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.api.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.putBytes(
    @ApiPath apiPath: String,
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
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "put(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.api.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.put(
    @ApiPath apiPath: String,
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
 * A serialize-friendly version of [put] that has no body but provides a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "put(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.api.put",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.put(
    @ApiPath apiPath: String,
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
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPut] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPut<RequestClass>().bodyAs<ReplyClass>()`
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryPut` with `import com.varabyte.kobweb.api.tryPut`.")
suspend inline fun <reified B, T> ApiFetcher.tryPut(
    @ApiPath apiPath: String,
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
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPut] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryPut` with `import com.varabyte.kobweb.api.tryPut`.")
suspend inline fun <reified B> ApiFetcher.tryPut(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.api.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.tryPutBytes(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [put], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.api.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.tryPut(
    @ApiPath apiPath: String,
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
 * A serialize-friendly version of [tryPut] that has no body but provides a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPut(apiPath, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.api.tryPut",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryPut(
    @ApiPath apiPath: String,
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
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.patch` with `import com.varabyte.kobweb.api.patch`.")
suspend inline fun <reified B> ApiFetcher.patch(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.api.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.patchBytes(
    @ApiPath apiPath: String,
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
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "patch(apiPath, body, headers, redirect, abortController, bodySerializer).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.api.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.patch(
    @ApiPath apiPath: String,
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
 * A serialize-friendly version of [patch] that has no body but provides a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "patch(apiPath, body = null, headers, redirect, abortController).bodyAs(responseDeserializer)",
        "com.varabyte.kobweb.api.patch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.patch(
    @ApiPath apiPath: String,
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
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPatch] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPatch<RequestClass>().bodyAs<ReplyClass>()`
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryPatch` with `import com.varabyte.kobweb.api.tryPatch`.")
suspend inline fun <reified B, T> ApiFetcher.tryPatch(
    @ApiPath apiPath: String,
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
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPatch] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryPatch` with `import com.varabyte.kobweb.api.tryPatch`.")
suspend inline fun <reified B> ApiFetcher.tryPatch(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.api.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B> ApiFetcher.tryPatchBytes(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAsBytes() }

/**
 * Like [patch], but returns null if the request fails or the response can't be deserialized.
 *
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.api.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified B, reified R> ApiFetcher.tryPatch(
    @ApiPath apiPath: String,
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
 * A serialize-friendly version of [tryPatch] that has no body but provides a serialized response.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("With these serialization-aware network methods, we are moving response deserialization handling to a separate `bodyAs` call. This lets us accomplish the same amount of functionality with fewer methods.",
    ReplaceWith(
        "tryPatch(apiPath, body = null, headers, redirect, abortController) { bodyAs(responseDeserializer) }",
        "com.varabyte.kobweb.api.tryPatch",
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryPatch(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.delete(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryDelete(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.options(
    @ApiPath apiPath: String,
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
        "com.varabyte.kobweb.browser.http.FetchDefaults",
        "com.varabyte.kobweb.browser.http.bodyAs",
        "kotlinx.serialization.serializer",
    )
)
suspend inline fun <reified R> ApiFetcher.tryOptions(
    @ApiPath apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryOptions(apiPath, headers, redirect, abortController) { bodyAs(responseDeserializer) }

/**
 * A version of [query] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `query<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.query` with `import com.varabyte.kobweb.api.query`.")
suspend inline fun <reified B> ApiFetcher.query(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = query(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [tryQuery] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryQuery(...) { convert() }` over `tryQuery(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryQuery] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryQuery<RequestClass>().bodyAs<ReplyClass>()`
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryQuery` with `import com.varabyte.kobweb.api.tryQuery`.")
suspend inline fun <reified B, T> ApiFetcher.tryQuery(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
    noinline transform: suspend Response.() -> T
): T? = tryQuery(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController, transform)

/**
 * Like [query] but returns null instead of throwing if the request fails.
 *
 * Additionally, if `ApiFetcher.logOnError` is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryQuery] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryQuery(...) { convert() }` over `tryQuery(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
@Deprecated("Serialization-aware `ApiFetcher` extensions have moved to the `com.varabyte.kobweb.api` package. Replace `import com.varabyte.kobweb.browser.tryQuery` with `import com.varabyte.kobweb.api.tryQuery`.")
suspend inline fun <reified B> ApiFetcher.tryQuery(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryQuery(apiPath, body, headers, redirect, abortController, bodySerializer) { this }
