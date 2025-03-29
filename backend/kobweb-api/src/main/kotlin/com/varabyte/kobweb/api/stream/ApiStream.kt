package com.varabyte.kobweb.api.stream

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.env.Environment
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
 */
abstract class ApiStream {
    interface LimitedStreamContext {
        val stream: LimitedStream
        val clientId: StreamClientId
        val env: Environment
        val data: Data
        val logger: Logger
    }

    interface StreamContext : LimitedStreamContext {
        override val stream: Stream
    }

    class ClientConnectedContext(
        override val stream: Stream,
        override val clientId: StreamClientId,
        override val env: Environment,
        override val data: Data,
        override val logger: Logger
    ) : StreamContext

    class TextReceivedContext(
        override val stream: Stream,
        override val clientId: StreamClientId,
        val text: String,
        override val env: Environment,
        override val data: Data,
        override val logger: Logger
    ) : StreamContext

    class ClientDisconnectedContext(
        override val stream: LimitedStream,
        override val clientId: StreamClientId,
        override val env: Environment,
        override val data: Data,
        override val logger: Logger
    ) : LimitedStreamContext

    open suspend fun onClientConnected(ctx: ClientConnectedContext) = Unit
    abstract suspend fun onTextReceived(ctx: TextReceivedContext)
    open suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) = Unit
}

fun ApiStream(block: suspend (ApiStream.TextReceivedContext) -> Unit) =
    object : ApiStream() {
        override suspend fun onTextReceived(ctx: TextReceivedContext) = block(ctx)
    }
