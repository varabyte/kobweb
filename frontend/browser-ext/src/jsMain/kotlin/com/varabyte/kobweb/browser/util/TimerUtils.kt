package com.varabyte.kobweb.browser.util

import org.w3c.dom.WindowOrWorkerGlobalScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private external val self: WindowOrWorkerGlobalScope

/**
 * A handle to a `setTimeout` or `setInterval` call which can be used to cancel it.
 *
 * This is essentially a convenient replacement for:
 * ```
 * val id = window.setTimeout(block, timeout)
 * window.clearTimeout(id)
 * ```
 *
 * with the advantage that it can be used for both `setTimeout` and `setInterval` calls, as well as mixed mode calls,
 * like starting with an initial timeout before leading into a repeated interval.
 */
class CancellableActionHandle(id: Int, private var isInterval: Boolean = false) {
    companion object {
        /**
         * A fake handle which can be used as a stub if you need to initialize a handle before a timer is started.
         *
         * For example, in code like:
         *
         * ```
         * var handle = CancellableActionHandle.Stub
         * handle = window.setInterval(timeToWaitPerAttempt) {
         *   if (someCondition) {
         *      handle.cancel()
         *   }
         * }
         * ```
         *
         * In the above case, `handle` will be correctly set by the time the first interval callback is triggered.
         *
         * Without this stub, the user would have to declare `handle` as nullable, resulting in `handle!!` calls in
         * common cases like the above example represents.
         */
        val Stub = CancellableActionHandle(0)
    }

    internal var id = id
        private set

    fun cancel() {
        if (isInterval) {
            self.clearInterval(id)
        } else {
            self.clearTimeout(id)
        }
        id = 0
    }

    internal fun setTo(other: CancellableActionHandle) {
        this.id = other.id
        this.isInterval = other.isInterval
    }
}

/**
 * A helper function to invoke a block of code on the next event loop.
 *
 * This can be useful if you want to ensure an action gets delayed until the DOM has a chance to process all pending
 * requests. This can occasionally be important to ensure an action happens AFTER the current page recomposition
 * finishes, for example.
 *
 * This is equivalent to `window.setTimeout(block, 0)` but with a parameter order that takes advantage of Kotlin's
 * lambda syntax.
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun WindowOrWorkerGlobalScope.invokeLater(block: () -> Unit): CancellableActionHandle {
    return setTimeout(0.milliseconds, block)
}

/**
 * A more Kotlin-friendly version of `window.setTimeout` (with a [Duration] & the lambda parameter last).
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun WindowOrWorkerGlobalScope.setTimeout(timeout: Duration, block: () -> Unit): CancellableActionHandle {
    val id = setTimeout(block, timeout.inWholeMilliseconds.toInt())
    return CancellableActionHandle(id)
}

/**
 * A more Kotlin-friendly version of `window.setInterval` (with a [Duration] & the lambda parameter last).
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun WindowOrWorkerGlobalScope.setInterval(delay: Duration, block: () -> Unit): CancellableActionHandle {
    val id = setInterval(block, delay.inWholeMilliseconds.toInt())
    return CancellableActionHandle(id, isInterval = true)
}

/**
 * A version of [setInterval] where the initial delay is different from the followup delay.
 *
 * This can be useful if you need to fire something immediately but then have a different delay for subsequent
 * invocations.
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun WindowOrWorkerGlobalScope.setInterval(initialDelay: Duration, delay: Duration, block: () -> Unit): CancellableActionHandle {
    lateinit var handle: CancellableActionHandle
    handle = setTimeout(initialDelay) {
        block()
        handle.setTo(setInterval(delay, block))
    }
    return handle
}

/**
 * A version of [setInterval] where the action is fired immediately then put on a timer.
 *
 * In practice, this behavior should be identical to `setInterval(initialDelay = 0.milliseconds, delay, block)`, but
 * this version expresses a more explicit intent. Also, this version invokes the callback *immediately* unlike the
 * `setInterval(0.milliseconds)` method which invokes the callback on the next event loop at the earliest.
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun WindowOrWorkerGlobalScope.invokeThenInterval(delay: Duration, block: () -> Unit): CancellableActionHandle {
    block()
    return setInterval(delay, block)
}
