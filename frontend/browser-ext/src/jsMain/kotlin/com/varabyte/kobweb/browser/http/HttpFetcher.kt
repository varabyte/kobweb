package com.varabyte.kobweb.browser.http

import org.w3c.dom.WindowOrWorkerGlobalScope
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response

/**
 * A class which can be used to abort an API request after it was made.
 *
 * ```
 * var abortController: AbortController? by remember { mutableStateOf(null) }
 * LaunchedEffect(Unit) {
 *   abortController = AbortController()
 *   val result = window.http.get("/some/api/path", abortController = abortController)
 *   abortController = null
 * }
 *
 * Button(onClick = { abortController?.abort() }) {
 *   Text("Abort")
 * }
 * ```
 *
 * Note that if you re-use the same abort controller across multiple requests, one abort call will abort them all. And
 * if you pass an already aborted controller into a new request, it will fail immediately.
 */
class AbortController {
    private val controller = js("new AbortController()")

    internal val signal = controller.signal

    fun abort() {
        controller.abort()
    }
}

/**
 * A class with a slightly friendlier API than using [Window.fetch][org.w3c.dom.Window.fetch] directly by providing HTTP
 * method helper methods.
 *
 * For example:
 *
 * ```
 * // Without HttpFetcher
 * window.fetch(HttpMethod.GET, "/some/api/path")
 *
 * // With HttpFetcher
 * window.http.get("/some/api/path")
 * // ... or a version that won't throw exceptions
 * // window.http.tryGet("/some/api/path")
 * ```
 *
 * The class additionally exposes a [logOnError] property which globally applies to all `try...` methods.
 */
class HttpFetcher(private val fetchScope: WindowOrWorkerGlobalScope) {
    /**
     * If true, when using any of the "try" methods, logs any errors, if they occur, to the console.
     *
     * This is a useful way to debug what happened because otherwise the exception will be silently swallowed.
     *
     * This value will be set to true if you are running on a debug build, but it will default to false otherwise.
     */
    var logOnError: Boolean = false

