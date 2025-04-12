package com.varabyte.kobweb.streams

import androidx.compose.runtime.*
import com.varabyte.kobweb.streams.StreamMessage.Payload
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.WebSocket

interface ImmutableApiStream {
    val route: String
    val isConnected: Boolean
}

interface ApiStreamListener {
    class ConnectedContext(val stream: ApiStream)
    class TextReceivedContext(val stream: ApiStream, val text: String)
    class DisconnectedContext(val stream: ImmutableApiStream)

    fun onConnected(ctx: ConnectedContext) = Unit
    fun onTextReceived(ctx: TextReceivedContext)
    fun onDisconnected(ctx: DisconnectedContext) = Unit
}

/**
 * Represents a persistent connection with a Kobweb server that you can continuously send and receive events on.
 *
 * You must first define an API stream handler on the server side, and then you can connect to it from the client side
 * with code like the following:
 * ```
 * val stream = remember { ApiStream("echo") }
 * LaunchedEffect(Unit) {
 *     stream.connect(object : ApiStreamListener {
 *         override fun onConnected(ctx: ConnectedContext) { ... }
 *         override fun onTextReceived(ctx: TextReceivedContext) { ... }
 *         override fun onDisconnected(ctx: DisconnectedContext) { ... }
 *     }
 * }
 * ```
 * Note that there's a shortcut for the case where you only need to handle the "onTextReceived" event:
 * ```
 * LaunchedEffect(Unit) {
 *     stream.connect { text -> console.log("Got text from server: \"$text\"") }
 * }
 * ```
 *
 * While connected, you can call [send] to send messages to the server and [disconnect] to kill the stream
 * connection.
 *
 * If you call [send] BEFORE a stream finished connecting, what happens to the message (whether it gets enqueued or
 * dropped) is configurable. See its header docs for more details.
 */
class ApiStream(override val route: String) : ImmutableApiStream {
    internal class WebSocketChannel {
        internal interface Listener {
            fun onOpen()
            fun onClose()
            fun onMessage(message: StreamMessage<Payload.Server>)
        }

        var isOpen = false
            private set

        private val listeners = mutableListOf<Listener>()

        private val socket = run {
            val location = window.location
            val scheme = if (location.protocol == "https:") "wss" else "ws"
            val webSocketUrl = scheme + "://" + location.host + "/api/kobweb-streams"
            WebSocket(webSocketUrl)
        }

        fun addListener(listener: Listener) {
            listeners.add(listener)
            if (isOpen) {
                listener.onOpen()
            }
        }

        fun removeListener(listener: Listener) {
            listeners.remove(listener)
            if (isOpen) {
                // We're killing a specific stream before the overall websocket was killed. Let's
                // just act like the websocket closed in that case, so we run any shutdown logic.
                listener.onClose()
            }
        }

        fun send(message: StreamMessage<Payload.Client>) {
            if (isOpen) socket.send(Json.encodeToString(message))
        }

        init {
            socket.onopen = {
                isOpen = true
                listeners.forEach { it.onOpen() }
            }

            socket.onclose = {
                isOpen = false
                listeners.forEach { it.onClose() }
                listeners.clear()
            }

            socket.onmessage = { event ->
                val message = Json.decodeFromString<StreamMessage<Payload.Server>>(event.data.toString())
                listeners.forEach { it.onMessage(message) }
            }
        }

        fun close() {
            socket.close()
        }
    }

    companion object {
        private var _channel: WebSocketChannel? = null
        private var activeStreamCount = 0

        private var nextStreamId: Int = 0

        private fun connectChannel(): WebSocketChannel {
            if (activeStreamCount == 0) {
                _channel = WebSocketChannel()
            }
            ++activeStreamCount
            return _channel!!
        }

        private fun disconnectChannel() {
            check(activeStreamCount > 0) { "Called disconnectChannel more often than connectChannel" }
            --activeStreamCount
            if (activeStreamCount == 0) {
                _channel!!.close()
                _channel = null
            }
        }

        private fun nextId(): Short = nextStreamId.toShort()
            .also { nextStreamId = (nextStreamId + 1) % Short.MAX_VALUE }
    }

    private val id = nextId()
    private var channel: WebSocketChannel? = null
    private val isClosed = CompletableDeferred<Unit>()

    private var wasConnectCalled = false
    override val isConnected get() = channel?.isOpen ?: false

