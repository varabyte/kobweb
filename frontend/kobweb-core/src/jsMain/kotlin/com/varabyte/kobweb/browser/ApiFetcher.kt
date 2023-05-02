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
    private suspend fun fetch(method: String, apiPath: String, autoPrefix: Boolean, body: ByteArray? = null): ByteArray {
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

        window.fetch(RoutePrefix.prependIf(autoPrefix, "/api/$apiPath"), requestInit).then(
            onFulfilled = { res ->
                if (res.ok) {
                    res.arrayBuffer().then { responseBuffer ->
                        val int8Array = Int8Array(responseBuffer)
                        responseBytesDeferred.complete(ByteArray(int8Array.length) { i -> int8Array[i] })
                    }
                } else {
                    var msg: String? = null
                    res.text().then { msg = it.takeUnless { it.isNotBlank() } }
                    responseBytesDeferred.completeExceptionally(Exception(msg ?: "Unknown error"))
                }
            },
            onRejected = {
                responseBytesDeferred.completeExceptionally(it)
            })

        return responseBytesDeferred.await()
    }

    private suspend fun tryFetch(method: String, apiPath: String, autoPrefix: Boolean, body: ByteArray? = null): ByteArray? {
        return try {
            fetch(method, apiPath, autoPrefix, body)
        } catch (t: Throwable) {
            console.log("Error fetching API endpoint \"$apiPath\"\n\n$t")
            null
        }
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
    suspend fun delete(apiPath: String, autoPrefix: Boolean = true): ByteArray = fetch("DELETE", apiPath, autoPrefix)

    /**
     * Like [delete], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryDelete(apiPath: String, autoPrefix: Boolean = true): ByteArray? = tryFetch("DELETE", apiPath, autoPrefix)

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
    suspend fun get(apiPath: String, autoPrefix: Boolean = true): ByteArray = fetch("GET", apiPath, autoPrefix)

    /**
     * Like [get], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryGet(apiPath: String, autoPrefix: Boolean = true): ByteArray? = tryFetch("GET", apiPath, autoPrefix)

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
    suspend fun head(apiPath: String, autoPrefix: Boolean = true): ByteArray = fetch("HEAD", apiPath, autoPrefix)

    /**
     * Like [head], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryHead(apiPath: String, autoPrefix: Boolean = true): ByteArray? = tryFetch("HEAD", apiPath, autoPrefix)

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
    suspend fun options(apiPath: String, autoPrefix: Boolean = true): ByteArray = fetch("OPTIONS", apiPath, autoPrefix)

    /**
     * Like [options], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryOptions(apiPath: String, autoPrefix: Boolean = true): ByteArray? = tryFetch("OPTIONS", apiPath, autoPrefix)

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
    suspend fun patch(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray = fetch("PATCH", apiPath, autoPrefix, body)

    /**
     * Like [patch], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryPatch(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray? = tryFetch("PATCH", apiPath, autoPrefix, body)

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
    suspend fun post(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray = fetch("POST", apiPath, autoPrefix, body)

    /**
     * Like [post], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryPost(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray? = tryFetch("POST", apiPath, autoPrefix, body)

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
    suspend fun put(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray = fetch("PUT", apiPath, autoPrefix, body)

    /**
     * Like [put], but returns null (and logs the error to the console) if the request failed for any reason.
     */
    suspend fun tryPut(apiPath: String, autoPrefix: Boolean = true, body: ByteArray? = null): ByteArray? = tryFetch("PUT", apiPath, autoPrefix, body)
}

private val apiFetcherInstance = ApiFetcher()

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.api get() = apiFetcherInstance