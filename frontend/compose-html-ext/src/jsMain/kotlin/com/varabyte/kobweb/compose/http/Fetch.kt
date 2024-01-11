@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.compose.http

import androidx.compose.runtime.*
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
import kotlin.js.json
import com.varabyte.kobweb.browser.http.fetch
import com.varabyte.kobweb.browser.http.tryFetch

@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.HttpMethod` instead (that is, `compose` → `browser`).")
typealias HttpMethod = com.varabyte.kobweb.browser.http.HttpMethod

/**
 * An exception that gets thrown if we receive a response whose code is not in the 200 (OK) range.
 *
 * @property bodyBytes The raw bytes of the response body, if any. They are passed in directly instead of queried
 *   from the [Response] object because that needs to happen asynchronously, and we need to create the exception
 *   message immediately.
 */
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.ResponseException` instead (that is, `compose` → `browser`).")
typealias ResponseException = com.varabyte.kobweb.browser.http.ResponseException

/**
 * A Kotlin-idiomatic version of the standard library's [Window.fetch] function.
 *
 * @param headers An optional map of headers to send with the request. Note: If a body is specified, the
 *   `Content-Length` and `Content-Type` headers will be automatically set. Setting them manually here will result in
 *   those values getting overridden.
 */
// Needed to calm down the Compose compiler for some reason: "Duplicate live literal key found"
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.fetch` instead (that is, `compose` → `browser`).")
suspend fun Window.fetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = null,
    body: ByteArray? = null,
    abortController: AbortController? = null
): ByteArray {
    return fetch(method, resource, headers, body, abortController)
}

@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.tryFetch` instead (that is, `compose` → `browser`).")
suspend fun Window.tryFetch(
    method: HttpMethod,
    resource: String,
    headers: Map<String, Any>? = null,
    body: ByteArray? = null,
    logOnError: Boolean = false,
    abortController: AbortController? = null
): ByteArray? {
    return tryFetch(method, resource, headers, body, logOnError, abortController)
}