    /**
     * Call DELETE on a target resource.
     *
     * See also [tryDelete], which will return null if the request fails.
     */
    suspend fun delete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.DELETE, resource, body = null, headers, redirect, abortController)

    /**
     * Call DELETE on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryDeleteBytes], which will return null if the request fails.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "delete(resource, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun deleteBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = delete(resource, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [delete], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryDelete(...) { convert() }` over `tryDelete(...)?.convert()` as the
     * former will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryDelete] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryDelete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = fetchScope.tryFetch(
        HttpMethod.DELETE,
        resource,
        body = null,
        headers,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [delete], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryDelete] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryDelete(...) { convert() }` over `tryDelete(...)?.convert()`.
     */
    suspend fun tryDelete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryDelete(resource, headers, redirect, abortController) { this }

    /**
     * Like [deleteBytes], but returns null instead of throwing if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryDelete(resource, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryDeleteBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryDelete(resource, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call GET on a target resource.
     *
     * See also [tryGet], which will return null if the request fails.
     */
    suspend fun get(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.GET, resource, body = null, headers, redirect, abortController)

    /**
     * Call GET on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryGetBytes], which will return null if the request fails.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "get(resource, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun getBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = get(resource, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [get], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryGet(...) { convert() }` over `tryGet(...)?.convert()` as the former will
     * ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryGet] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryGet(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = fetchScope.tryFetch(
        HttpMethod.GET,
        resource,
        body = null,
        headers,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [get], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryGet] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryGet(...) { convert() }` over `tryGet(...)?.convert()`.
     */
    suspend fun tryGet(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryGet(resource, headers, redirect, abortController) { this }

    /**
     * Like [getBytes], but returns null instead of throwing if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryGet(resource, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryGetBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryGet(resource, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call HEAD on a target resource.
     *
     * See also [tryHead], which will return null if the request fails.
     */
    suspend fun head(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.HEAD, resource, body = null, headers, redirect, abortController)

    /**
     * Like [head], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryHead(...) { convert() }` over `tryHead(...)?.convert()` as the former
     * will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryHead] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryHead(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = fetchScope.tryFetch(
        HttpMethod.HEAD,
        resource,
        body = null,
        headers,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [head], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryHead] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryHead(...) { convert() }` over `tryHead(...)?.convert()`.
     */
    suspend fun tryHead(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryHead(resource, headers, redirect, abortController) { this }

    /**
     * Call OPTIONS on a target resource.
     *
     * See also [tryOptions], which will return null if the request fails.
     */
    suspend fun options(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.OPTIONS, resource, body = null, headers, redirect, abortController)

    /**
     * Call OPTIONS on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryOptionsBytes], which will return null if the request fails.
     */
    suspend fun optionsBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = options(resource, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [options], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryOptions(...) { convert() }` over `tryOptions(...)?.convert()` as the
     * former will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryOptions] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryOptions(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = fetchScope.tryFetch(
        HttpMethod.OPTIONS,
        resource,
        body = null,
        headers,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [options], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryOptions] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryOptions(...) { convert() }` over `tryOptions(...)?.convert()`.
     */
    suspend fun tryOptions(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryOptions(resource, headers, redirect, abortController) { this }

    /**
     * Like [optionsBytes], but returns null instead of throwing if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryOptions(resource, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryOptionsBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryOptions(resource, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call PATCH on a target resource.
     *
     * See also [tryPatch], which will return null if the request fails.
     */
    suspend fun patch(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.PATCH, resource, body, headers, redirect, abortController)

    /**
     * Call PATCH on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPatchBytes], which will return null if the request fails.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "patch(resource, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun patchBytes(
        resource: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = patch(resource, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [patch], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()` as the former
     * will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryPatch] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryPatch(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? =
        fetchScope.tryFetch(HttpMethod.PATCH, resource, body, headers, redirect, logOnError, abortController, transform)

    /**
     * Like [patch], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryPatch] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()`.
     */
    suspend fun tryPatch(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryPatch(resource, body, headers, redirect, abortController) { this }

    /**
     * Like [patchBytes], but returns null instead of throwing if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPatch(resource, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPatchBytes(
        resource: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPatch(resource, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call POST on a target resource.
     *
     * See also [tryPost], which will return null if the request fails.
     */
    suspend fun post(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.POST, resource, body, headers, redirect, abortController)

    /**
     * Call POST on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPostBytes], which will return null if the request fails.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "post(resource, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun postBytes(
        resource: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = post(resource, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [post], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()` as the former
     * will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryPost] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryPost(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? =
        fetchScope.tryFetch(HttpMethod.POST, resource, body, headers, redirect, logOnError, abortController, transform)

    /**
     * Like [post], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryPost] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()`.
     */
    suspend fun tryPost(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryPost(resource, body, headers, redirect, abortController) { this }

    /**
     * Like [postBytes], but returns null instead of throwing if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPost(resource, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPostBytes(
        resource: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPost(
        resource,
        body?.let { bodyOf(it) },
        headers,
        redirect,
        abortController
    ) { bodyAsBytes() }

    /**
     * Call PUT on a target resource.
     *
     * See also [tryPut], which will return null if the request fails.
     */
    suspend fun put(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.PUT, resource, body, headers, redirect, abortController)

    /**
     * Call PUT on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPutBytes], which will return null if the request fails.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "put(resource, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun putBytes(
        resource: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = put(resource, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [put], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()` as the former will
     * ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryPut] version that returns [Response?][Response]
     * instead.
     */
    suspend fun <T> tryPut(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? =
        fetchScope.tryFetch(HttpMethod.PUT, resource, body, headers, redirect, logOnError, abortController, transform)

    /**
     * Like [put], but returns null instead of throwing if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * If you plan to do additional operations on the response and would also like to have logging / exception
     * protection for them, consider using the other [tryPut] call which lets you pass in a `transform` callback.
     * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()`.
     */
    suspend fun tryPut(
        resource: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryPut(resource, body, headers, redirect, abortController) { this }

    /**
     * Like [putBytes], but returns null instead of throwing if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPut(resource, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPutBytes(
        resource: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPut(resource, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }
}

val WindowOrWorkerGlobalScope.http: HttpFetcher get() = with(this.asDynamic()) {
    httpFetcher = httpFetcher ?: HttpFetcher(this@http)
    httpFetcher
}
