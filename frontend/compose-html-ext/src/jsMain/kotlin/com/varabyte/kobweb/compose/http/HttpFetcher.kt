package com.varabyte.kobweb.compose.http

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.khronos.webgl.get
import org.w3c.dom.Window

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
class HttpFetcher(private val window: Window) {
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
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.DELETE, resource, headers, body = null, abortController)

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryDelete(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.DELETE, resource, headers, body = null, logOnError, abortController)

    /**
     * Call GET on a target resource.
     *
     * See also [tryGet], which will return null if the request fails for any reason.
     */
    suspend fun get(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.GET, resource, headers, body = null, abortController)

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryGet(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.GET, resource, headers, body = null, logOnError, abortController)

    /**
     * Call HEAD on a target resource.
     *
     * See also [tryHead], which will return null if the request fails for any reason.
     */
    suspend fun head(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.HEAD, resource, headers, body = null, abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryHead(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.HEAD, resource, headers, body = null, logOnError, abortController)

    /**
     * Call OPTIONS on a target resource.
     *
     * See also [tryOptions], which will return null if the request fails for any reason.
     */
    suspend fun options(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.OPTIONS, resource, headers, body = null, abortController)

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryOptions(
        resource: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.OPTIONS, resource, headers, body = null, logOnError, abortController)

    /**
     * Call PATCH on a target resource.
     *
     * See also [tryPatch], which will return null if the request fails for any reason.
     */
    suspend fun patch(
        resource: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.PATCH, resource, headers, body, abortController)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPatch(
        resource: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.PATCH, resource, headers, body, logOnError, abortController)

    /**
     * Call POST on a target resource.
     *
     * See also [tryPost], which will return null if the request fails for any reason.
     */
    suspend fun post(
        resource: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.POST, resource, headers, body, abortController)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPost(
        resource: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.POST, resource, headers, body, logOnError, abortController)

    /**
     * Call PUT on a target resource.
     *
     * See also [tryPut], which will return null if the request fails for any reason.
     */
    suspend fun put(
        resource: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null
    ): ByteArray = window.fetch(HttpMethod.PUT, resource, headers, body, abortController)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console.
     */
    suspend fun tryPut(
        resource: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null
    ): ByteArray? = window.tryFetch(HttpMethod.PUT, resource, headers, body, logOnError, abortController)
}

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.http by lazy { HttpFetcher(window) }
