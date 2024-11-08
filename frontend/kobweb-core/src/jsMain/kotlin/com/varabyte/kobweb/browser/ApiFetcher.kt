package com.varabyte.kobweb.browser

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.fetch
import com.varabyte.kobweb.browser.http.http
import com.varabyte.kobweb.navigation.BasePath
import kotlinx.browser.window
import org.khronos.webgl.get
import org.w3c.dom.Window

/**
 * A class which makes it easier to access a Kobweb API endpoint, instead of using [Window.fetch] directly.
 *
 * This class works by wrapping [Window.http] but specifically for Kobweb API calls (URLs which are prefixed with
 * "/api/").
 */
@Suppress("MemberVisibilityCanBePrivate") // It's an API...
class ApiFetcher(private val window: Window) {
    /**
     * If true, when using any of the "try" methods, log any errors, if they occur, to the console.
     *
     * This is a useful way to debug what happened because otherwise the exception will be silently swallowed.
     *
     * This value will be set to true if you are running on a debug build, but it will default to false otherwise.
     */
    var logOnError: Boolean by window.http::logOnError

    private fun toResource(apiPath: String): String {
        return BasePath.prepend("/api/${apiPath.trimStart('/')}")
    }

    /**
     * Call DELETE on a target API path.
     *
     * See also [tryDelete], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun delete(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.delete(toResource(apiPath), headers, abortController)

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryDelete(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryDelete(toResource(apiPath), headers, abortController)

    /**
     * Call GET on a target API path.
     *
     * See also [tryGet], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun get(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.get(toResource(apiPath), headers, abortController)

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryGet(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryGet(toResource(apiPath), headers, abortController)

    /**
     * Call HEAD on a target API path.
     *
     * See also [tryHead], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun head(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.head(toResource(apiPath), headers, abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryHead(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryHead(toResource(apiPath), headers, abortController)

    /**
     * Call OPTIONS on a target API path.
     *
     * See also [tryOptions], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun options(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.options(toResource(apiPath), headers, abortController)

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryOptions(
        apiPath: String,
        headers: Map<String, Any>? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryOptions(toResource(apiPath), headers, abortController)

    /**
     * Call PATCH on a target API path.
     *
     * See also [tryPatch], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun patch(
        apiPath: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.patch(toResource(apiPath), headers, body, abortController)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPatch(
        apiPath: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryPatch(toResource(apiPath), headers, body, abortController)

    /**
     * Call POST on a target API path.
     *
     * See also [tryPost], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun post(
        apiPath: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.post(toResource(apiPath), headers, body, abortController)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPost(
        apiPath: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryPost(toResource(apiPath), headers, body, abortController)

    /**
     * Call PUT on a target API path.
     *
     * See also [tryPut], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun put(
        apiPath: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null,
    ): ByteArray = window.http.put(toResource(apiPath), headers, body, abortController)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPut(
        apiPath: String,
        headers: Map<String, Any>? = null,
        body: ByteArray? = null,
        abortController: AbortController? = null,
    ): ByteArray? = window.http.tryPut(toResource(apiPath), headers, body, abortController)
}

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.api by lazy { ApiFetcher(window) }
