package com.varabyte.kobweb.api.stream

import java.util.concurrent.atomic.AtomicInteger

// Some functionality is still available even after a stream has been disconnected
interface DisconnectedStream {
    /**
     * Send a text message to all clients connected on this stream.
     *
     * @param filter An optional filter which, if specified, limits the set of clients who will receive the message.
     *   The filter should return true to indicate that the client with the target ID should receive the message.
     */
    suspend fun broadcast(text: String, filter: (StreamClientId) -> Boolean = { true })
}

interface Stream : DisconnectedStream {
    /**
     * Reply with a text message back to the client.
     *
     * Use [broadcast] if you want to send a message to all clients connected on this stream.
     */
    suspend fun send(text: String)

    /**
     * Remove the user from this stream.
     *
     * If this was the last connected user, the stream will be closed.
     */
    suspend fun disconnect()
}

/**
 * Convenience method to broadcast a message to all clients by their IDs.
 */
suspend fun DisconnectedStream.broadcast(text: String, clientIds: Iterable<StreamClientId>) {
    val idSet = clientIds.toSet()
    broadcast(text) { it in idSet }
}

/**
 * Represents the ID of the user who is connected to this stream.
 *
 * For clarity, note that you might have multiple active streams flowing at the same time. This class does NOT represent
 * them! Instead, it represents the user sending data to the stream. A single user might be concurrently sending data
 * across multiple streams (and will have the same ID in each case), and also multiple different users might be sending
 * data to the same stream.
 */
@JvmInline
value class StreamClientId private constructor(val id: Short) {
    companion object {
        private val nextId = AtomicInteger(0)

        // A machine only can allow up to 65K concurrent connections, so even in that extreme case, using a short and
        // wrapping around is fine to guarantee uniqueness.
        fun next() = StreamClientId((nextId.getAndIncrement() % Short.MAX_VALUE).toShort())
    }
}


/**
 * A single event associated with the stream between the client and this server.
 *
 * A stream is a collection of many events, most which usually are text events sent from the client.
 *
 * Each event contains additional properties allowing the handler to respond to it.
 *
 * @property clientId The ID of the streamer (that is, user) associated with this stream. This is NOT an ID of the
 *   stream itself, in other words.
 */
sealed class StreamEvent(val clientId: StreamClientId) {
    class ClientConnected(val stream: Stream, clientId: StreamClientId) : StreamEvent(clientId)
    class Text(val stream: Stream, clientId: StreamClientId, val text: String) : StreamEvent(clientId)
    class ClientDisconnected(val stream: DisconnectedStream, clientId: StreamClientId) : StreamEvent(clientId)
}
