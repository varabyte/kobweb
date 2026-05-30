package com.varabyte.kobweb.server.ratelimiting

/**
 * Represents the result of a rate limiting check.
 *
 * @property allowed Whether the request is permitted.
 * @property retryAfterMs The number of milliseconds the client should wait before retrying.
 *   This is `0` when [allowed] is `true`.
 * @property limit The maximum number of requests allowed within the rate limiting window.
 * @property remaining The number of requests remaining within the current window.
 * @property resetsAtMs The wall-clock timestamp (epoch millis) at which the rate limit window resets.
 */
data class RateLimitingResult(
    val allowed: Boolean,
    val retryAfterMs: Long,
    val limit: Int,
    val remaining: Int,
    val resetsAtMs: Long,
)

/** Algorithm for rate limiting requests. */
interface RateLimitingAlgorithm : AutoCloseable {
    /** Check if a request for the given key is allowed. */
    fun consume(key: String): RateLimitingResult

    /** Closes the rate limiter to new requests and cleans up resources. */
    override fun close()
}
