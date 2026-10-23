package com.varabyte.kobweb.api

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.FetchDefaults
import com.varabyte.kobweb.browser.http.bodyAs
import com.varabyte.kobweb.browser.http.toRequestBody
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response

/**
 * A version of [post] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `post<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.post(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = post(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [tryPost] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPost] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPost<RequestClass>().bodyAs<ReplyClass>()`
 */
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
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPost] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryPost(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPost(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A version of [put] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `put<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.put(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = put(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [tryPut] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPut] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPut<RequestClass>().bodyAs<ReplyClass>()`
 */
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
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPut] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryPut(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPut(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A version of [patch] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `patch<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.patch(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response = patch(apiPath, body.toRequestBody(bodySerializer), headers, redirect, abortController)

/**
 * A serialize-friendly version of [tryPatch] that accepts a serializable body and logic to convert the response to some
 * desired target object of type [T].
 *
 * A [transform] callback is provided allowing you to convert the response into a different type.
 * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()` as the former will
 * ensure that exception handling is covered in that case.
 *
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryPatch] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryPatch<RequestClass>().bodyAs<ReplyClass>()`
 */
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
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryPatch] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryPatch(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryPatch(apiPath, body, headers, redirect, abortController, bodySerializer) { this }

/**
 * A version of [query] that accepts a serializable body.
 *
 * Use [bodyAs] on the returned [Response] if you want to deserialize returned bytes, e.g.
 * `query<RequestClass>(/*...*/).bodyAs<ReplyClass>()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
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
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console (including the
 * logic in the [transform] block).
 *
 * If you do not care about converting the result to some arbitrary type, use the [tryQuery] version that returns
 * [Response?][Response] instead. For serialization-aware methods, it is expected that users will rarely, if ever, need
 * this version. Instead, via the [Response] version, use `tryQuery<RequestClass>().bodyAs<ReplyClass>()`
 */
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
 * Additionally, if [ApiFetcher.logOnError] is set to true, any failure will be logged to the console. By default, this
 * will be true for debug builds and false for release builds.
 *
 * If you plan to do additional operations on the response and would also like to have logging / exception
 * protection for them, consider using the other [tryQuery] call which lets you pass in a `transform` callback.
 * You are generally encouraged to call `tryQuery(...) { convert() }` over `tryQuery(...)?.convert()`.
 *
 * Note: you should NOT prepend your path with "api/", as that will be added automatically.
 */
suspend inline fun <reified B> ApiFetcher.tryQuery(
    @ApiPath apiPath: String,
    body: B,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null,
    bodySerializer: SerializationStrategy<B> = serializer(),
): Response? = tryQuery(apiPath, body, headers, redirect, abortController, bodySerializer) { this }
