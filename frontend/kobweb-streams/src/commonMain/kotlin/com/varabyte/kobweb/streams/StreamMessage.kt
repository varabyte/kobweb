package com.varabyte.kobweb.streams

import kotlinx.serialization.Serializable

/**
 * A message sent from the client to the server, to deliver a payload to a target API stream.
 */
@Serializable
class StreamMessage(
    val route: String,
    val payload: String,
)
