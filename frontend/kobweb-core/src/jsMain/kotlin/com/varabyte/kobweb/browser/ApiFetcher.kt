package com.varabyte.kobweb.browser

import com.varabyte.kobweb.browser.http.AbortController
import com.varabyte.kobweb.browser.http.FetchDefaults
import com.varabyte.kobweb.browser.http.RequestBody
import com.varabyte.kobweb.browser.http.bodyAsBytes
import com.varabyte.kobweb.browser.http.bodyOf
import com.varabyte.kobweb.browser.http.http
import com.varabyte.kobweb.navigation.BasePath
import org.w3c.dom.Window
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response

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
        return BasePath.prependTo("/api/${apiPath.trimStart('/')}")
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
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.delete(toResource(apiPath), headers, redirect, abortController)

    /**
     * Call DELETE on a target API path, returning the response body as a raw array of bytes.
     *
     * See also [tryDeleteBytes], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "delete(apiPath, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun deleteBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = delete(apiPath, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryDelete(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryDelete(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [delete], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryDelete(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryDelete(apiPath, headers, redirect, abortController) { this }

    /**
     * Like [deleteBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryDelete(apiPath, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryDeleteBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryDelete(apiPath, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call GET on a target API path.
     *
     * See also [tryGet], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun get(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.get(toResource(apiPath), headers, redirect, abortController)

    /**
     * Call GET on a target API path, returning the response body as a raw array of bytes.
     *
     * See also [tryGetBytes], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "get(apiPath, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun getBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = get(apiPath, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryGet(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryGet(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [get], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryGet(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryGet(apiPath, headers, redirect, abortController) { this }

    /**
     * Like [getBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryGet(apiPath, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryGetBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryGet(apiPath, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call HEAD on a target API path.
     *
     * See also [tryHead], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun head(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.head(toResource(apiPath), headers, redirect, abortController)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryHead(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryHead(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [head], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryHead(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryHead(apiPath, headers, redirect, abortController) { this }

    /**
     * Call OPTIONS on a target API path.
     *
     * See also [tryOptions], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun options(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.options(toResource(apiPath), headers, redirect, abortController)

    /**
     * Call OPTIONS on a target API path, returning the response body as a raw array of bytes.
     *
     * See also [tryOptionsBytes], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "options(apiPath, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun optionsBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = options(apiPath, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryOptions(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryOptions(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [options], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryOptions(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryOptions(apiPath, headers, redirect, abortController) { this }

    /**
     * Like [optionsBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryOptions(apiPath, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
        )
    )
    suspend fun tryOptionsBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryOptions(apiPath, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call PATCH on a target API path.
     *
     * See also [tryPatch], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun patch(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.patch(toResource(apiPath), headers, body, redirect, abortController)

    /**
     * Call PATCH on a target API path, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPatchBytes], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "patch(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun patchBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = patch(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryPatch(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryPatch(toResource(apiPath), headers, body, redirect, abortController, transform)

    /**
     * Like [patch], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPatch(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryPatch(apiPath, headers, body, redirect, abortController) { this }

    /**
     * Like [patchBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPatch(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPatchBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryPatch(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController) { bodyAsBytes() }

    /**
     * Call POST on a target API path.
     *
     * See also [tryPost], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun post(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.post(toResource(apiPath), headers, body, redirect, abortController)

    /**
     * Call POST on a target API path, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPostBytes], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "post(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun postBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = post(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryPost(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryPost(toResource(apiPath), headers, body, redirect, abortController, transform)

    /**
     * Like [post], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPost(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryPost(apiPath, headers, body, redirect, abortController) { this }

    /**
     * Like [postBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPost(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPostBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryPost(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController) { bodyAsBytes() }

    /**
     * Call PUT on a target API path.
     *
     * See also [tryPut], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun put(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.put(toResource(apiPath), headers, body, redirect, abortController)

    /**
     * Call PUT on a target API path, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPutBytes], which will return null if the request fails for any reason.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "put(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun putBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = put(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController).bodyAsBytes()

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * @param transform A final step to convert the response into a different type. Any exception that is thrown while
     *   this method's logic is run will automatically be caught and, if [logOnError] is true, reported.
     */
    suspend fun <T> tryPut(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryPut(toResource(apiPath), headers, body, redirect, abortController, transform)

    /**
     * Like [put], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    suspend fun tryPut(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: RequestBody? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryPut(apiPath, headers, body, redirect, abortController) { this }

    /**
     * Like [putBytes], but returns null if the request failed for any reason.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPut(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPutBytes(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        body: ByteArray? = null,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryPut(apiPath, headers, body?.let { bodyOf(it) }, redirect, abortController) { bodyAsBytes() }
}

val Window.api: ApiFetcher get() = with(this.asDynamic()) {
    apiFetcher = apiFetcher ?: ApiFetcher(this@api)
    apiFetcher
}
