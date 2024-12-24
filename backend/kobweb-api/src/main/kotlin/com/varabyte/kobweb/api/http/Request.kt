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
 * @property connection Information about the connection that carried the request.
 * @property method The type of http method this call was sent with.
 * @property params A list of key/value pairs extracted either from the user's [query string](https://en.wikipedia.org/wiki/Query_string)
 *   or from any dynamic path parts.
 * @property queryParams Like [params] but only for the query string, just in case a user needs to disambiguate between
 *   a dynamic path part and a query parameter with the same name.
 * @property headers All headers sent with the request.
 * @property cookies Any cookies sent with the request. Note the value of the cookies will be in a raw format, so you
 *   may need to decode them yourself.
 * @property body An (optional) payload sent with the request. Will only potentially be set with appropriate methods that
 * are allowed to send data, i.e. [HttpMethod.POST], [HttpMethod.PUT], and [HttpMethod.PATCH]
 * @property contentType The content type of the [body], if set and sent.
 *
 * @see Response
 */
interface Request {
    val connection: Connection
    val method: HttpMethod
    val params: Map<String, String>
    val queryParams: Map<String, String>
    val headers: Map<String, List<String>>
    val cookies: Map<String, String>
    val body: ByteArray?
    val contentType: String?

    /**
     * Top-level container class for views about a connection for some request.
     *
     * You may wish to
     * review [Ktor docs about Forwarding Headers](https://ktor.io/docs/forward-headers.html#request_info) if you want
     * to learn more about how the [origin] and [local] views can be different.
     *
     * @property origin Details about the request's connection point of origin (i.e. the client).
     * @property local Details about the request's connection at the point it was received by the server. This can be
     *   different from [origin] if the server is behind a proxy (that is, the request was intercepted and rerouted), at
     *   which point the connection details will be about the proxy, not the client.
     */
    class Connection(
        val origin: Details,
        val local: Details,
    ) {
        /**
         * Details about a connection that carries a request.
         *
         * @property scheme The scheme of the connection, e.g. "http" or "https"
         * @property version The version of the connection, e.g. "HTTP/1.1"
         * @property localAddress The IP address of the client making the request.
         * @property localHost The host name of the client making the request.
         * @property localPort The port of the client making the request.
         * @property remoteAddress The IP address of the server receiving the request.
         * @property remoteHost The host name of the server receiving the request.
         * @property remotePort The port of the server receiving the request.
         * @property serverHost The host name of the server receiving the request. This can be different from the
         *   remote host in cases where proxies or load balancers are used.
         * @property serverPort The port of the server receiving the request. This can be different from the remote port
         *   in cases where proxies or load balancers are used.
         */
        data class Details(
            val scheme: String,
            val version: String,
            val localAddress: String,
            val localHost: String,
            val localPort: Int,
            val remoteAddress: String,
            val remoteHost: String,
            val remotePort: Int,
            val serverHost: String,
            val serverPort: Int,
        )
    }
}

class MutableRequest(
    override val connection: Request.Connection,
    override var method: HttpMethod,
    params: Map<String, String>,
    queryParams: Map<String, String>,
    headers: Map<String, List<String>>,
    cookies: Map<String, String>,
    override var body: ByteArray?,
    override var contentType: String?,
) : Request {
    constructor(request: Request) : this(
        request.connection,
        request.method,
        request.params,
        request.queryParams,
        request.headers,
        request.cookies,
        request.body,
        request.contentType,
    )

    override val params: MutableMap<String, String> = params.toMutableMap()
    override val queryParams: MutableMap<String, String> = queryParams.toMutableMap()
    override val headers: MutableMap<String, MutableList<String>> = headers
        .mapValues { entry -> entry.value.toMutableList() }
        .toMutableMap()

    override val cookies: MutableMap<String, String> = cookies.toMutableMap()
}

/**
 * Convenience method to pull body text out from a request.
 *
 * Otherwise, you'd have to deal with the raw [ByteArray] and convert it to a [String] yourself.
 */
fun Request.readBodyText(): String? {
    return body?.toString(Charsets.UTF_8)
}
