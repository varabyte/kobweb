package com.varabyte.kobweb.browser.http

import kotlinx.browser.window
import org.w3c.dom.Window
import org.w3c.dom.WindowOrWorkerGlobalScope
import org.w3c.fetch.RequestRedirect

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
    @Deprecated("DO NOT IGNORE. Please change to `deleteBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("deleteBytes(resource, headers, redirect, abortController)"))
    suspend fun delete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = deleteBytes(resource, headers, redirect, abortController)

    /**
     * Call DELETE on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryDeleteBytes], which will return null if the request fails for any reason.
     */
    suspend fun deleteBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.DELETE, resource, headers, body = null, redirect, abortController)

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryDelete` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryDeleteBytes(resource, headers, redirect, abortController)"))
    suspend fun tryDelete(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryDeleteBytes(resource, headers, redirect, abortController)

    /**
     * Like [deleteBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryDeleteBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? =
        fetchScope.tryFetchBytes(HttpMethod.DELETE, resource, headers, body = null, redirect, logOnError, abortController)

    /**
     * Call GET on a target resource.
     *
     * See also [tryGet], which will return null if the request fails for any reason.
     */
    @Deprecated("DO NOT IGNORE. Please change to `getBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("getBytes(resource, headers, redirect, abortController)"))
    suspend fun get(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = getBytes(resource, headers, redirect, abortController)

    /**
     * Call GET on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryGetBytes], which will return null if the request fails for any reason.
     */
    suspend fun getBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.GET, resource, headers, body = null, redirect, abortController)

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryGetBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryGetBytes(resource, headers, redirect, abortController)"))
    suspend fun tryGet(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryGetBytes(resource, headers, redirect, abortController)

    /**
     * Like [getBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryGetBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? =
        fetchScope.tryFetchBytes(HttpMethod.GET, resource, headers, body = null, redirect, logOnError, abortController)

    /**
     * Call HEAD on a target resource.
     *
     * See also [tryHead], which will return null if the request fails for any reason.
     */
    @Deprecated("DO NOT IGNORE. Please change to `headBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("headBytes(resource, headers, redirect, abortController)"))
    suspend fun head(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = headBytes(resource, headers, redirect, abortController)

    /**
     * Call HEAD on a target resource, returning the response body as a raw array of bytes.
     *
     * See also [tryHeadBytes], which will return null if the request fails for any reason.
     */
    suspend fun headBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.HEAD, resource, headers, body = null, redirect, abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryHeadBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryHeadBytes(resource, headers, redirect, abortController)"))
    suspend fun tryHead(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryHeadBytes(resource, headers, redirect, abortController)

    /**
     * Like [headBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryHeadBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? =
        fetchScope.tryFetchBytes(HttpMethod.HEAD, resource, headers, body = null, redirect, logOnError, abortController)

    /**
     * Call OPTIONS on a target resource.
     *
     * See also [tryOptions], which will return null if the request fails for any reason.
     */
    @Deprecated("DO NOT IGNORE. Please change to `optionsBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("optionsBytes(resource, headers, redirect, abortController)"))
    suspend fun options(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = optionsBytes(resource, headers, redirect, abortController)

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
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.OPTIONS, resource, headers, body = null, redirect, abortController)

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryOptionsBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryOptionsBytes(resource, headers, redirect, abortController)"))
    suspend fun tryOptions(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryOptionsBytes(resource, headers, redirect, abortController)

    /**
     * Like [optionsBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryOptionsBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? =
        fetchScope.tryFetchBytes(HttpMethod.OPTIONS, resource, headers, body = null, redirect, logOnError, abortController)

    /**
     * Call PATCH on a target resource.
     *
     * See also [tryPatch], which will return null if the request fails for any reason.
     */
    @Deprecated("DO NOT IGNORE. Please change to `patchBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("patchBytes(resource, headers, body, redirect, abortController)"))
    suspend fun patch(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = patchBytes(resource, headers, body, redirect, abortController)

    /**
     * Call PATCH on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPatchBytes], which will return null if the request fails for any reason.
     */
    suspend fun patchBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.PATCH, resource, headers, body, redirect, abortController)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryPatchBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryPatchBytes(resource, headers, body, redirect, abortController)"))
    suspend fun tryPatch(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPatchBytes(resource, headers, body, redirect, abortController)

    /**
     * Like [patchBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPatchBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? =
        fetchScope.tryFetchBytes(HttpMethod.PATCH, resource, headers, body, redirect, logOnError, abortController)

    /**
     * Call POST on a target resource.
     *
     * See also [tryPost], which will return null if the request fails for any reason.
     */
    @Deprecated("DO NOT IGNORE. Please change to `postBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("postBytes(resource, headers, body, redirect, abortController)"))
    suspend fun post(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = postBytes(resource, headers, body, redirect, abortController)

    /**
     * Call POST on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPostBytes], which will return null if the request fails for any reason.
     */
    suspend fun postBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.POST, resource, headers, body, redirect, abortController)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryPostBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryPostBytes(resource, headers, body, redirect, abortController)"))
    suspend fun tryPost(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPostBytes(resource, headers, body, redirect, abortController)

    /**
     * Like [postBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPostBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? =
        fetchScope.tryFetchBytes(HttpMethod.POST, resource, headers, body, redirect, logOnError, abortController)

    /**
     * Call PUT on a target resource.
     *
     * See also [tryPut], which will return null if the request fails for any reason.
     */
    @Deprecated("DO NOT IGNORE. Please change to `putBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("putBytes(resource, headers, body, redirect, abortController)"))
    suspend fun put(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = putBytes(resource, headers, body, redirect, abortController)

    /**
     * Call PUT on a target resource, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPutBytes], which will return null if the request fails for any reason.
     */
    suspend fun putBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray = fetchScope.fetchBytes(HttpMethod.PUT, resource, headers, body, redirect, abortController)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    @Deprecated("DO NOT IGNORE. Please change to `tryPutBytes` instead. This method will be modified soon in a backwards incompatible way, in order to support additional cases that the current form doesn't support.", replaceWith = ReplaceWith("tryPutBytes(resource, headers, body, redirect, abortController)"))
    suspend fun tryPut(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = tryPutBytes(resource, headers, body, redirect, abortController)

    /**
     * Like [putBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPutBytes(
        resource: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null
    ): ByteArray? = fetchScope.tryFetchBytes(HttpMethod.PUT, resource, headers, body, redirect, logOnError, abortController)
}

val WindowOrWorkerGlobalScope.http: HttpFetcher get() = with(this.asDynamic()) {
    httpFetcher = httpFetcher ?: HttpFetcher(this)
    httpFetcher
}
