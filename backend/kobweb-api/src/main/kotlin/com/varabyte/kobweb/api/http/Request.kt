package com.varabyte.kobweb.api.http

import com.varabyte.kobweb.api.ApiContext

/**
 * Information passed into an API endpoint from the client.
 *
 * The request information will be passed in via an [ApiContext]. Developers implementing an API endpoint can read
 * request values with code like the following:
 *
 * ```
 * @Api
 * fun echo(ctx: ApiContext) {
 *   val msg = ctx.req.params["msg"]
 *   if (msg != null) {
 *     ctx.res.setBodyText("Received message: $msg")
 *   }
 *   else {
 *     ctx.res.status = 400
 *     ctx.res.setBodyText("Missing: required parameter 'msg'")
 *   }
 * }
 * ```
 *
 * @property method The type of http method this call was sent with.
 * @property params A list of key/value pairs extracted from the user's [query string](https://en.wikipedia.org/wiki/Query_string)
 * @property headers All headers sent with the request.
 * @property body An (optional) payload sent with the request. Will only potentially be set with appropriate methods that
 * are allowed to send data, i.e. [HttpMethod.POST], [HttpMethod.PUT], and [HttpMethod.PATCH]
 * @property contentType The content type of the [body], if set and sent.
 *
 * @see Response
 */
class Request(
    val method: HttpMethod,
    val params: Map<String, String>,
    val headers: Map<String, List<String>>,
    val body: ByteArray?,
    val contentType: String?,
)

fun Request.readBodyText(): String? {
    return body?.toString(Charsets.UTF_8)
}
