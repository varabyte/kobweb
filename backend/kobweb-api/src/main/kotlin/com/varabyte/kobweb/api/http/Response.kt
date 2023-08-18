package com.varabyte.kobweb.api.http

/** A convenience value you can use if you want to express intention that your body should be empty */
val EMPTY_BODY = ByteArray(0)

/**
 * Data to send back to the client after it makes a request to an API endpoint.
 *
 * An empty successful response is automatically created and passed into an API via an [ApiContext]. Developers
 * implementing an API endpoint should modify this response with code like the following:
 *
 * ```
 * @Api
 * fun demo(ctx: ApiContext) {
 *   ctx.res.setBodyText("This is how you send text back to the client")
 * }
 * ```
 *
 * @see [Request]
 */
class Response {
    private var _status: Int? = null
    private var _body: ByteArray? = null

    /** See also: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status */
    var status: Int
        get() = _status ?: 400
        set(value) {
            _status = value
        }

    /** The body payload to send back */
    var body: ByteArray
        get() = _body ?: EMPTY_BODY
        set(value) {
            if (_status == null) {
                _status = 200
            }
            _body = value
        }

    /**
     * The content type of the [body], e.g. "image/jpeg" or "application/json". Can include parameters.
     *
     * See also: https://www.w3.org/Protocols/rfc1341/4_Content-Type.html
     */
    var contentType: String? = null
}

/**
 * Convenience method for setting the body to a text value.
 */
fun Response.setBodyText(text: String) {
    body = text.toByteArray(Charsets.UTF_8)
}
