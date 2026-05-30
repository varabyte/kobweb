package com.varabyte.kobweb.server.ratelimiting

import com.varabyte.kobweb.project.conf.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.time.Duration.Companion.nanoseconds

private const val CLEANUP_INTERVAL_MULTIPLIER = 10

/**
 * A concurrent implementation of the Token Bucket rate-limiting algorithm.
 *
 * Each client identified by a key is assigned a [Bucket] which contains a number of tokens.
 *
 * Each incoming request consumes one token from the bucket and its
 * [tokens][Bucket.tokens] are replenished periodically at the rate of
 * [configuration][Server.RateLimiting.Configuration.TokenBucket]'s
 * [tokens][Server.RateLimiting.Configuration.TokenBucket.tokens] per
 * [duration][Server.RateLimiting.Configuration.TokenBucket.duration].
 *
 * @param configuration The parameters controlling the bucket's capacity, refill rate, and duration.
 */
class TokenBucketAlgorithm(val configuration: Server.RateLimiting.Configuration.TokenBucket) :
    RateLimitingAlgorithm {

    private val closed = AtomicBoolean(false)

    private val capacity = configuration.capacity
    private val refillDurationNs = configuration.duration.inWholeNanoseconds
    private val durationMs = configuration.duration.inWholeMilliseconds

    /**
     * Stores the internal token state for a single client.
     *
     * @property tokens The current number of available tokens in the bucket. This is represented
     * as a `Double` to support fractional token accumulation during fractional time intervals.
     *
     * @property lastRefillNs The `System.nanoTime()` timestamp of the last time tokens were calculated and added.
     *
     * @property lastAccessedNs The `System.nanoTime()` timestamp of the last time this bucket was accessed
     * via [consume]. Used by the background cleanup coroutine to detect and evict idle buckets.
     */
    private class Bucket(
        var tokens: Double,
        var lastRefillNs: Long,
        var lastAccessedNs: Long,
    )

    private val buckets = ConcurrentHashMap<String, Bucket>()

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        require(configuration.capacity > 0) { "capacity must be positive, was ${configuration.capacity}" }
        require(configuration.tokens > 0) { "tokens must be positive, was ${configuration.tokens}" }
        require(durationMs > 0) { "duration must be positive, was ${configuration.duration}" }

        coroutineScope.launch {
            val cleanUpIntervalNs = (durationMs * CLEANUP_INTERVAL_MULTIPLIER) * 1_000_000L

            while (isActive) {
                delay(cleanUpIntervalNs.nanoseconds)

                val now = System.nanoTime()

                buckets.keys.forEach { key ->
                    buckets.compute(key) { _, bucket ->
                        if (bucket == null) return@compute null

                        val elapsed = now - bucket.lastRefillNs
                        val intervals = elapsed.toDouble() / refillDurationNs
                        val newTokens = intervals * configuration.tokens
                        val projectedTokens = min(bucket.tokens + newTokens, capacity.toDouble())

                        val isIdle = (now - bucket.lastAccessedNs) >= cleanUpIntervalNs

                        if (projectedTokens >= capacity && isIdle) null else bucket
                    }
                }
            }
        }
    }

    /**
     * Attempts to consume a token for the given client identifier.
     *
     * @param key The unique identifier for the client (e.g., an IP address).
     * @return A [RateLimitingResult] indicating whether the request is allowed and metadata for the response headers.
     */
    override fun consume(key: String): RateLimitingResult {
        check(!closed.get()) { "Algorithm has been closed" }

        lateinit var result: RateLimitingResult

        buckets.compute(key) { _, current ->
            val nowNs = System.nanoTime()
            val nowMs = System.currentTimeMillis()

            val bucket = current ?: Bucket(
                tokens = capacity.toDouble(),
                lastRefillNs = nowNs,
                lastAccessedNs = nowNs,
            )

            val elapsed = nowNs - bucket.lastRefillNs
            val intervals = elapsed.toDouble() / refillDurationNs
            val newTokens = intervals * configuration.tokens

            bucket.tokens = min(bucket.tokens + newTokens, capacity.toDouble())
            bucket.lastRefillNs = nowNs
            bucket.lastAccessedNs = nowNs

            if (bucket.tokens >= 1.0) {
                bucket.tokens--

                result = RateLimitingResult(
                    allowed = true,
                    retryAfterMs = 0,
                    limit = capacity,
                    remaining = floor(bucket.tokens).toInt(),
                    resetsAtMs = nowMs + durationMs
                )
            } else {
                val retryAfterMs =
                    ceil((1.0 - bucket.tokens) / configuration.tokens * durationMs).toLong()

                result = RateLimitingResult(
                    allowed = false,
                    retryAfterMs = retryAfterMs,
                    limit = capacity,
                    remaining = 0,
                    resetsAtMs = nowMs + retryAfterMs
                )
            }

            bucket
        }

        return result
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        coroutineScope.cancel()
        buckets.clear()
    }
}
