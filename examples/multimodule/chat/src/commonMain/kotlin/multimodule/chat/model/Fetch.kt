package multimodule.chat.model

import kotlinx.serialization.Serializable

@Serializable
class FetchRequest(val afterId: String? = null)

@Serializable
class FetchResponse(val messages: List<MessageEntry>)