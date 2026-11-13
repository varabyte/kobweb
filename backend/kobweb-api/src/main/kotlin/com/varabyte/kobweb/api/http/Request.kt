package com.varabyte.kobweb.api.http

import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.data.MutableData
import com.varabyte.kobweb.api.intercept.ApiInterceptor

/**
 * Information passed into an API endpoint from the client.
 *
 * The request information will be passed in via an [ApiContext]. Developers implementing an API endpoint can read
 * request values with code like the following:
 *
 * ```
 * @Api
 * suspend fun echo(ctx: ApiContext) {
 *   val msg = ctx.req.params["msg"]
 *   if (msg != null) {
 *     ctx.res.body = bodyOf("Received message: $msg")
 *   }
 *   else {
 *     ctx.res.status = 400
 *     ctx.res.body = bodyOf("Missing: required parameter 'msg'")
 *   }
 * }
 * ```
 *
 * @see Response
 */
sealed interface Request {
    /** Information about the connection that carried the request. */
    val connection: Connection
    /** The type of http method this call was sent with. */
    val method: HttpMethod
    /**
     * Key/value pairs extracted from the request's [query string](https://en.wikipedia.org/wiki/Query_string).
     *
     * For example, a request to `/api/users?sort=asc` will have a mapping of "sort" to "asc".
     *
     * Use this over [params] when you want to make sure you get the query param value, even if an identically named
     * dynamic path part exists.
     *
     * @see dynamicParams
     * @see params
     */
    val queryParams: Map<String, String>
    /**
     * Key/value pairs captured from the dynamic parts of the endpoint's route.
     *
     * For example, a request to `/api/users/123` for an API endpoint registered as `/api/users/{user}` will have a
     * mapping of "user" to "123".
     *
     * NOTE: Unlike query params, which can contain any key, the keys of this map are fixed by the endpoint's route.
     * This means the Kobweb IntelliJ plugin can check your lookups, for example flagging a key that isn't part of the
     * route (likely a typo) or confirming one that is (meaning it knows it won't ever be null).
     *
     * Note that dynamic values are captured when the request is dispatched to its handler, so they will be missing
     * inside an [ApiInterceptor] until then.
     *
     * @see queryParams
     * @see params
     */
    val dynamicParams: Map<String, String>
    /**
     * Key/value pairs extracted either from the request's query string or from any dynamic path parts.
     *
     * For example, a request to `/api/users/123?sort=asc` for an API endpoint registered as `/api/users/{user}` will
     * have a mapping of "user" to "123" and "sort" to "asc".
     *
     * If the same key is specified in both the query string and the dynamic path, the dynamic path will take
     * precedence.
     *
     * This property is fine to use over [dynamicParams] or [queryParams], especially if you don't care where the input
     * comes from. Prefer [dynamicParams] or [queryParams] when you want to make explicit where a value comes from.
     * [dynamicParams] has the added advantage that the Kobweb IntelliJ plugin can check its keys against the
     * endpoint's route.
     *
     * Note that dynamic values are captured when the request is dispatched to its handler, so they will be missing
     * inside an [ApiInterceptor] until then.
     *
     * @see queryParams
     * @see dynamicParams
     */
    val params: Map<String, String>
    /** All headers sent with the request. */
    val headers: Headers
    /**
     * Any cookies sent with the request.
     *
     * Note the value of the cookies will be in a raw format, so you may need to decode them yourself.
     */
    val cookies: Map<String, String>
    /**
     * A holder of user data that can be added to this request.
     *
     * This will only be relevant for a project that uses an [ApiInterceptor]; in other words, this allows optional
     * communication from an API interceptor to an API handler (such as storing some calculated auth value).
     */
    val data: Data
    /**
     * The body payload sent with the request.
     *
     * Will only potentially be set with appropriate methods that are allowed to send data, i.e. [HttpMethod.POST],
     * [HttpMethod.PUT], [HttpMethod.PATCH], and [HttpMethod.QUERY]. This value will be null if no body is set /
     * applicable.
     */
    val body: Body?

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

/**
 * A [Request] whose values can be modified, e.g. by an [ApiInterceptor] before the request reaches its handler.
 *
 * Note that [params], [queryParams], and [dynamicParams] are separate maps, so changing one does not update the
 * others. [queryParams] and [dynamicParams] are meant to reflect the request's route, so if you add extra params, add
 * them to [params] only. (To pass along values that aren't really params, such as a computed auth result, consider
 * [data] instead.)
 */
class MutableRequest(
    override val connection: Request.Connection,
    override var method: HttpMethod,
    params: Map<String, String>,
    queryParams: Map<String, String>,
    dynamicParams: Map<String, String>,
    headers: Headers,
    cookies: Map<String, String>,
    override var body: Body?,
    override val data: MutableData = MutableData(),
) : Request {
    constructor(request: Request) : this(
        request.connection,
        request.method,
        request.params,
        request.queryParams,
        request.dynamicParams,
        request.headers,
        request.cookies,
        request.body,
        request.data.toMutableData(),
    )

    override val params: MutableMap<String, String> = params.toMutableMap()
    override val queryParams: MutableMap<String, String> = queryParams.toMutableMap()
    override val dynamicParams: MutableMap<String, String> = dynamicParams.toMutableMap()
    override val headers: MutableHeaders = MutableHeaders(headers)

    override val cookies: MutableMap<String, String> = cookies.toMutableMap()
}
