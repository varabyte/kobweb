package com.varabyte.kobweb.api.http

/** A convenience value you can use if you want to express intention that your payload should be empty */
val EMPTY_PAYLOAD = ByteArray(0)

/**
 * Data to send back to the client after it makes a request to an API endpoint.
 *
 * An empty successful response is automatically created and passed into an API via an [ApiContext]. Developers
 * implementing an API endpoint should modify this response with code like the following:
 *
 * ```
 * @Api
 * fun demo(ctx: ApiContext) {
 *   ctx.res.setPayloadText("This is how you send text back to the client")
 * }
 * ```
 *
 * See also: [Request]
 */
class Response {
    /** See also: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status */
    var status = 200

    /** The payload to send back */
    var payload: ByteArray? = null

    /**
     * The content type of the [payload], e.g. "image/jpeg" or "application/json". Can include parameters.
     *
     * See also: https://www.w3.org/Protocols/rfc1341/4_Content-Type.html
     */
    var contentType: String? = null
}

/**
 * Convenience method for setting the payload to a text value.
 */
fun Response.setPayloadText(text: String) {
    payload = text.toByteArray(Charsets.UTF_8)
}
