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
 *     ctx.res.payload = "Received message: $msg".toByteArray()
 *   }
 *   else {
 *     ctx.res.status = 400
 *     ctx.res.payload = "Missing: required parameter 'msg'"
 *   }
 * }
 * ```
 *
 * See also: [Response]
 *
 * @param query A list of key/value pairs extracted from the user's [query string](https://en.wikipedia.org/wiki/Query_string)
 */
class Request(
    val query: Map<String, String>
)