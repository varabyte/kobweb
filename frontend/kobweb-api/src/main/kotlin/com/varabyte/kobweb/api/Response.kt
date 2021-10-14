package com.varabyte.kobweb.api

class Response {
    /** See also: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status */
    var status = 200

    /** The text payload to send back */
    var payload: ByteArray = ByteArray(0)

    /**
     * The content type of the [payload], e.g. "image/jpeg" or "application/json". Can include parameters.
     *
     * See also: https://www.w3.org/Protocols/rfc1341/4_Content-Type.html
     */
    var contentType: String? = null
}