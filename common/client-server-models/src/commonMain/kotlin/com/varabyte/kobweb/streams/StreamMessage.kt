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
        @Serializable
        @SerialName("StreamPayloadClientConnect")
        object ClientConnect : Payload

        @Serializable
        @SerialName("StreamPayloadClientDisconnect")
        object ClientDisconnect : Payload

        @Serializable
        @SerialName("StreamPayloadText")
        class Text(val text: String) : Payload
    }

    companion object {
        fun clientConnect(route: String) = StreamMessage(route, Payload.ClientConnect)
        fun clientDisconnect(route: String) = StreamMessage(route, Payload.ClientDisconnect)
        fun text(route: String, text: String) = StreamMessage(route, Payload.Text(text))
    }
}
