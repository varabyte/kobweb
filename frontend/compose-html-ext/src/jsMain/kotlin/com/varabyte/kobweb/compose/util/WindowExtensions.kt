@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.compose.util

import com.varabyte.kobweb.browser.util.invokeLater
import com.varabyte.kobweb.browser.util.invokeThenInterval
import com.varabyte.kobweb.browser.util.setInterval
import com.varabyte.kobweb.browser.util.setTimeout
import org.w3c.dom.Window
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.CancellableActionHandle` instead (that is, `compose` → `browser`).")
typealias CancellableActionHandle = com.varabyte.kobweb.browser.util.CancellableActionHandle

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
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.invokeLater` instead (that is, `compose` → `browser`).")
fun Window.invokeLater(block: () -> Unit) = invokeLater(block)

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
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.setTimeout` instead (that is, `compose` → `browser`).")
fun Window.setTimeout(timeout: Duration, block: () -> Unit) = setTimeout(timeout, block)

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
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.setInterval` instead (that is, `compose` → `browser`).")
fun Window.setInterval(delay: Duration, block: () -> Unit) = setInterval(delay, block)

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
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.setInterval` instead (that is, `compose` → `browser`).")
fun Window.setInterval(initialDelay: Duration, delay: Duration, block: () -> Unit) =
    setInterval(initialDelay, delay, block)

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
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.invokeThenInterval` instead (that is, `compose` → `browser`).")
fun Window.invokeThenInterval(delay: Duration, block: () -> Unit) = invokeThenInterval(delay, block)
