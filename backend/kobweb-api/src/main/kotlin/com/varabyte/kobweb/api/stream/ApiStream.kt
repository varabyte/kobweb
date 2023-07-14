package com.varabyte.kobweb.api.stream

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.log.Logger

/**
 * A class which can be used to handle events coming in over a streaming connection.
 *
 * If you declare a public, global ApiStream property, Kobweb will automatically detect it and register it at compile
 * time.
 *
 * ```
 * // src/api/Example.kt
 * // By default, the name of this stream will be "example", taken from the file path + name.
 * val exampleStream = object : ApiStream {
 *    ...
 * }
 * ```
 *
 * For technical readers, note that streams are NOT websockets. Instead, if configured, a Kobweb server will open a
 * single web socket, which can then handle one (or more!) Kobweb streams. The API for streams mimics web sockets so
 * the experience will feel similar, but they're not 1:1 the same.
 *
 * We use this approach instead of websockets directly for two reasons:
 * 1. this potentially reduces the number of connections a server needs to manage even if a site wants to register
 *    multiple separate streams.
 * 2. Kobweb supports a live reloading experience, and we cannot easily dynamically create and destroy websocket
 *    handlers in ktor. However, we can create a single streaming endpoint and multiplex the incoming messages to the
 *    appropriate stream handlers.
 *
 * ## routeOverride
 *
 * Note that the route generated for this page is quite customizable by setting the [routeOverride] parameter. In
 * general, you should NOT set it, as this will make it harder for people to navigate your project and find where a
 * api stream is being defined.
 *
 * However, if you do set it, in most cases, it is expected to just be a single, lowercase word, which changes the slug
 * used for this route (instead of the file name).
 *
 * But wait, there's more!
 *
 * If the value starts with a slash, it will be treated as a full path. If the value ends with a slash, it means the
 * override represents a change in the path but the slug will still be derived from the filename.
 *
 * Some examples should clear up the various cases. Let's say this `ApiStream` is defined in `package api.user` in file
 * `Fetch.kt`:
 *
 * ```
 * ApiStream -> "user/fetch
 * ApiStream("retrieve") -> /user/retrieve
 * ApiStream("current/") -> /user/current/fetch
 * ApiStream("current/retrieve") -> /user/current/retrieve
 * ApiStream("/users/") -> /users/fetch
 * ApiStream("/users/retrieve") -> /users/retrieve
 * ApiStream("/") -> /fetch
 * ```
 *
 * @param routeOverride If specified, override the logic for generating a name for this API stream as documented in this
 *   header doc.
 */
abstract class ApiStream(val routeOverride: String = "") {
    class ClientConnectedContext(val stream: Stream, val clientId: StreamClientId, val data: Data, val logger: Logger)
    class TextReceivedContext(
        val stream: Stream,
        val clientId: StreamClientId,
        val text: String,
        val data: Data,
        val logger: Logger
    )

    class ClientDisconnectedContext(
        val stream: Stream,
        val clientId: StreamClientId,
        val data: Data,
        val logger: Logger
    )

    open suspend fun onClientConnected(ctx: ClientConnectedContext) = Unit
    abstract suspend fun onTextReceived(ctx: TextReceivedContext)
    open suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) = Unit
}

fun ApiStream(routeOverride: String = "", block: suspend (ApiStream.TextReceivedContext) -> Unit) =
    object : ApiStream(routeOverride) {
        override suspend fun onTextReceived(ctx: TextReceivedContext) = block(ctx)
    }
