package multimodule.chat.model

import kotlinx.serialization.Serializable

@Serializable
class Message(val username: String, val text: String)

@Serializable
class MessageEntry(val message: Message, val id: String)
