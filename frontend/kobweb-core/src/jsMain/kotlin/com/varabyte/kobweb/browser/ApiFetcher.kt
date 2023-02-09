package com.varabyte.kobweb.browser

import androidx.compose.runtime.*
import com.varabyte.kobweb.navigation.RoutePrefix
import com.varabyte.kobweb.navigation.prependIf
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.Window
import org.w3c.fetch.RequestInit

/**
 * A class which makes it easier to access a Kobweb API endpoint, instead of using [Window.fetch] directly.
 */
class ApiFetcher {
    @NoLiveLiterals // <-- We seemed to have confused the Compose compiler. Used this to make a warning go away.
    private suspend fun fetch(method: String, apiPath: String, autoPrefix: Boolean, body: ByteArray? = null): ByteArray? {
        val responseBytesDeferred = CompletableDeferred<ByteArray?>()
        val requestInit = RequestInit(
            method = method,
            headers = if (body != null) {
                val headers = js("{}")
                headers["Content-Length"] = body.size
                headers["Content-Type"] = "application/octet-stream"
            } else undefined,
            body = body ?: undefined,
        )

        window.fetch(RoutePrefix.prependIf(autoPrefix, "/api/$apiPath"), requestInit).then(
            onFulfilled = { res ->
                if (res.ok) {
                    res.arrayBuffer().then { responseBuffer ->
                        val int8Array = Int8Array(responseBuffer)
                        responseBytesDeferred.complete(ByteArray(int8Array.length) { i -> int8Array[i] })
                    }
                } else {
                    res.text().then { msg -> if (msg.isNotBlank()) console.error(msg) }
                    responseBytesDeferred.complete(null)
                }
            },
            onRejected = {
                responseBytesDeferred.complete(null)
            })

        return responseBytesDeferred.await()
    }

    /**
     * Call DELETE on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun delete(apiPath: String, autoPrefix: Boolean = true): ByteArray? = fetch("DELETE", apiPath, autoPrefix)

    /**
     * Call GET on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun get(apiPath: String, autoPrefix: Boolean = true): ByteArray? = fetch("GET", apiPath, autoPrefix)

    /**
     * Call HEAD on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun head(apiPath: String, autoPrefix: Boolean = true): ByteArray? = fetch("HEAD", apiPath, autoPrefix)

    /**
     * Call OPTIONS on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun options(apiPath: String, autoPrefix: Boolean = true): ByteArray? = fetch("OPTIONS", apiPath, autoPrefix)

    /**
     * Call PATCH on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun patch(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray? = fetch("PATCH", apiPath, autoPrefix, body)

    /**
     * Call POST on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun post(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray? = fetch("POST", apiPath, autoPrefix, body)

    /**
     * Call PUT on a target API path.
     *
     * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You
     *   usually want this to be true, unless you are intentionally linking outside this site's root folder while still
     *   staying in the same domain.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun put(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray? = fetch("PUT", apiPath, autoPrefix, body)
}

private val apiFetcherInstance = ApiFetcher()

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.api get() = apiFetcherInstance