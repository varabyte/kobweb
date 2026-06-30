package com.varabyte.kobweb.browser.http

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.Int8Array
import org.w3c.dom.WindowOrWorkerGlobalScope
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response
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
 * An exception that gets thrown if we receive a response whose body cannot be converted into raw bytes.
 *
 * @property bodyBytes The raw bytes of the response body, if any. They are passed in directly instead of queried
 *   from the [Response] object because that needs to happen asynchronously, and we need to create the exception
 *   message immediately.
 */
class ResponseBytesException(val response: Response, val bodyBytes: ByteArray?, cause: Throwable? = null) : Exception(
    buildString {
        append("URL = ${response.url}, Status = ${response.status}, Status Text = ${response.statusText}")

        val bodyString = bodyBytes?.decodeToString()
        if (bodyString != null) {
            val indent = "  "
            appendLine()
            appendLine("${indent}Body:")
            val lines = bodyString.split("\n")
            val longestLineLength = (lines.maxOfOrNull { it.length } ?: 0).coerceIn(10, 120)
            val boundary = indent + "-".repeat(longestLineLength)
            appendLine(boundary)
            lines.forEach { line ->
                appendLine(indent + line)
            }
            appendLine(boundary)
        }
    },
    cause
)

private fun Response.bodyAsBytesPromise(): Promise<ByteArray> {
    return this.arrayBuffer().then { responseBuffer ->
        // Zero-copy conversion since ByteArray is backed by Int8Array in JS
        Int8Array(responseBuffer).unsafeCast<ByteArray>()
    }
}

private suspend fun Response.getBodyBytes(): ByteArray {
    return try {
        bodyAsBytesPromise().await()
    } catch (t: Throwable) {
        throw ResponseBytesException(this, bodyBytes = null, cause = t)
    }
}

/**
 * Returns the current body of the target [Response].
 *
 * This method will throw [ResponseBytesException] if the body cannot be read. This can be due to a connection issue,
 * for example.
 *
 * An exception will also be thrown if you try to read a body that has previously been read! So, you are only expected
 * to call this method once per response. (Remember, you can call [Response.clone] if you plan to read the contents of a
 * response more than that.)
 *
 * Be aware that responses that are not [ok][Response.ok] can still have bodies! For example, a server might add
 * details about the error in there.
 *
 * However, by default, this method is designed for the happy path where good bytes go in with a request and good bytes
 * come back out with a response. Therefore, it will throw for an response with an error code even if there's a body.
 * You can change [requireOk] if you want the body of "bad" responses to also be returned.
 */
suspend fun Response.bodyAsBytes(requireOk: Boolean = true): ByteArray {
    if (requireOk && !this.ok) {
        var cause: Throwable? = null
        val errorBytes = try {
            // Here, we clone our response because we're just trying to extract the body delicately for error logging
            // purposes, so we shouldn't consume the body as a side effect
            this.clone().bodyAsBytesPromise().await()
        } catch (t: Throwable) {
            cause = t
            null
        }
        throw ResponseBytesException(this, errorBytes, cause)
    }

    return getBodyBytes()
}

/**
 * Like [bodyAsBytes] but returns null instead of throwing an exception if something goes wrong.
 */
suspend fun Response.bodyAsBytesOrNull(requireOk: Boolean = true): ByteArray? {
    return try {
        bodyAsBytes(requireOk)
    } catch (_: ResponseBytesException) {
        null
    }
}

fun ByteArray?.orEmpty(): ByteArray {
    return this ?: ByteArray(0)
}

/**
 * Default values for [com.varabyte.kobweb.browser.http.fetch] (or methods that delegate to fetch).
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
 * [bodyAsBytes] extension method we provide on top of the [Response] class.
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
@Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "fetch(method, resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "com.varabyte.kobweb.browser.http.bodyOf",
    )
)
suspend fun WindowOrWorkerGlobalScope.fetchBytes(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    abortController: AbortController? = null
): ByteArray {
    return fetch(method, resource, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()
}

private fun logFetchResourceError(resource: String, t: Throwable) {
    console.log("Error fetching resource \"$resource\"\n\n$t")
}

/**
 * Like [com.varabyte.kobweb.browser.http.fetch] but returns null if the fetch fails for any reason instead of throwing.
 *
 * @param transform A final step to convert the response into a different type. Any exception that is thrown while
 *   this method's logic is being run will automatically be caught and, if [logOnError] is true, reported. You can use
 *   the [tryFetch] method that returns a [Response?][Response] instead if you don't need to convert it.
 */
suspend fun <T> WindowOrWorkerGlobalScope.tryFetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: RequestBody? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    logOnError: Boolean = false,
    abortController: AbortController? = null,
    transform: suspend (Response) -> T
): T? {
    return try {
        val res = fetch(method, resource, headers, body, redirect, abortController)
        transform(res)
    } catch (t: Throwable) {
        if (logOnError) logFetchResourceError(resource, t)
        null
    }
}

/**
 * Like [com.varabyte.kobweb.browser.http.fetch] but returns null if the fetch fails for any reason instead of throwing.
 */
suspend fun WindowOrWorkerGlobalScope.tryFetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: RequestBody? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    logOnError: Boolean = false,
    abortController: AbortController? = null,
): Response? {
    return tryFetch(method, resource, headers, body, redirect, logOnError, abortController, transform = { it })
}

/**
 * Like [fetchBytes] but returns null if the fetch fails for any reason instead of throwing.
 */
@Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
    ReplaceWith(
        "tryFetch(method, resource, headers, body?.let { bodyOf(it) }, redirect, logOnError, abortController, transform = { it.bodyAsBytes() })",
        "com.varabyte.kobweb.browser.http.bodyAsBytes",
        "com.varabyte.kobweb.browser.http.bodyOf",
    )
)
suspend fun WindowOrWorkerGlobalScope.tryFetchBytes(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = FetchDefaults.Headers,
    body: ByteArray? = null,
    redirect: RequestRedirect? = FetchDefaults.Redirect,
    logOnError: Boolean = false,
    abortController: AbortController? = null
): ByteArray? {
    return tryFetch(method, resource, headers, body?.let { bodyOf(it) }, redirect, logOnError, abortController, transform = { it.bodyAsBytes() })
}
