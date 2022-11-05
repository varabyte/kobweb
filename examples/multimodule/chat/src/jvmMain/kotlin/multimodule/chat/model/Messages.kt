package multimodule.chat.model

import com.varabyte.kobweb.api.InitApi
import com.varabyte.kobweb.api.InitApiContext
import com.varabyte.kobweb.api.data.add

@InitApi
fun initAccounts(ctx: InitApiContext) {
    ctx.data.add(Messages())
}

class Messages {
    val list = mutableListOf<MessageEntry>()
}