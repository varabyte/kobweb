package com.varabyte.kobweb.browser

import androidx.compose.runtime.*
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
    private suspend fun fetch(method: String, apiPath: String, body: ByteArray? = null): ByteArray? {
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
        window.fetch("/api/$apiPath", requestInit).then {
            it.arrayBuffer().then { responseBuffer ->
                val int8Array = Int8Array(responseBuffer)
                responseBytesDeferred.complete(ByteArray(int8Array.length) { i -> int8Array[i] })
            }
        }.catch {
            responseBytesDeferred.complete(null)
        }

        return responseBytesDeferred.await()
    }

    /**
     * Call DELETE on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun delete(apiPath: String): ByteArray? = fetch("DELETE", apiPath)

    /**
     * Call GET on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun get(apiPath: String): ByteArray? = fetch("GET", apiPath)

    /**
     * Call HEAD on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun head(apiPath: String): ByteArray? = fetch("HEAD", apiPath)

    /**
     * Call OPTIONS on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun options(apiPath: String): ByteArray? = fetch("OPTIONS", apiPath)

    /**
     * Call PATCH on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun patch(apiPath: String, body: ByteArray? = null): ByteArray? = fetch("PATCH", apiPath, body)

    /**
     * Call POST on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun post(apiPath: String, body: ByteArray? = null): ByteArray? = fetch("POST", apiPath, body)

    /**
     * Call PUT on a target API path.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun put(apiPath: String, body: ByteArray? = null): ByteArray? = fetch("PUT", apiPath, body)
}

private val apiFetcherInstance = ApiFetcher()

@Suppress("unused") // We tie our class to the "Window" class on purpose, so it can be used instead of `fetch`
val Window.api get() = apiFetcherInstance