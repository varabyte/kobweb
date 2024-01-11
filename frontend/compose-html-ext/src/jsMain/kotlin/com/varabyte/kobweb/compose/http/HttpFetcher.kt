package com.varabyte.kobweb.compose.http

import com.varabyte.kobweb.browser.http.http
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
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.AbortController` instead (that is, `compose` → `browser`).")
typealias AbortController = com.varabyte.kobweb.browser.http.AbortController

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
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.HttpFetcher` instead (that is, `compose` → `browser`).")
typealias HttpFetcher = com.varabyte.kobweb.browser.http.HttpFetcher

// "unused" = We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
// "DeprecatedCallableAddReplaceWith" = Migrating deprecated extension methods is not a good experience
@Suppress("unused", "DeprecatedCallableAddReplaceWith")
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.http.http` instead (that is, `compose` → `browser`).")
val Window.http get() = http
