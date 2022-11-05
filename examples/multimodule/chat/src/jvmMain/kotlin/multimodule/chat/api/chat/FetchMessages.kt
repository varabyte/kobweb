package multimodule.chat.api.chat

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.readBodyText
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import multimodule.chat.model.FetchRequest
import multimodule.chat.model.FetchResponse
import multimodule.chat.model.Messages

@Api
fun fetchMessages(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return
    val fetchRequest = Json.decodeFromString(FetchRequest.serializer(), ctx.req.params["request"]!!)
    val messages = ctx.data.getValue<Messages>()

    val fetched = if (fetchRequest.afterId == null) {
        messages.list
    } else {
        messages.list.dropWhile { it.id != fetchRequest.afterId }.drop(1)
    }

    ctx.res.setBodyText(Json.encodeToString(FetchResponse.serializer(), FetchResponse(fetched)))
}