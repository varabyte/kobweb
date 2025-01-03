package playground.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.setBodyText
import java.util.*

@Api
fun generateId(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return
    ctx.res.setBodyText(UUID.randomUUID().toString())
}