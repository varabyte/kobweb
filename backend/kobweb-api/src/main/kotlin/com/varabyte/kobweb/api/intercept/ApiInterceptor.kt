package com.varabyte.kobweb.api.intercept

import com.varabyte.kobweb.api.http.Response

/**
 * An annotation which tags a method that will be given a chance to process all incoming requests.
 *
 * The signature of the method being annotated must take a [ApiInterceptorContext] as its only parameter, and
 * return a [Response].
 *
 * The "no-op" implementation of an interceptor that doesn't change the normal behavior of a Kobweb server is as
 * follows:
 *
 * ```
 * @ApiInterceptor
 * suspend fun intercept(ctx: ApiInterceptorContext): Response {
 *   return ctx.dispatcher.dispatch()
 * }
 * ```
 *
 * but you can of course intercept specific API paths with the following pattern:
 *
 * ```
 * @ApiInterceptor
 * suspend fun intercept(ctx: ApiInterceptorContext): Response {
 *   if (ctx.path == "/example") {
 *     return Response().apply { setBodyText("Intercepted!") }
 *   }
 *   return ctx.dispatcher.dispatch()
 * }
 * ```
 *
 * This only works on intercepting API routes. API streams are not involved.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class ApiInterceptor
