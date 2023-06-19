package com.varabyte.kobweb.compose.util

import kotlinx.browser.window
import org.w3c.dom.Window

class ActionHandle(id: Int, isInterval: Boolean = false) {
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

    fun setTo(other: ActionHandle) {
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
 * @return An [ActionHandle] to the action being invoked. Use [ActionHandle.cancel] to cancel the action.
 */
fun Window.invokeLater(block: () -> Unit): ActionHandle {
    return setTimeout(0, block)
}

/**
 * An alternate version of `window.setTimeout` in a more Kotlin-friendly form (with the lambda parameter last).
 *
 * @return An [ActionHandle] to the action being invoked. Use [ActionHandle.cancel] to cancel the action.
 */
fun Window.setTimeout(timeout: Int, block: () -> Unit): ActionHandle {
    val id = window.setTimeout(block, timeout)
    return ActionHandle(id)
}

/**
 * An alternate version of `window.setInterval` in a more Kotlin-friendly form (with the lambda parameter last).
 *
 * @return An [ActionHandle] to the action being invoked. Use [ActionHandle.cancel] to cancel the action.
 */
fun Window.setInterval(delay: Int, block: () -> Unit): ActionHandle {
    val id = window.setInterval(block, delay)
    return ActionHandle(id, isInterval = true)
}

/**
 * A version of [setInterval] where the initial delay is different from the followup delay.
 *
 * This can be useful if you need to fire something immediately but then have a different delay for subsequent
 * invocations.
 *
 * @return An [ActionHandle] to the action being invoked. Use [ActionHandle.cancel] to cancel the action.
 */
fun Window.setInterval(initialDelay: Int, delay: Int, block: () -> Unit): ActionHandle {
    lateinit var handle: ActionHandle
    handle = window.setTimeout(initialDelay) {
        block()
        handle.setTo(window.setInterval(delay, block))
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
 * @return An [ActionHandle] to the action being invoked. Use [ActionHandle.cancel] to cancel the action.
 */
fun Window.invokeThenInterval(delay: Int, block: () -> Unit): ActionHandle {
    block()
    return setInterval(delay, block)
}
