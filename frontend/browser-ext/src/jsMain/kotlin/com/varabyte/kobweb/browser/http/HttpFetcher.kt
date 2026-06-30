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
 * A class with a slightly friendlier API than using [Window.fetch] directly by providing HTTP method helper methods.
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
 * The class additionally exposes a `logOnError` field which globally applies to all `try...` methods.
 */
class HttpFetcher(private val fetchScope: WindowOrWorkerGlobalScope) {
    /**
     * If true, when using any of the "try" methods, log any errors, if they occur, to the console.
     *
     * This is a useful way to debug what happened because otherwise the exception will be silently swallowed.
     */
    var logOnError: Boolean = false

    /**
     * Call DELETE on a target resource.
     *
     * See also [tryDelete], which will return null if the request fails for any reason.
     */
    suspend fun delete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.DELETE, resource, headers, body = null, redirect, abortController)

    /**
     * Call DELETE on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryDeleteBytes], which will return null if the request fails for any reason.
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
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
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
        headers,
        body = null,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryDelete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryDelete(resource, headers, redirect, abortController, transform = { this })

    /**
     * Like [deleteBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryDelete(resource, headers, redirect, abortController, transform = { bodyAsBytes() })",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryDeleteBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryDelete(resource, headers, redirect, abortController, transform = { bodyAsBytes() })

    /**
     * Call GET on a target resource.
     *
     * See also [tryGet], which will return null if the request fails for any reason.
     */
    suspend fun get(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.GET, resource, headers, body = null, redirect, abortController)

    /**
     * Call GET on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryGetBytes], which will return null if the request fails for any reason.
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
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
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
        headers,
        body = null,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryGet(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryGet(resource, headers, redirect, abortController, transform = { this })

    /**
     * Like [getBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryGet(resource, headers, redirect, abortController, transform = { bodyAsBytes() })",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryGetBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryGet(resource, headers, redirect, abortController, transform = { bodyAsBytes() })

    /**
     * Call HEAD on a target resource.
     *
     * See also [tryHead], which will return null if the request fails for any reason.
     */
    suspend fun head(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.HEAD, resource, headers, body = null, redirect, abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
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
        headers,
        body = null,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryHead(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryHead(resource, headers, redirect, abortController, transform = { this })

    /**
     * Call OPTIONS on a target resource.
     *
     * See also [tryOptions], which will return null if the request fails for any reason.
     */
    suspend fun options(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.OPTIONS, resource, headers, body = null, redirect, abortController)

    /**
     * Call OPTIONS on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryOptionsBytes], which will return null if the request fails for any reason.
     */
    suspend fun optionsBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = options(resource, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
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
        headers,
        body = null,
        redirect,
        logOnError,
        abortController,
        transform
    )

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryOptions(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryOptions(resource, headers, redirect, abortController, transform = { this })

    /**
     * Like [optionsBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryOptions(resource, headers, redirect, abortController, transform = { bodyAsBytes() })",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryOptionsBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryOptions(resource, headers, redirect, abortController, transform = { bodyAsBytes() })

    /**
     * Call PATCH on a target resource.
     *
     * See also [tryPatch], which will return null if the request fails for any reason.
     */
    suspend fun patch(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.PATCH, resource, headers, body, redirect, abortController)

    /**
     * Call PATCH on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPatchBytes], which will return null if the request fails for any reason.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "patch(resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun patchBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = patch(resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryPatch(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? =
        fetchScope.tryFetch(HttpMethod.PATCH, resource, headers, body, redirect, logOnError, abortController, transform)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPatch(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryPatch(resource, headers, body, redirect, abortController, transform = { this })

    /**
     * Like [patchBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPatch(resource, headers, body?.let { bodyOf(it) }, redirect, abortController, transform = { bodyAsBytes() })",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPatchBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPatch(resource, headers, body?.let { bodyOf(it) }, redirect, abortController, transform = { bodyAsBytes() })

    /**
     * Call POST on a target resource.
     *
     * See also [tryPost], which will return null if the request fails for any reason.
     */
    suspend fun post(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.POST, resource, headers, body, redirect, abortController)

    /**
     * Call POST on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPostBytes], which will return null if the request fails for any reason.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "post(resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun postBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = post(resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryPost(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? =
        fetchScope.tryFetch(HttpMethod.POST, resource, headers, body, redirect, logOnError, abortController, transform)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPost(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryPost(resource, headers, body, redirect, abortController, transform = { this })

    /**
     * Like [postBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPost(resource, headers, body?.let { bodyOf(it) }, redirect, abortController, transform = { bodyAsBytes() })",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPostBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPost(
        resource,
        headers,
        body?.let { bodyOf(it) },
        redirect,
        abortController,
        transform = { bodyAsBytes() }
    )

    /**
     * Call PUT on a target resource.
     *
     * See also [tryPut], which will return null if the request fails for any reason.
     */
    suspend fun put(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response = fetchScope.fetch(HttpMethod.PUT, resource, headers, body, redirect, abortController)

    /**
     * Call PUT on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPutBytes], which will return null if the request fails for any reason.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "put(resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun putBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = put(resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryPut(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? =
        fetchScope.tryFetch(HttpMethod.PUT, resource, headers, body, redirect, logOnError, abortController, transform)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPut(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): Response? = tryPut(resource, headers, body, redirect, abortController, transform = { this })

    /**
     * Like [putBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPut(resource, headers, body?.let { bodyOf(it) }, redirect, abortController, transform = { bodyAsBytes() })",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPutBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPut(resource, headers, body?.let { bodyOf(it) }, redirect, abortController, transform = { bodyAsBytes() })
}

val WindowOrWorkerGlobalScope.http: HttpFetcher get() = with(this.asDynamic()) {
    httpFetcher = httpFetcher ?: HttpFetcher(this@http)
    httpFetcher
}
