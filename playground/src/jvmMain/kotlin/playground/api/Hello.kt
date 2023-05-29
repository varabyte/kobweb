package playground.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext

@Api
fun hello(ctx: ApiContext) {
    ctx.res.body = "hello world".toByteArray()
}