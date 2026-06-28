package playground.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.bodyOf

@Api
fun hello(ctx: ApiContext) {
    ctx.res.body = bodyOf("hello world")
}