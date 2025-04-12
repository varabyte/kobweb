package com.varabyte.kobweb.api.stream

import kotlin.random.Random
import kotlin.random.nextInt

// Some functionality is still available even after a stream has been disconnected
interface LimitedStream {
    val id: StreamId

    /**
     * Send a text message to all clients connected on this stream.
     *
     * @param filter An optional filter which, if specified, limits the set of clients who will receive the message.
     *   The filter should return true to indicate that the client with the target ID should receive the message.
     */
    suspend fun broadcast(text: String, filter: (StreamId) -> Boolean = { true })
}

interface Stream : LimitedStream {
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
 * Convenience method to send a message to all clients with matching IDs.
 */
suspend fun LimitedStream.sendTo(text: String, streamIds: Iterable<StreamId>) {
    val idSet = streamIds.toSet()
    broadcast(text) { it in idSet }
}

suspend fun LimitedStream.sendTo(text: String, streamId: StreamId) {
    sendTo(text, listOf(streamId))
}

/**
 * Convenience method to send a message to all clients that don't match any of the passed in IDs.
 */
suspend fun LimitedStream.broadcastExcluding(text: String, streamIds: Iterable<StreamId>) {
    val idSet = streamIds.toSet()
    broadcast(text) { it !in idSet }
}

/**
 * Convenience method to send a message to all clients that don't match any of the passed in IDs.
 */
suspend fun LimitedStream.broadcastExcluding(text: String, streamId: StreamId) {
    broadcastExcluding(text, listOf(streamId))
}

/**
 * Represents a unique ID for a stream.
 *
 * This is commonly used when indicating which other streams should receive (or NOT receive) a broadcast message:
 * ```
 * // Exclude from self:
 * ctx.stream.broadcastExcluding("Hello, everyone else!", ctx.stream.id)
 * ```
 *
 * As this class is simple, immutable data, it is totally safe to store a copy of it after receiving some event, e.g.
 * if you receive a `ClientConnected` event, without worrying about major memory leaks. Just be sure to remove it when
 * you receive a `ClientDisconnected` event that contains that stream ID.
 *
 * In most cases, a user will only ever create a single client stream per API stream endpoint they want to connect to.
 * However, a user could technically create multiple streams that attach to the same endpoint, and each one will get its
 * own unique stream ID.
 *
 * NOTE: A stream's ID is a hashed combination of its [clientId] and its [localStreamId], which are both exposed as
 * well.
 *
 * @property clientId A value which uniquely identifies the client that connected this stream. In most cases, users will
 *   only create one stream per endpoint, but in the case a user does create multiple streams, the client ID will be the
 *   same across all of them.
 *
 * @property localStreamId A value which uniquely identifies a stream **for a client** (but not globally across all
 *    clients). In other words, multiple different `StreamId` instances may share the same `localStreamId` but they will
 *    represent different streams if associated with different `clientId` values.
 */
class StreamId(val clientId: Short, val localStreamId: Short) {
    companion object {
        // Define some random constants for mixing.
        private val C1 = 0xdeadbeef.toInt()
        private val C2 = 0xabcdef01.toInt()
        // Odd values used for multiplication, taken from murmurhash3
        // See also: https://github.com/aappleby/smhasher/blob/master/src/MurmurHash3.cpp
        private val M1 = 0xcc9e2d51.toInt()
        private val M2 = 0x1b873593.toInt()
        // Random additive constants
        private val A1 = Random.nextInt()
        private val A2 = 0xe6546b64.toInt() // taken from murmurhash3
        // Rotation amounts, taken from murmurhash3
        private val R1 = 15
        private val R2 = 13

        // Obfuscate the incoming value, not because we care that much from a security standpoint, but to emphasize to
        // users that the ID is not meant to be human-readable or predictable.
        // The approach we use here should be fairly fast, results in splitting neighboring input numbers up, and
        // most important no two input numbers should produce the same output number.
        private fun Int.obfuscate(): Int {
            var x = this

            x += A1
            x = x xor C1
            x *= M1
            x = x.rotateLeft(R1)

            x += A2
            x = x xor C2
            x *= M2
            x = x.rotateLeft(R2)

            return x
        }
    }

    /**
     * A value uniquely identifying this stream globally across all connected clients and all of their streams.
     */
    val value = ((clientId.toInt() shl 16).or(localStreamId.toInt())).obfuscate()
    override fun equals(other: Any?): Boolean {
        if (other !is StreamId) return false
        return this.value == other.value
    }
    override fun hashCode() = value.hashCode()
    override fun toString() = String.format("%08X", value) // Convert ID to hex format, looks more ID like
}

/**
 * A single event associated with the stream between the client and this server.
 *
 * A stream is a collection of many events, most which usually are text events sent from the client.
 *
 * Each event contains additional properties allowing the handler to respond to it.
 */
sealed interface StreamEvent {
    class ClientConnected(val stream: Stream) : StreamEvent
    class Text(val stream: Stream, val text: String) : StreamEvent
    class ClientDisconnected(val stream: LimitedStream) : StreamEvent
}
