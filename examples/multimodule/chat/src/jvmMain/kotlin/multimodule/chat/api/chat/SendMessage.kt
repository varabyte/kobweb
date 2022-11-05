package multimodule.chat.api.chat

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.EMPTY_BODY
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.readBodyText
import kotlinx.serialization.json.Json
import multimodule.chat.model.Message
import multimodule.chat.model.MessageEntry
import multimodule.chat.model.Messages
import java.util.*

@Api
fun sendMessage(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return
    val message = Json.decodeFromString(Message.serializer(), ctx.req.readBodyText()!!)
    val messages = ctx.data.getValue<Messages>()
    messages.list.add(MessageEntry(message, UUID.randomUUID().toString()))

    ctx.res.body = EMPTY_BODY
}