package com.varabyte.kobweb.streams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A message sent from the client to the server, to deliver a payload to a target API stream.
 */
@Serializable
class StreamMessage(
    val route: String,
    val payload: Payload,
) {
    @Serializable
    sealed interface Payload {
        /**
         * Payloads that are sent from the client to the server.
         */
        @Serializable
        sealed interface Client : Payload {
            @Serializable
            @SerialName("StreamPayloadClientConnect")
            object Connect : Client

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
        fun clientConnect(route: String) = StreamMessage(route, Payload.Client.Connect)
        fun clientDisconnect(route: String) = StreamMessage(route, Payload.Client.Disconnect)
        fun text(route: String, text: String) = StreamMessage(route, Payload.Text(text))
        fun serverError(route: String, callstack: String?) = StreamMessage(route, Payload.Server.Error(callstack))
    }
}
