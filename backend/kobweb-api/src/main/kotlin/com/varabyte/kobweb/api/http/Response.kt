package com.varabyte.kobweb.api.http

import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.MutableData
import com.varabyte.kobweb.api.intercept.ApiInterceptor
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.io.ByteSource
import com.varabyte.kobweb.io.RawByteSource
import com.varabyte.kobweb.io.toByteSource
import java.io.InputStream
import java.nio.charset.Charset

private val VALID_REDIRECT_STATUS_CODES = setOf(301, 302, 303, 307, 308)
private const val API_PREFIX = "/api"
private const val API_PREFIX_WITH_TRAILING_SLASH = "$API_PREFIX/"

/**
 * Data to send back to the client after it makes a request to an API endpoint.
 *
 * An empty successful response is automatically created and passed into an API via an [ApiContext]. Developers
 * implementing an API endpoint should modify this response with code like the following:
 *
 * ```
 * @Api
 * fun demo(ctx: ApiContext) {
 *   ctx.res.body = Body.text("This is how you send text back to the client")
 * }
 * ```
 *
 * @see Request
 */
class Response {
    private var _status: Int? = null

    /** @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">HTTP response status codes</a> */
    var status: Int
        get() = _status ?: 400
        set(value) {
            _status = value
        }

    /**
     * The body payload.
     *
     * Leave null if there should be no body to send back with this response.
     */
    var body: Body? = null
        set(value) {
            if (value != null && _status == null) {
                _status = 200
            }
            field = value
        }

    /**
     * Any additional headers to send back to the client.
     */
    val headers = MutableHeaders()

    /**
     * A holder of user data that can be added to this response.
     *
     * This will only be relevant for a project that uses an [ApiInterceptor]; in other words, this allows optional
     * communication from an API handler back to an API interceptor.
     */
    val data = MutableData()
}

/**
 * Convenience method for setting the body to a text value.
 */
@Deprecated("We are migrating to creating body instances using `Body` factory methods instead.", ReplaceWith("body = Body.text(text)", "com.varabyte.kobweb.api.http.text"))
fun Response.setBodyText(text: String) {
    body = Body.text(text)
}

/**
 * Set this to a response that tells the client that the requested resource has moved to a new location.
 *
 * @param status The specific redirect status code to use. See
 *   [MDN docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) for more information. Defaults to
 *   307 (temporary redirect).
 *
 * @param isApiPath If true, [newPath] will be prefixed with the "/api" prefix (unless already prefixed). This is useful
 *   if you're redirecting the user away from one API endpoint to another. You can of course just prepend "/api" to the
 *   path yourself, but in most cases, Kobweb tries to hide the "/api" prefix from the user, so it's a bit strange for
 *   us to force them to manually reference it here. Therefore, this parameter is provided as a convenience and as a way
 *   to document this situation.
 */
fun Response.setAsRedirect(newPath: String, status: Int = 307, isApiPath: Boolean = false) {
    check(status in VALID_REDIRECT_STATUS_CODES) { "Redirect status code is invalid ($status); must be one of $VALID_REDIRECT_STATUS_CODES" }
    this.status = status
    headers["Location"] =
        if (!isApiPath || newPath.startsWith(API_PREFIX_WITH_TRAILING_SLASH)) newPath else "$API_PREFIX$newPath"
}
