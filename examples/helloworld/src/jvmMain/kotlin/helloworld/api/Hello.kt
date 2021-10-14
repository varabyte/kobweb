package helloworld.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext

@Api
fun hello(ctx: ApiContext) {
    ctx.res.payload = "hello world".toByteArray()
}