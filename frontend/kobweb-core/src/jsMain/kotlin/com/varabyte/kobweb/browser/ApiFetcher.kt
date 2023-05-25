package com.varabyte.kobweb.browser

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.http.AbortController
import com.varabyte.kobweb.compose.http.HttpMethod
import com.varabyte.kobweb.compose.http.fetch
import com.varabyte.kobweb.compose.http.tryFetch
import com.varabyte.kobweb.navigation.RoutePrefix
import com.varabyte.kobweb.navigation.prependIf
import kotlinx.browser.window
import org.khronos.webgl.get
import org.w3c.dom.Window

/**
 * A class which makes it easier to access a Kobweb API endpoint, instead of using [Window.fetch] directly.
 */
class ApiFetcher(private val window: Window) {
    /**
     * If true, when using any of the "try" methods, log any errors, if they occur, to the console.
     *
     * This is a useful way to debug what happened because otherwise the exception will be silently swallowed.
     *
     * This value will be set to true if you are running on a debug build, but it will default to false otherwise.
     */
    var logOnError: Boolean = false

    private fun toResource(apiPath: String, autoPrefix: Boolean): String {
        return RoutePrefix.prependIf(autoPrefix, "/api/$apiPath")
    }

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
    suspend fun delete(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.DELETE, toResource(apiPath, autoPrefix), body = null, abortController)

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryDelete(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.DELETE, toResource(apiPath, autoPrefix), body = null, logOnError, abortController)

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
    suspend fun get(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.GET, toResource(apiPath, autoPrefix), body = null, abortController)

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryGet(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.GET, toResource(apiPath, autoPrefix), body = null, logOnError, abortController)

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
    suspend fun head(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.HEAD, toResource(apiPath, autoPrefix), body = null, abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryHead(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.HEAD, toResource(apiPath, autoPrefix), body = null, logOnError, abortController)

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
    suspend fun options(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.OPTIONS, toResource(apiPath, autoPrefix), body = null, abortController)

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryOptions(apiPath: String, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.OPTIONS, toResource(apiPath, autoPrefix), body = null, logOnError, abortController)

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
    suspend fun patch(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.PATCH, toResource(apiPath, autoPrefix), body, abortController)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPatch(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.PATCH, toResource(apiPath, autoPrefix), body, logOnError, abortController)

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
    suspend fun post(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.POST, toResource(apiPath, autoPrefix), body, abortController)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPost(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.POST, toResource(apiPath, autoPrefix), body, logOnError, abortController)

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
    suspend fun put(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray = window.fetch(HttpMethod.PUT, toResource(apiPath, autoPrefix), body, abortController)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPut(apiPath: String, body: ByteArray? = null, abortController: AbortController? = null, autoPrefix: Boolean = true): ByteArray? = window.tryFetch(HttpMethod.PUT, toResource(apiPath, autoPrefix), body, logOnError, abortController)
}

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.api by lazy { ApiFetcher(window) }