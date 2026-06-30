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
     * If true, when using any of the "try" methods, logs any errors, if they occur, to the console.
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
     * See also [tryDelete], which will return null if the request fails.
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
     * See also [tryDeleteBytes], which will return null if the request fails.
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
     * Like [delete], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryDelete(...) { convert() }` over `tryDelete(...)?.convert()` as the
     * former will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryDelete] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryDelete(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryDelete(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [delete], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun tryDelete(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryDelete(apiPath, headers, redirect, abortController) { this }

    /**
     * Like [deleteBytes], but returns null if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
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
     * See also [tryGet], which will return null if the request fails.
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
     * See also [tryGetBytes], which will return null if the request fails.
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
     * Like [get], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryGet(...) { convert() }` over `tryGet(...)?.convert()` as the former will
     * ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryGet] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryGet(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryGet(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [get], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun tryGet(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryGet(apiPath, headers, redirect, abortController) { this }

    /**
     * Like [getBytes], but returns null if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
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
     * See also [tryHead], which will return null if the request fails.
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
     * Like [head], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryHead(...) { convert() }` over `tryHead(...)?.convert()` as the former
     * will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryHead] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryHead(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryHead(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [head], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
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
     * See also [tryOptions], which will return null if the request fails.
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
     * See also [tryOptionsBytes], which will return null if the request fails.
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
     * Like [options], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryOptions(...) { convert() }` over `tryOptions(...)?.convert()` as the
     * former will ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryOptions] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryOptions(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryOptions(toResource(apiPath), headers, redirect, abortController, transform)

    /**
     * Like [options], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun tryOptions(
        apiPath: String,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryOptions(apiPath, headers, redirect, abortController) { this }

    /**
     * Like [optionsBytes], but returns null if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
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
     * See also [tryPatch], which will return null if the request fails.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun patch(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.patch(toResource(apiPath), body, headers, redirect, abortController)

    /**
     * Call PATCH on a target API path, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPatchBytes], which will return null if the request fails.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "patch(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun patchBytes(
        apiPath: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = patch(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [patch], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryPatch(...) { convert() }` over `tryPatch(...)?.convert()` as the former
     * ill ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryPatch] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryPatch(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryPatch(toResource(apiPath), body, headers, redirect, abortController, transform)

    /**
     * Like [patch], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun tryPatch(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryPatch(apiPath, body, headers, redirect, abortController) { this }

    /**
     * Like [patchBytes], but returns null if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPatch(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPatchBytes(
        apiPath: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryPatch(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call POST on a target API path.
     *
     * See also [tryPost], which will return null if the request fails.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun post(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.post(toResource(apiPath), body, headers, redirect, abortController)

    /**
     * Call POST on a target API path, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPostBytes], which will return null if the request fails.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "post(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun postBytes(
        apiPath: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = post(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [post], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryPost(...) { convert() }` over `tryPost(...)?.convert()` as the former
     * ill ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryPost] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryPost(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryPost(toResource(apiPath), body, headers, redirect, abortController, transform)

    /**
     * Like [post], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun tryPost(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryPost(apiPath, body, headers, redirect, abortController) { this }

    /**
     * Like [postBytes], but returns null if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPost(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPostBytes(
        apiPath: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryPost(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }

    /**
     * Call PUT on a target API path.
     *
     * See also [tryPut], which will return null if the request fails.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun put(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response = window.http.put(toResource(apiPath), body, headers, redirect, abortController)

    /**
     * Call PUT on a target API path, returning the response body as a raw array of bytes.
     *
     * If a request body is provided, it is also specified as a raw array of bytes.
     *
     * See also [tryPutBytes], which will return null if the request fails.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "put(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun putBytes(
        apiPath: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray = put(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController).bodyAsBytes()

    /**
     * Like [put], but returns null instead of throwing if the request fails.
     *
     * This method also provides a [transform] step which you can use to convert the response into a different type.
     * You are generally encouraged to call `tryPut(...) { convert() }` over `tryPut(...)?.convert()` as the former will
     * ensure that exception handling is covered in that case.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console (including the logic in
     * the [transform] block).
     *
     * If you do not care about converting the result, use the [tryPut] version that returns [Response?][Response]
     * instead.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun <T> tryPut(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
        transform: suspend Response.() -> T
    ): T? = window.http.tryPut(toResource(apiPath), body, headers, redirect, abortController, transform)

    /**
     * Like [put], but returns null if the request fails.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    suspend fun tryPut(
        apiPath: String,
        body: RequestBody? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): Response? = tryPut(apiPath, body, headers, redirect, abortController) { this }

    /**
     * Like [putBytes], but returns null if the request fails or its body can't be read.
     *
     * Additionally, if [logOnError] is set to true, any failure will be logged to the console. By default, this will
     * be true for debug builds and false for release builds.
     *
     * Note: you should NOT prepend your path with "api/", as that will be added automatically.
     */
    @Deprecated("We are phasing out the *Bytes version of network requests, now that we have new versions that return `Response` objects directly.",
        ReplaceWith(
            "tryPut(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }",
            "com.varabyte.kobweb.browser.http.FetchDefaults",
            "com.varabyte.kobweb.browser.http.bodyAsBytes",
            "com.varabyte.kobweb.browser.http.bodyOf",
        )
    )
    suspend fun tryPutBytes(
        apiPath: String,
        body: ByteArray? = null,
        headers: Map<String, Any>? = FetchDefaults.Headers,
        redirect: RequestRedirect? = FetchDefaults.Redirect,
        abortController: AbortController? = null,
    ): ByteArray? = tryPut(apiPath, body?.let { bodyOf(it) }, headers, redirect, abortController) { bodyAsBytes() }
}

val Window.api: ApiFetcher get() = with(this.asDynamic()) {
    apiFetcher = apiFetcher ?: ApiFetcher(this@api)
    apiFetcher
}
