package com.varabyte.kobweb.browser

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.FetchDefaults
import com.varabyte.kobweb.browser.http.getBodyBytes
import com.varabyte.kobweb.browser.http.orEmpty
import com.varabyte.kobweb.browser.http.patch
import com.varabyte.kobweb.browser.http.toRequestBody
import com.varabyte.kobweb.browser.http.tryDeserializeResponseBody
import com.varabyte.kobweb.browser.http.tryPatch
import com.varabyte.kobweb.browser.http.tryPut
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response
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
): R = get(apiPath, headers, redirect, abortController).tryDeserializeResponseBody(responseDeserializer)!!

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
): R? = tryGet(apiPath, headers, redirect, abortController)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [post] that expects a serializable body but does not expect a serialized response.
 */
suspend inline fun <reified B> ApiFetcher.post(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = post(apiPath, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [post] that expects a body and returns its response as a raw byte array.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "post(apiPath, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.post",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> ApiFetcher.postBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = post(apiPath, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()

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
): R = post(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [post] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.post(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = post(apiPath, headers, body = null, redirect, abortController).tryDeserializeResponseBody(responseDeserializer)!!

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
): Response? = tryPost(apiPath, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [tryPost] that expects a body and returns its response as a raw byte array.
 */
@Deprecated(
    "We are migrating away from returning raw bytes to a more proper Respose object instead.",
    ReplaceWith(
        "tryPost(apiPath, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.tryPost",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> ApiFetcher.tryPostBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPost(apiPath, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()

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
): R? = tryPost(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [tryPost] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.tryPost(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPost(
    apiPath,
    headers,
    body = null,
    redirect,
    abortController,
)?.tryDeserializeResponseBody(responseDeserializer)

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
): Response = put(apiPath, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [put] that expects a body and returns its response as a raw byte array.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "put(apiPath, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.put",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> ApiFetcher.putBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = put(apiPath, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()

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
): R = put(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [put] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.put(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = put(
    apiPath,
    headers,
    body = null,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

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
): Response? = tryPut(apiPath, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [tryPut] that expects a body and returns its response as a raw byte array.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "tryPut(resource, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()",
        "com.varabyte.kobweb.browser.tryPut",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> ApiFetcher.tryPutBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPut(apiPath, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()

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
): R? = tryPut(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [tryPut] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.tryPut(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPut(
    apiPath,
    headers,
    body = null,
    redirect,
    abortController,
)?.tryDeserializeResponseBody(responseDeserializer)

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
): Response = patch(apiPath, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [patch] that expects a body and returns its response as a raw byte array.
 */
@Deprecated(
    "We are migrating away from returning raw bytes to a more proper Respose object instead.",
    ReplaceWith(
        "patch(resource, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()",
        "com.varabyte.kobweb.browser.patch",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> ApiFetcher.patchBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray = patch(apiPath, body, headers, redirect, abortController, bodySerializer).getBodyBytes().orEmpty()

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
): R = patch(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer
).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [patch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.patch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = patch(
    apiPath,
    headers,
    body = null,
    redirect,
    abortController
).tryDeserializeResponseBody(responseDeserializer)!!

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
): Response? = tryPatch(apiPath, headers, body.toRequestBody(bodySerializer), redirect, abortController)

/**
 * A serialize-friendly version of [tryPatch] that expects a body and returns its response as a raw byte array.
 */
@Deprecated(
    "For these serializable type-safe extension methods, we are migrating away from returning raw bytes to a more proper `Response` object instead.",
    ReplaceWith(
        "tryPatch(resource, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()",
        "com.varabyte.kobweb.browser.tryPatch",
        "com.varabyte.kobweb.browser.http.getBodyBytes",
        "com.varabyte.kobweb.browser.http.orEmpty"
    )
)
suspend inline fun <reified B> ApiFetcher.tryPatchBytes(
    apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): ByteArray? = tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer)?.getBodyBytes()?.orEmpty()

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
): R? = tryPatch(
    apiPath,
    body,
    headers,
    redirect,
    abortController,
    bodySerializer,
)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * A serialize-friendly version of [tryPatch] that has no body but expects a serialized response.
 */
suspend inline fun <reified R> ApiFetcher.tryPatch(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryPatch(
    apiPath,
    headers,
    body = null,
    redirect,
    abortController
)?.tryDeserializeResponseBody(responseDeserializer)

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
): R = delete(apiPath, headers, redirect, abortController).tryDeserializeResponseBody(responseDeserializer)!!
/**
 * A serialize-friendly version of [tryDelete].
 */
suspend inline fun <reified R> ApiFetcher.tryDelete(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryDelete(apiPath, headers, redirect, abortController)?.tryDeserializeResponseBody(responseDeserializer)

/**
 * Call OPTIONS on a target API path with [R] as the expected return type.
 *
 * You can set [R] to `Unit` if this request doesn't expect a response body.
 *
 * See also [tryDelete], which will return null if the request fails for any reason.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified R> ApiFetcher.options(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R = options(apiPath, headers, redirect, abortController).tryDeserializeResponseBody(responseDeserializer)!!

/**
 * A serialize-friendly version of [tryOptions].
 */
suspend inline fun <reified R> ApiFetcher.tryOptions(
    apiPath: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    responseDeserializer: DeserializationStrategy<R> = serializer(),
): R? = tryOptions(apiPath, headers, redirect, abortController)?.tryDeserializeResponseBody(responseDeserializer)


