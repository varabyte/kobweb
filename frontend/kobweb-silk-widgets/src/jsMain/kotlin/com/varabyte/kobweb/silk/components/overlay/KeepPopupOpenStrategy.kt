package com.varabyte.kobweb.silk.components.overlay

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.FocusEvent

/**
 * A contract for a strategy that determines whether a popup should stay open or not.
 *
 * The strategy will be informed about some popup element, which it can query / listen to its events to determine if
 * it should be kept open or not.
 *
 * If users want to provide their own strategy, they should instantiate a class that implements this class and
 * `remember` it somewhere:
 *
 * ```
 * val manualStrategy = remember { KeepPopupOpenStrategy.manual() }
 * Popup(..., keepOpenStrategy = manualStrategy) {
 *   Button(onClick = { manualStrategy.shouldKeepOpen = false }) { Text("Close") }
 * }
 * ```
 *
 * Children classes should implement this and fire [emitShouldKeepOpen] when they detect some change that determines if
 * the popup should stay open or not.
 *
 * @param defaultValue The initial "keep open" value for this strategy. Defaults to `false`. Note that the same strategy
 *   can be reused for multiple lifecycles of a popup opening and closing, so each time it is reset, it will use set
 *   its state back to this default value.
 */
abstract class KeepPopupOpenStrategy(private val defaultValue: Boolean = false) {
    companion object; // Declared so we can extend it with strategies

    private val _keepOpenFlow = MutableStateFlow(defaultValue)

    /**
     * A flow which represents a stream of decisions by this strategy on whether the popup should stay open or not.
     *
     * Listeners can either collect the flow as normal, or check its value directly via `keepOpenFlow.value`.
     *
     * See also: [shouldKeepOpen]
     */
    val keepOpenFlow: StateFlow<Boolean> = _keepOpenFlow.asStateFlow()

    /**
     * Initialize any state needed by the strategy.
     *
     * @param popupElement The raw DOM element that represents the popup. This is useful for attaching event listeners.
     */
    open fun init(popupElement: HTMLElement) = Unit

    protected fun emitShouldKeepOpen(shouldKeepOpen: Boolean) {
        _keepOpenFlow.tryEmit(shouldKeepOpen)
    }

    /**
     * Reset the strategy to its initial state.
     *
     * This will be called whenever the popup is closed.
     */
    fun reset() {
        onResetting()
        emitShouldKeepOpen(defaultValue)
    }

    /** Method triggered right before a [reset] happens, provided in case implementors need to prepare for it. */
    protected open fun onResetting() = Unit
}

/** A readable convenience property that queries the underlying state flow. */
val KeepPopupOpenStrategy.shouldKeepOpen: Boolean get() = keepOpenFlow.value

/**
 * A [KeepPopupOpenStrategy] that keeps the popup open as long as the mouse cursor is inside the popup somewhere.
 */
fun KeepPopupOpenStrategy.Companion.onHover() = object : KeepPopupOpenStrategy() {
    override fun init(popupElement: HTMLElement) {
        popupElement.addEventListener("mouseenter", EventListener { emitShouldKeepOpen(true) })
        popupElement.addEventListener("mouseleave", EventListener { emitShouldKeepOpen(false) })
    }
}

/**
 * A [KeepPopupOpenStrategy] that keeps the popup open as long as any elements within the popup have focus.
 */
fun KeepPopupOpenStrategy.Companion.onFocus() = object : KeepPopupOpenStrategy() {
    override fun init(popupElement: HTMLElement) {
        popupElement.addEventListener("focusin", { emitShouldKeepOpen(true) })
        popupElement.addEventListener("focusout", { evt ->
            val focusEvent = evt as FocusEvent
            val newFocus = focusEvent.relatedTarget as? Node
            emitShouldKeepOpen(if (newFocus != null) popupElement.contains(newFocus) else false)
        })
    }
}

class ManualKeepPopupOpenStrategy internal constructor(): KeepPopupOpenStrategy(defaultValue = true) {
    var shouldKeepOpen: Boolean
        get() = keepOpenFlow.value
        set(value) { emitShouldKeepOpen(value) }
}

/**
 * A [KeepPopupOpenStrategy] that allows the user to manually control whether the popup should stay open or not.
 *
 * This strategy will always be defaulted to `true`, so if you want to close the popup, you must explicitly set
 * [shouldKeepOpen] to false. A common use-case here is that your popup has a close button that the user can click to
 * close the popup. Another approach could be a popup that closes when a timer runs down, etc.
 */
fun KeepPopupOpenStrategy.Companion.manual() = ManualKeepPopupOpenStrategy()

/**
 * A [KeepPopupOpenStrategy] which asks to never keep the popup open.
 *
 * As widgets default to a behavior that tries to keep the popup open if no strategy is explicitly provided, this can be
 * used to explicitly reject that behavior.
 *
 * If this is the strategy that is used, then a popup will only stay open as long as the mouse cursor is over the
 * original element that owns the popup.
 */
fun KeepPopupOpenStrategy.Companion.never() = object : KeepPopupOpenStrategy() {}

/**
 * A [KeepPopupOpenStrategy] that combines multiple orthogonal strategies into one.
 *
 * As long as any of the strategies want to keep the popup open, this will treat that as a request to keep the popup
 * open.
 */
fun KeepPopupOpenStrategy.Companion.combine(vararg strategies: KeepPopupOpenStrategy) = object : KeepPopupOpenStrategy() {
    init {
        strategies
            .map { it.keepOpenFlow }
            .merge()
            .onEach {
                val anyKeepOpen = strategies.any { it.shouldKeepOpen }
                if (anyKeepOpen != keepOpenFlow.value) {
                    emitShouldKeepOpen(anyKeepOpen)
                }
            }.launchIn(CoroutineScope(window.asCoroutineDispatcher()))
    }

    override fun init(popupElement: HTMLElement) {
        strategies.forEach { it.init(popupElement) }
    }

    override fun onResetting() {
        strategies.forEach { it.reset() }
    }
}


operator fun KeepPopupOpenStrategy.plus(other: KeepPopupOpenStrategy) = KeepPopupOpenStrategy.combine(this, other)