package playground.api

import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.intercept.ApiInterceptor
import com.varabyte.kobweb.api.intercept.ApiInterceptorContext

// Visit http://localhost:8080/api/hello and then open .kobweb/server/logs/kobweb-server.log
// to confirm that this interceptor was triggered.

@ApiInterceptor
suspend fun interceptRequest(ctx: ApiInterceptorContext): Response {
    ctx.logger.debug("Intercepting request for ${ctx.path}.")
    return ctx.dispatcher.dispatch()
}
