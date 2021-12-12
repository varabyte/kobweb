package todo.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.setBodyText
import java.util.*

/**
 * Instead of generating an ID on the client (where there's no UUID library by default), we just ask the server to do
 * it. It's doing it anyways for its own stuff!
 */
@Api
fun generateId(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return
    ctx.res.setBodyText(UUID.randomUUID().toString())
}