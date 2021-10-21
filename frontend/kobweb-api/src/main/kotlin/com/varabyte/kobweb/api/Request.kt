package com.varabyte.kobweb.api

/**
 * Information passed into an API endpoint from the client.
 *
 * The request information will be passed in via an [ApiContext]. Developers implementing an API endpoint can read
 * request values with code like the following:
 *
 * ```
 * @Api
 * fun echo(ctx: ApiContext) {
 *   val msg = ctx.req.query["msg"]
 *   if (msg != null) {
 *     ctx.res.setPayloadText("Received message: $msg")
 *   }
 *   else {
 *     ctx.res.status = 400
 *     ctx.res.setPayloadText("Missing: required parameter 'msg'")
 *   }
 * }
 * ```
 *
 * See also: [Response]
 *
 * @param method The type of http method this call was sent with.
 * @param query A list of key/value pairs extracted from the user's [query string](https://en.wikipedia.org/wiki/Query_string)
 * @param body An (optional) payload sent with the request. Will only potentially be set with appropriate methods that
 * are allowed to send data, i.e. [HttpMethod.POST], [HttpMethod.PUT], and [HttpMethod.PATCH]
 * @param contentType The content type of the [body], if set and sent.
 */
class Request(
    val method: HttpMethod,
    val query: Map<String, String>,
    val body: ByteArray?,
    val contentType: String?,
)

fun Request.readBodyText(): String? {
    return body?.toString(Charsets.UTF_8)
}