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
import kotlin.time.Duration.Companion.milliseconds

private const val CLEANUP_INTERVAL_MULTIPLIER = 10

/**
 * A concurrent implementation of the Fixed Window rate-limiting algorithm.
 *
 * Time is divided into discrete, non-overlapping windows of [duration][Server.RateLimiting.Configuration.FixedWindow.duration].
 * Each window allows up to [size][Server.RateLimiting.Configuration.FixedWindow.size] requests per client.
 * When the current window expires, the counter resets.
 *
 * Window boundaries are computed from epoch time via integer division (`nowMs / durationMs`), so all clients
 * share the same window boundaries for a given duration.
 *
 * @param configuration The parameters controlling the window size and duration.
 */
class FixedWindowAlgorithm(val configuration: Server.RateLimiting.Configuration.FixedWindow) :
    RateLimitingAlgorithm {

    private val closed = AtomicBoolean(false)

    private val durationMs = configuration.duration.inWholeMilliseconds

    /**
     * The state of a single client's request counter within its current window.
     *
     * @property id The window identifier, computed as `System.currentTimeMillis() / durationMs`.
     * When the current time advances past this window, the counter resets.
     * @property count The number of requests made within this window.
     */
    private class WindowState(
        var id: Long,
        var count: Int,
    )

    private val states = ConcurrentHashMap<String, WindowState>()

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        require(configuration.size > 0) { "size must be positive, was ${configuration.size}" }
        require(durationMs > 0) { "duration must be positive, was ${configuration.duration}" }

        coroutineScope.launch {
            val cleanUpIntervalMs = durationMs * CLEANUP_INTERVAL_MULTIPLIER

            while (isActive) {
                delay(cleanUpIntervalMs.milliseconds)

                val nowMs = System.currentTimeMillis()

                val currentId = nowMs / durationMs

                states.forEach { (key, _) ->
                    states.compute(key) { _, state ->
                        if (state == null) return@compute null
                        if (state.id < currentId) null else state
                    }
                }
            }
        }
    }

    /**
     * Attempts to consume a request slot for the given client identifier.
     *
     * If the current window still has capacity, the request is allowed and the counter is incremented.
     * If the window is exhausted, the request is rejected with a retry-after hint.
     *
     * @param key The unique identifier for the client (e.g., an IP address).
     * @return A [RateLimitingResult] indicating whether the request is allowed and metadata for the response headers.
     */
    override fun consume(key: String): RateLimitingResult {
        check(!closed.get()) { "Algorithm has been closed" }

        lateinit var result: RateLimitingResult

        states.compute(key) { _, current ->
            val nowMs = System.currentTimeMillis()

            val currentId = nowMs / durationMs
            val resetsAtMs = (currentId + 1) * durationMs

            val state = current ?: WindowState(id = currentId, count = 0)

            if (state.id != currentId) {
                state.id = currentId
                state.count = 0
            }

            if (state.count < configuration.size) {
                state.count++

                result = RateLimitingResult(
                    allowed = true,
                    retryAfterMs = 0,
                    limit = configuration.size,
                    remaining = configuration.size - state.count,
                    resetsAtMs = resetsAtMs
                )
            } else {
                result = RateLimitingResult(
                    allowed = false,
                    retryAfterMs = resetsAtMs - nowMs,
                    limit = configuration.size,
                    remaining = 0,
                    resetsAtMs = resetsAtMs
                )
            }

            state
        }

        return result
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        coroutineScope.cancel()
        states.clear()
    }
}
