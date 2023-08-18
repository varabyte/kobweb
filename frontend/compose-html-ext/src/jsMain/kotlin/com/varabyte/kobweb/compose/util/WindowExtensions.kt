package com.varabyte.kobweb.compose.util

import kotlinx.browser.window
import org.w3c.dom.Window
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class CancellableActionHandle(id: Int, isInterval: Boolean = false) {
    var id = id
        private set

    var isInterval = isInterval
        private set

    fun cancel() {
        if (isInterval) {
            window.clearInterval(id)
        } else {
            window.clearTimeout(id)
        }
        id = 0
    }

    fun setTo(other: CancellableActionHandle) {
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
fun Window.invokeLater(block: () -> Unit): CancellableActionHandle {
    return setTimeout(0.milliseconds, block)
}

/**
 * An alternate version of `window.setTimeout` in a more Kotlin-friendly form (with the lambda parameter last).
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
@Deprecated(
    "Use the Duration version instead.",
    ReplaceWith(
        "setTimeout(timeout.milliseconds, block)",
        "kotlin.time.Duration.Companion.milliseconds",
    )
)
fun Window.setTimeout(timeout: Int, block: () -> Unit): CancellableActionHandle =
    setTimeout(timeout.milliseconds, block)

/**
 * A more Kotlin-friendly version of `window.setTimeout` (with a [Duration] & the lambda parameter last).
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun Window.setTimeout(timeout: Duration, block: () -> Unit): CancellableActionHandle {
    val id = window.setTimeout(block, timeout.inWholeMilliseconds.toInt())
    return CancellableActionHandle(id)
}

/**
 * An alternate version of `window.setInterval` in a more Kotlin-friendly form (with the lambda parameter last).
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
@Deprecated(
    "Use the Duration version instead.",
    ReplaceWith(
        "setInterval(delay.milliseconds, block)",
        "kotlin.time.Duration.Companion.milliseconds",
    )
)
fun Window.setInterval(delay: Int, block: () -> Unit): CancellableActionHandle = setInterval(delay.milliseconds, block)

/**
 * A more Kotlin-friendly version of `window.setInterval` (with a [Duration] & the lambda parameter last).
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun Window.setInterval(delay: Duration, block: () -> Unit): CancellableActionHandle {
    val id = window.setInterval(block, delay.inWholeMilliseconds.toInt())
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
@Deprecated(
    "Use the Duration version instead.",
    ReplaceWith(
        "setInterval(initialDelay.milliseconds, delay.milliseconds, block)",
        "kotlin.time.Duration.Companion.milliseconds",
    )
)
fun Window.setInterval(initialDelay: Int, delay: Int, block: () -> Unit): CancellableActionHandle =
    setInterval(initialDelay.milliseconds, delay.milliseconds, block)

/**
 * A version of [setInterval] where the initial delay is different from the followup delay.
 *
 * This can be useful if you need to fire something immediately but then have a different delay for subsequent
 * invocations.
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun Window.setInterval(initialDelay: Duration, delay: Duration, block: () -> Unit): CancellableActionHandle {
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
 * In practice, this behavior should be identical to `setInterval(initialDelay = 0, delay, block)`, but this version
 * expresses a more explicit intent. Also, this version invokes the callback *immediately* unlike the
 * `setInterval(0)` method which invokes the callback on the next event loop at the earliest.
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
@Deprecated(
    "Use the Duration version instead.",
    ReplaceWith(
        "invokeThenInterval(delay.milliseconds, block)",
        "kotlin.time.Duration.Companion.milliseconds",
    )
)
fun Window.invokeThenInterval(delay: Int, block: () -> Unit): CancellableActionHandle =
    invokeThenInterval(delay.milliseconds, block)

/**
 * A version of [setInterval] where the action is fired immediately then put on a timer.
 *
 * In practice, this behavior should be identical to `setInterval(initialDelay = 0.milliseconds, delay, block)`, but
 * this version expresses a more explicit intent. Also, this version invokes the callback *immediately* unlike the
 * `setInterval(0.milliseconds)` method which invokes the callback on the next event loop at the earliest.
 *
 * @return An [CancellableActionHandle] to the action being invoked. Use [CancellableActionHandle.cancel] to cancel the action.
 */
fun Window.invokeThenInterval(delay: Duration, block: () -> Unit): CancellableActionHandle {
    block()
    return setInterval(delay, block)
}
