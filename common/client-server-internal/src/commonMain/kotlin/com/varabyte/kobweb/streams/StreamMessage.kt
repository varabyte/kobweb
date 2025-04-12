package com.varabyte.kobweb.streams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A message sent between the client and the server (could be in either direction), including some payload.
 *
 * @property localStreamId An ID that uniquely identifies the stream **to a client**. NOTE: This value is NOT the same
 *   as the ID used for the stream as the server manages it, since it has to distinguish between multiple clients that
 *   may use the same `localStreamId` value.
 */
@Serializable
class StreamMessage<out P : StreamMessage.Payload>(val localStreamId: Short, val payload: P) {
    @Serializable
    sealed interface Payload {
        /**
         * Payloads that are sent from the client to the server.
         */
        @Serializable
        sealed interface Client : Payload {
            /**
             * @property route The name of the API endpoint being connected to. A stream has to advertise this because
             *   the server uses a single websocket endpoint to handle all streams, so it relies on the client for
             *   informing it which endpoint it is trying to connect to. After getting connected, the server will
             *   associate this information with the stream moving forward.
             */
            @Serializable
            @SerialName("StreamPayloadClientConnect")
            class Connect(val route: String) : Client

            @Serializable
            @SerialName("StreamPayloadClientDisconnect")
            object Disconnect : Client
        }

        /**
         * Payloads that are sent from the server to the client.
         */
        @Serializable
        sealed interface Server : Payload {
            /**
             * @param callstack The callstack of the error from the server. This will only get sent by servers in dev mode.
             *   If in release, you'll need to check the logs instead to see what happened.
             */
            @Serializable
            @SerialName("StreamPayloadServerError")
            class Error(val callstack: String?) : Server
        }

        /** Payloads that can be sent in either direction. */
        @Serializable
        sealed interface Bidirectional : Client, Server

        @Serializable
        @SerialName("StreamPayloadText")
        class Text(val text: String) : Bidirectional
    }

    companion object {
        fun clientConnect(streamId: Short, route: String) = StreamMessage<Payload.Client>(
            streamId,
            Payload.Client.Connect(route),
        )
        fun clientDisconnect(streamId: Short) = StreamMessage<Payload.Client>(
            streamId,
            Payload.Client.Disconnect
        )
        fun text(streamId: Short, text: String) = StreamMessage<Payload.Bidirectional>(
            streamId,
            Payload.Text(text)
        )
        fun serverError(streamId: Short, callstack: String?) =
            StreamMessage<Payload.Server>(streamId, Payload.Server.Error(callstack))
    }
}
