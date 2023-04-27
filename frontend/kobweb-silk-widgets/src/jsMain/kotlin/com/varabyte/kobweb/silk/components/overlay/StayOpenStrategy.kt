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
 * If users want to provide their own strategy, they should instantiate a class that implements this class and
 * `remember` it somewhere:
 *
 * ```
 * val manualStrategy = remember { ManualStayOpenStrategy() }
 * Popup(..., stayOpenStrategy = manualStrategy) {
 *   Button(onClick = { manualStrategy.shouldStayOpen = false }) { Text("Close") }
 * }
 * ```
 */
abstract class StayOpenStrategy(private val defaultValue: Boolean = false) {
    private val _stayOpenFlow = MutableStateFlow(defaultValue)

    /**
     * A flow which represents a stream of decisions by this strategy on whether the popup should stay open or not.
     *
     * Listeners can either collect the flow as normal, or check its value directly via `stayOpenFlow.value`.
     *
     * See also: [shouldStayOpen]
     */
    val stayOpenFlow: StateFlow<Boolean> = _stayOpenFlow.asStateFlow()

    /**
     * Initialize any state needed by the strategy.
     *
     * @param popupElement The raw DOM element that represents the popup. This is useful for attaching event listeners.
     */
    open fun init(popupElement: HTMLElement) = Unit

    protected fun emitShouldStayOpen(shouldStayOpen: Boolean) {
        _stayOpenFlow.tryEmit(shouldStayOpen)
    }

    /**
     * Reset the strategy to its initial state.
     *
     * This will be called whenever the popup is closed.
     */
    fun reset() {
        emitShouldStayOpen(defaultValue)
    }
}

/** A readable convenience property that queries the underlying state flow. */
val StayOpenStrategy.shouldStayOpen: Boolean get() = stayOpenFlow.value

/**
 * A [StayOpenStrategy] that keeps the popup open as long as the mouse cursor is inside the popup somewhere.
 */
class IsMouseOverStayOpenStrategy : StayOpenStrategy() {
    override fun init(popupElement: HTMLElement) {
        popupElement.addEventListener("mouseenter", EventListener { emitShouldStayOpen(true) })
        popupElement.addEventListener("mouseleave", EventListener { emitShouldStayOpen(false) })
        emitShouldStayOpen(popupElement.matches(":hover"))
    }
}

/**
 * A [StayOpenStrategy] that keeps the popup open as long as any elements within the popup have focus.
 */
class HasFocusStayOpenStrategy : StayOpenStrategy() {
    override fun init(popupElement: HTMLElement) {
        popupElement.addEventListener("focusin", { emitShouldStayOpen(true) })
        popupElement.addEventListener("focusout", { evt ->
            val focusEvent = evt as FocusEvent
            val newFocus = focusEvent.relatedTarget as? Node
            emitShouldStayOpen(if (newFocus != null) popupElement.contains(newFocus) else false)
        })
        emitShouldStayOpen(popupElement.contains(window.document.activeElement))
    }
}

/**
 * A [StayOpenStrategy] that allows the user to manually control whether the popup should stay open or not.
 *
 * This strategy will always be defaulted to `true`, so if you want to close the popup, you must explicitly set
 * [shouldStayOpen] to false. A common use-case here is that your popup has a close button that the user can click to
 * close the popup. Another approach could be a popup that closes when a timer runs down, etc.
 */
class ManualStayOpenStrategy : StayOpenStrategy(defaultValue = true) {
    var shouldStayOpen: Boolean
        get() = stayOpenFlow.value
        set(value) { emitShouldStayOpen(value) }
}

/**
 * A [StayOpenStrategy] which asks to never keep the popup open.
 *
 * As widgets default to a behavior that tries to keep the popup open if no strategy is explicitly provided, this can be
 * used to explicitly reject that behavior.
 *
 * If this is the strategy that is used, then a popup will only stay open as long as the mouse cursor is over the
 * original element that owns the popup.
 */
class NeverStayOpenStrategy : StayOpenStrategy()

/**
 * A [StayOpenStrategy] that combines multiple orthogonal strategies into one.
 *
 * As long as any of the strategies want to keep the popup open, this will treat that as a request to keep the popup
 * open.
 */
class CompositeStayOpenStrategy(private vararg val strategies: StayOpenStrategy) : StayOpenStrategy() {
    init {
        strategies
            .map { it.stayOpenFlow }
            .merge()
            .onEach {
                val anyOpen = strategies.any { it.shouldStayOpen }
                if (anyOpen != stayOpenFlow.value) {
                    emitShouldStayOpen(anyOpen)
                }
            }.launchIn(CoroutineScope(window.asCoroutineDispatcher()))
    }

    override fun init(popupElement: HTMLElement) {
        strategies.forEach { it.init(popupElement) }
    }
}
