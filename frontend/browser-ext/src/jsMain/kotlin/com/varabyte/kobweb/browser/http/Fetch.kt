package com.varabyte.kobweb.browser.http

import com.varabyte.kobweb.browser.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.WindowOrWorkerGlobalScope
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response
import kotlin.coroutines.resume
import kotlin.js.Promise
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
 * The returned value will be null if the response did not have a body.
 *
 * Note that even if [Response.ok] is false, it might still have a body which will get returned here.
 */
suspend fun Response.getBodyBytes(): ByteArray? {
    return suspendCancellableCoroutine { cont ->
        val _ = this.arrayBuffer().then { responseBuffer ->
            val int8Array = Int8Array(responseBuffer)
            cont.resume(ByteArray(int8Array.length) { i -> int8Array[i] })
        }.catch {
            cont.resume(null)
        }
    }
}
fun ByteArray?.orEmpty(): ByteArray {
    return this ?: ByteArray(0)
}

private fun Response.getBodyBytesAsync(dispatcher: CoroutineDispatcher, result: (ByteArray?) -> Unit) {
    CoroutineScope(dispatcher).launch { result(getBodyBytes()) }
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

        val bodyString = bodyBytes?.decodeToString()
        if (bodyString != null) {
            val indent = "  "
            appendLine()
            appendLine("${indent}Body:")
            val lines = bodyString.split("\n")
            val longestLineLength = (lines.maxOfOrNull { it.length } ?: 0).coerceAtLeast(10)
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
 * A Kotlin-idiomatic version of the standard library's [Window.fetch][WindowOrWorkerGlobalScope.fetch] function.
 *
 * This method is a suspend function, so it returns a [Response] directly instead of returning a [Promise]. It also adds
 * a slew of additional, useful parameters that help configure the fetch, without needing to use the [RequestInit]
 * object or any `dynamic` values.
 *
 * If the request fails to connect, this method throws; but if the server replies that the response is bad, it will be
 * returned. Just be sure to check [Response.ok] before using it.
 *
 * If all you care about is the response payload, you can use the [fetchBytes] convenience method instead, or the
 * [getBodyBytes] extension method we provide on top of the [Response] class.
 *
 * @param headers An optional map of headers to send with the request. The "Content-Type" header may be automatically
 *   set if the `body` parameter is present, but anything specified manually will take precedence.
 */
suspend fun WindowOrWorkerGlobalScope.fetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: RequestBody? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null
): Response {
    val headersJson = if (!headers.isNullOrEmpty() || body != null) {
        json().apply {
            when (body) {
                is RequestBody.OfBlob -> {
                    body.blob.type
                }
                is RequestBody.OfArrayBuffer -> {
                    body.contentType
                }
                is RequestBody.OfFormData -> {
                    // Do nothing, handled by browser
                    null
                }
                is RequestBody.OfText -> {
                    body.contentType
                }
                is RequestBody.OfUrlEncoded -> {
                    "application/x-www-form-urlencoded; charset=UTF-8"
                }
                null -> null
            }?.let { contentType: String ->
                this["Content-Type"] = contentType
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
        body = when (body) {
            is RequestBody.OfBlob -> body.blob
            is RequestBody.OfArrayBuffer -> body.buffer
            is RequestBody.OfFormData -> body.formData
            is RequestBody.OfText -> body.text
            is RequestBody.OfUrlEncoded -> body.params
            null -> undefined
        },
        redirect = redirect ?: undefined,
    )
    if (abortController != null) {
        // Hack: Workaround since Compose HTML's `RequestInit` doesn't have a `signal` property
        val requestInitDynamic: dynamic = requestInit
        requestInitDynamic["signal"] = abortController.signal
    }

    val responseDeferred = CompletableDeferred<Response>()
    val _ = fetch(resource, requestInit).then(
        onFulfilled = { res -> responseDeferred.complete(res) },
        onRejected = { t -> responseDeferred.completeExceptionally(t) })

    return responseDeferred.await()
}

/**
 * A convenience method for [fetch] built around the common case of sending / receiving raw bytes.
 *
 * Note that if a response is returned from the server with an error status (like permission denied, etc.), this
 * method will throw. Otherwise, if a response does not have a body, an empty byte array will be returned.
 */
suspend fun WindowOrWorkerGlobalScope.fetchBytes(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null
): ByteArray {
    val res = fetch(method, resource, headers, body?.let { bodyOf(it) }, redirect, abortController)

    val responseBytesDeferred = CompletableDeferred<ByteArray>()
    if (res.ok) {
        res.getBodyBytesAsync(this.asCoroutineDispatcher()) { bodyBytes -> responseBytesDeferred.complete(bodyBytes.orEmpty()) }
    } else {
        res.getBodyBytesAsync(this.asCoroutineDispatcher()) { bodyBytes ->
            responseBytesDeferred.completeExceptionally(ResponseException(res, bodyBytes))
        }
    }

    return responseBytesDeferred.await()
}

private fun logFetchResourceError(resource: String, t: Throwable) {
    console.log("Error fetching resource \"$resource\"\n\n$t")
}

/**
 * Like [fetch] but returns null if the fetch fails for any reason instead of throwing.
 */
suspend fun WindowOrWorkerGlobalScope.tryFetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: RequestBody? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    logOnError: Boolean = false,
    abortController: AbortController? = null
): Response? {
    return try {
        fetch(method, resource, headers, body, redirect, abortController)
    } catch (t: Throwable) {
        if (logOnError) logFetchResourceError(resource, t)
        null
    }
}

/**
 * Like [fetchBytes] but returns null if the fetch fails for any reason instead of throwing.
 */
suspend fun WindowOrWorkerGlobalScope.tryFetchBytes(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    logOnError: Boolean = false,
    abortController: AbortController? = null
): ByteArray? {
    return try {
        fetchBytes(method, resource, headers, body, redirect, abortController)
    } catch (t: Throwable) {
        if (logOnError) logFetchResourceError(resource, t)
        null
    }
}
