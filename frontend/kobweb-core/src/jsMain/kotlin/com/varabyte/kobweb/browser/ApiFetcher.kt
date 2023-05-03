package com.varabyte.kobweb.browser

import androidx.compose.runtime.*
import com.varabyte.kobweb.navigation.RoutePrefix
import com.varabyte.kobweb.navigation.prependIf
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.Window
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A class which can be used to abort an API request after it was made.
 *
 * All [ApiFetcher] HTTP methods accept one. You can use it like so:
 *
 * ```
 * val abortController: AbortController? by remember { mutableStateOf(null) }
 * LaunchedEffect(Unit) {
 *   abortController = AbortController()
 *   val result = window.api.get("/some/api/path", abortController = abortController)
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
 * A class which makes it easier to access a Kobweb API endpoint, instead of using [Window.fetch] directly.
 */
class ApiFetcher {
    /**
     * Returns the current body of the target [Response].
     *
     * Note that the returned bytes could be an empty array, which could mean the body wasn't set OR that it was set to
     * the empty string.
     */
    private suspend fun Response.getBodyBytes(): ByteArray {
        return suspendCoroutine { cont ->
            this.arrayBuffer().then { responseBuffer ->
                val int8Array = Int8Array(responseBuffer)
                cont.resume(ByteArray(int8Array.length) { i -> int8Array[i] })
            }.catch {
                cont.resume(ByteArray(0))
            }
        }
    }

    private fun Response.getBodyBytesAsync(result: (ByteArray) -> Unit) {
        CoroutineScope(window.asCoroutineDispatcher()).launch {
            result(getBodyBytes())
        }
    }

    /**
     * An exception that gets thrown if we receive a response whose code is not in the 200 (OK) range.
     *
     * @property bodyBytes The raw bytes of the response body, if any. They are passed in directly instead of queried
     *   from the [Response] object because that needs to happen asynchronously, and we need to create the exception
     *   message immediately.
     */
    class ResponseException(val response: Response, val bodyBytes: ByteArray?) : Exception(
        buildString {
            append("URL = ${response.url}, Status = ${response.status}, Status Text = ${response.statusText}")

            val bodyString = bodyBytes?.decodeToString()?.trim()?.takeIf { it.isNotBlank() }
            if (bodyString != null) {
                appendLine()
                val lines = bodyString.split("\n")
                val longestLineLength = lines.maxOfOrNull { it.length } ?: 0
                val indent = "  "
                val boundary = indent + "-".repeat(longestLineLength)
                appendLine(boundary)
                lines.forEach { line ->
                    appendLine(indent + line)
                }
                appendLine(boundary)
            }
        }
    )

    @NoLiveLiterals // <-- We seemed to have confused the Compose compiler. Used this to make a warning go away.
    private suspend fun fetch(method: String, apiPath: String, autoPrefix: Boolean, body: ByteArray? = null, abortController: AbortController? = null): ByteArray {
        val responseBytesDeferred = CompletableDeferred<ByteArray>()
        val requestInit = RequestInit(
            method = method,
            headers = if (body != null) {
                val headers = js("{}")
                headers["Content-Length"] = body.size
                headers["Content-Type"] = "application/octet-stream"
            } else undefined,
            body = body ?: undefined,
        )
        if (abortController != null) {
            // Hack: Workaround since Compose HTML's `RequestInit` doesn't have a `signal` property
            val requestInitDynamic: dynamic = requestInit
            requestInitDynamic["signal"] = abortController.signal
        }

        window.fetch(RoutePrefix.prependIf(autoPrefix, "/api/$apiPath"), requestInit).then(
            onFulfilled = { res ->
                if (res.ok) {
                    res.getBodyBytesAsync { bodyBytes -> responseBytesDeferred.complete(bodyBytes) }
                } else {
                    res.getBodyBytesAsync { bodyBytes ->
                        responseBytesDeferred.completeExceptionally(ResponseException(res, bodyBytes))
                    }
                }
            },
            onRejected = { t -> responseBytesDeferred.completeExceptionally(t) })

        return responseBytesDeferred.await()
    }

    private suspend fun tryFetch(method: String, apiPath: String, autoPrefix: Boolean, body: ByteArray? = null, abortController: AbortController? = null): ByteArray? {
        return try {
            fetch(method, apiPath, autoPrefix, body, abortController)
        } catch (t: Throwable) {
            if (logOnError) {
                console.log("Error fetching API endpoint \"$apiPath\"\n\n$t")
            }
            null
        }
    }

    /**
     * If true, when using any of the "try" methods, log any errors, if they occur, to the console.
     *
     * This is a useful way to debug what happened because otherwise the exception will be silently swallowed.
     *
     * This value will be set to true if you are running on a debug build, but it will default to false otherwise.
     */
    var logOnError: Boolean = false

    /**
     * Call DELETE on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryDelete], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun delete(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("DELETE", apiPath, autoPrefix, abortController = abortController)

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryDelete(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("DELETE", apiPath, autoPrefix, abortController = abortController)

    /**
     * Call GET on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryGet], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun get(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("GET", apiPath, autoPrefix, abortController = abortController)

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryGet(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("GET", apiPath, autoPrefix, abortController = abortController)

    /**
     * Call HEAD on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryHead], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun head(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("HEAD", apiPath, autoPrefix, abortController = abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryHead(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("HEAD", apiPath, autoPrefix, abortController = abortController)

    /**
     * Call OPTIONS on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryOptions], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun options(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("OPTIONS", apiPath, autoPrefix, abortController = abortController)

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryOptions(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("OPTIONS", apiPath, autoPrefix, abortController = abortController)

    /**
     * Call PATCH on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryPatch], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun patch(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("PATCH", apiPath, autoPrefix, body, abortController)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPatch(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("PATCH", apiPath, autoPrefix, body, abortController)

    /**
     * Call POST on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryPost], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun post(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("POST", apiPath, autoPrefix, body, abortController)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPost(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("POST", apiPath, autoPrefix, body, abortController)

    /**
     * Call PUT on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * See also [tryPut], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun put(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = fetch("PUT", apiPath, autoPrefix, body, abortController)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPut(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = tryFetch("PUT", apiPath, autoPrefix, body, abortController)
}

private val apiFetcherInstance = ApiFetcher()

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.api get() = apiFetcherInstance