    suspend fun connect(streamListener: ApiStreamListener) {
        check(!wasConnectCalled) { "It is an error to try to connect the same ApiStream twice." }
        wasConnectCalled = true

        val channel = connectChannel()

        val listener = object : WebSocketChannel.Listener {
            override fun onOpen() {
                this@ApiStream.channel = channel
                channel.send(StreamMessage.clientConnect(id, route))
                streamListener.onConnected(ApiStreamListener.ConnectedContext(this@ApiStream))
                // Should be rare, but user can technically call `disconnect` in the `onConnected` handler
                if (isConnected) {
                    enqueuedMessages.forEach { message ->
                        channel.send(StreamMessage.text(id, message))
                    }
                }
                enqueuedMessages.clear()
            }

            override fun onClose() {
                isClosed.complete(Unit)
            }

            override fun onMessage(message: StreamMessage<Payload.Server>) {
                // Every Kobweb server only has one websocket that traffics multiple streams. If we've connected
                // multiple streams from this client, all ApiStream instances will receive all of them. Only respond to
                // the ones that target this stream specifically.
                if (message.localStreamId != id) return

                when (val payload = message.payload) {
                    is Payload.Text -> streamListener.onTextReceived(
                        ApiStreamListener.TextReceivedContext(
                            this@ApiStream,
                            payload.text
                        )
                    )

                    is Payload.Server.Error -> {
                        console.error(buildString {
                            append("API stream endpoint (\"$route\") threw an exception")
                            if (payload.callstack != null) {
                                append(":\n${payload.callstack}")
                            }
                        })
                    }
                }
            }
        }

        try {
            channel.addListener(listener)
            isClosed.await()
        } finally {
            // Might end up here without `isClosed` getting set explicitly if the user cancelled the coroutine, e.g. by
            // navigating away from the page.
            isClosed.complete(Unit)
            channel.send(StreamMessage.clientDisconnect(id))
            channel.removeListener(listener)
            disconnectChannel()
            streamListener.onDisconnected(ApiStreamListener.DisconnectedContext(this))
            this.channel = null
        }
    }

    private val enqueuedMessages = mutableListOf<String>()

    enum class IfSentBeforeConnectedStrategy {
        /**
         * If the stream is not connected at the time we want to send a message, queue it up to be sent when it is.
         *
         * If multiple messages were enqueued (and not cleared by [CLEAR_PREVIOUS]), they will be sent sequentially as
         * soon as this stream connects.
         */
        ENQUEUE,

        /**
         * If the stream is not connected at the time we want to send a message, clear any previously enqueued messages
         * and only send this one when the stream connects.
         *
         * This can be useful if you want to send a message that is only relevant to the most recent state of your site.
         */
        CLEAR_PREVIOUS,

        /**
         * If the stream is not connected at the time we want to send a message, do nothing.
         *
         * This can be useful for ignoring temporary messages like heartbeats or updates that only are worth sending at
         * the exact moment in time they happen.
         */
        SKIP,
    }

    fun send(text: String, strategy: IfSentBeforeConnectedStrategy = IfSentBeforeConnectedStrategy.ENQUEUE) {
        if (isClosed.isCompleted) return

        val channel = channel
        if (channel == null) {
            when (strategy) {
                IfSentBeforeConnectedStrategy.ENQUEUE -> enqueuedMessages.add(text)
                IfSentBeforeConnectedStrategy.CLEAR_PREVIOUS -> {
                    enqueuedMessages.clear(); enqueuedMessages.add(text)
                }

                IfSentBeforeConnectedStrategy.SKIP -> {}
            }
        } else {
            channel.send(StreamMessage.text(id, text))
        }
    }

    fun disconnect() {
        isClosed.complete(Unit)
    }
}

suspend fun ApiStream.connect(handleTextEvent: (ApiStreamListener.TextReceivedContext) -> Unit) {
    connect(object : ApiStreamListener {
        override fun onTextReceived(ctx: ApiStreamListener.TextReceivedContext) = handleTextEvent(ctx)
    })
}

@Composable
fun rememberApiStream(route: String, streamListener: ApiStreamListener): ApiStream {
    val stream = remember(route) { ApiStream(route) }
    LaunchedEffect(Unit) {
        stream.connect(streamListener)
    }
    return stream
}

@Composable
fun rememberApiStream(route: String, handleTextEvent: (ApiStreamListener.TextReceivedContext) -> Unit): ApiStream {
    return rememberApiStream(route, object : ApiStreamListener {
        override fun onTextReceived(ctx: ApiStreamListener.TextReceivedContext) = handleTextEvent(ctx)
    })
}
