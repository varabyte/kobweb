package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.conf.Server
import com.varabyte.kobweb.server.ratelimiting.FixedWindowAlgorithm
import com.varabyte.kobweb.server.ratelimiting.RateLimitingAlgorithm
import com.varabyte.kobweb.server.ratelimiting.TokenBucketAlgorithm
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.plugins.origin
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import kotlin.math.ceil

private fun Server.RateLimiting.Configuration.toAlgorithm(): RateLimitingAlgorithm {
    return when (this) {
        is Server.RateLimiting.Configuration.TokenBucket -> TokenBucketAlgorithm(this)
        is Server.RateLimiting.Configuration.FixedWindow -> FixedWindowAlgorithm(this)
    }
}

private fun ApplicationCall.resolveKey(algorithm: RateLimitingAlgorithm): String {
    fun resolveKey(key: Server.RateLimiting.Key, customHeader: String): String {
        return when (key) {
            Server.RateLimiting.Key.IP -> request.origin.remoteAddress
            Server.RateLimiting.Key.FORWARD_IP -> {
                request.headers[HttpHeaders.XForwardedFor]
                    ?.split(',')
                    ?.firstOrNull()
                    ?.trim()
            }

            Server.RateLimiting.Key.CUSTOM_HEADER -> request.headers[customHeader]
        } ?: request.local.remoteAddress
    }

    return when (algorithm) {
        is TokenBucketAlgorithm -> {
            resolveKey(
                key = algorithm.configuration.key,
                customHeader = algorithm.configuration.customHeader
            )
        }

        is FixedWindowAlgorithm -> {
            resolveKey(
                key = algorithm.configuration.key,
                customHeader = algorithm.configuration.customHeader
            )
        }

        else -> throw KobwebException("Unsupported rate limiting algorithm: $algorithm")
    }
}

fun Application.configureRateLimiting(rateLimiting: Server.RateLimiting) {
    val logger = log

    if (!rateLimiting.enabled) {
        logger.info("Rate limiting is disabled, skipping configuration")
        return
    }

    val globalAlgorithm = rateLimiting.global.toAlgorithm()

    val routeAlgorithms = rateLimiting.routes
        .sortedByDescending { it.path.length }
        .map { route -> route.path to route.configuration.toAlgorithm() }

    intercept(ApplicationCallPipeline.Plugins) {
        val path = call.request.uri.substringBefore("?")

        val algorithm = routeAlgorithms.firstOrNull { (currentPath, _) ->
            path.startsWith(currentPath)
        }?.second ?: globalAlgorithm

        val key = call.resolveKey(algorithm)

        val result = algorithm.consume(key)

        call.response.headers.append(name = "X-RateLimit-Limit", value = result.limit.toString())
        call.response.headers.append(name = "X-RateLimit-Remaining", value = result.remaining.toString())
        call.response.headers.append(name = "X-RateLimit-Reset", value = (result.resetsAtMs / 1000).toString())

        if (!result.allowed) {
            call.response.headers.append(
                name = HttpHeaders.RetryAfter,
                value = ceil(result.retryAfterMs / 1000.0).toLong().toString()
            )

            logger.debug("Rate limited: key={}, path={}, retryAfter={}ms", key, path, result.retryAfterMs)

            call.respond(HttpStatusCode.TooManyRequests)

            finish()
        }
    }

    monitor.subscribe(ApplicationStopped) {
        globalAlgorithm.close()

        routeAlgorithms.forEach { (_, algorithm) ->
            algorithm.close()
        }
    }
}