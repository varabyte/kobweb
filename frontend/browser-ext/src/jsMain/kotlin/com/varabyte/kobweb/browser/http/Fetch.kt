package com.varabyte.kobweb.browser.http

import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.Window
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json

enum class HttpMethod {
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT,
}

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

/**
 * Default values for [fetch] (or methods that delegate to fetch).
 */
object FetchDefaults {
    var Headers: Map<String, Any>? = null
    var Redirect: RequestRedirect? = null
}

/**
 * A Kotlin-idiomatic version of the standard library's [Window.fetch] function.
 *
 * @param headers An optional map of headers to send with the request. Note: If a body is specified, the
 *   `Content-Length` header will be automatically set. However, any headers set manually will always take precedence.
 */
suspend fun Window.fetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null
): ByteArray {
    val responseBytesDeferred = CompletableDeferred<ByteArray>()
    val headersJson = if (!headers.isNullOrEmpty() || body != null) {
        json().apply {
            if (body != null) {
                this["Content-Length"] = body.size
            }
            headers?.let { headers ->
                for ((key, value) in headers) {
                    this[key] = value
                }
            }
        }
    } else null

    val requestInit = RequestInit(
        method = method.name,
        headers = headersJson ?: undefined,
        body = body ?: undefined,
        redirect = redirect ?: undefined,
    )
    if (abortController != null) {
        // Hack: Workaround since Compose HTML's `RequestInit` doesn't have a `signal` property
        val requestInitDynamic: dynamic = requestInit
        requestInitDynamic["signal"] = abortController.signal
    }

    window.fetch(resource, requestInit).then(
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

suspend fun Window.tryFetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    logOnError: Boolean = false,
    abortController: AbortController? = null
): ByteArray? {
    return try {
        fetch(method, resource, headers, body, redirect, abortController)
    } catch (t: Throwable) {
        if (logOnError) {
            console.log("Error fetching resource \"$resource\"\n\n$t")
        }
        null
    }
}
