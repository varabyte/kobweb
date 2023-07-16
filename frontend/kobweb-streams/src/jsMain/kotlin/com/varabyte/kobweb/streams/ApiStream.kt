package com.varabyte.kobweb.streams

import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.WebSocket

interface ApiStreamListener {
    fun onConnected() = Unit
    fun onTextReceived(text: String)
    fun onDisconnected() = Unit
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
 *         override fun onTextReceived(ctx: MessageReceivedContext) { ... }
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
class ApiStream(val route: String) {
    internal class WebSocketChannel {
        internal interface Listener {
            fun onOpen()
            fun onClose()
            fun onMessage(message: StreamMessage)
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

        fun send(message: StreamMessage) {
            socket.send(Json.encodeToString(message))
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
                val message = Json.decodeFromString<StreamMessage>(event.data.toString())
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
    }

    private var channel: WebSocketChannel? = null
    private val isClosed = CompletableDeferred<Unit>()

    val isConnected get() = channel?.isOpen ?: false

    suspend fun connect(streamListener: ApiStreamListener) {
        val channel = connectChannel()

        val listener = object : WebSocketChannel.Listener {
            override fun onOpen() {
                this@ApiStream.channel = channel
                channel.send(StreamMessage.clientConnect(route))
                streamListener.onConnected()
                // Should be rare, but user can technically call `disconnect` in the `onConnected` handler
                if (isConnected) {
                    enqueuedMessages.forEach { message ->
                        channel.send(StreamMessage.text(route, message))
                    }
                }
                enqueuedMessages.clear()
            }

            override fun onClose() {
                isClosed.complete(Unit)
            }

            override fun onMessage(message: StreamMessage) {
                // We have one websocket that can traffic multiple streams. If we're connected for multiple streams,
                // we'll get messages for all of them. Only respond to the client stream we are associated with.
                if (message.route != route) return

                val payload = message.payload as? StreamMessage.Payload.Text ?: return
                streamListener.onTextReceived(payload.text)
            }
        }

        try {
            channel.addListener(listener)
            isClosed.await()
        } finally {
            // Might end up here without `isClosed` getting set explicitly if the user cancelled the coroutine, e.g. by
            // navigating away from the page.
            isClosed.complete(Unit)
            channel.send(StreamMessage.clientDisconnect(route))
            channel.removeListener(listener)
            disconnectChannel()
            streamListener.onDisconnected()
            this.channel = null
        }
    }

    suspend fun connect(handleTextEvent: (String) -> Unit) {
        connect(object : ApiStreamListener {
            override fun onTextReceived(text: String) = handleTextEvent(text)
        })
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
            channel.send(StreamMessage.text(route, text))
        }
    }

    fun disconnect() {
        isClosed.complete(Unit)
    }
}
