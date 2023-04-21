package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.FocusEvent

/**
 * A contract for a strategy that determines whether a popup should stay open or not.
 *
 * If users want to provide their own strategy, they should instantiate a class that implements this interface and
 * `remember` it somewhere, before passing it into a widget that uses it.
 *
 * Implementors may want to consider using [StayOpenStrategyBase] to handle some of the boilerplate.
 */
interface StayOpenStrategy {
    /**
     * A [State] that can be used to observe whether the popup should stay open or not.
     *
     * By being a state object, this allows children classes just to update the value and not worry about notifying
     * observers.
     */
    val stayOpenState: State<Boolean>

    /**
     * Whether the popup should hide immediately after it stops being open.
     *
     * If false, the popup will use its normal hide delay. Otherwise, the delay will be overridden and the popup will
     * close immediately.
     *
     * Note that if there are multiple strategies in play (see [CompositeStayOpenStrategy], they must ALL agree to hide
     * immediately.
     */
    val hideImmediately: Boolean

    /**
     * Initialize any state needed by the strategy.
     *
     * @param popupElement The raw DOM element that represents the popup. This is useful for attaching event listeners.
     */
    fun init(popupElement: HTMLElement)
}
val StayOpenStrategy.shouldStayOpen: Boolean get() = stayOpenState.value

/**
 * A base class for [StayOpenStrategy] implementations that handles some of the boilerplate.
 */
abstract class StayOpenStrategyBase : StayOpenStrategy {
    private val _stayOpenState = mutableStateOf(false)
    override val stayOpenState: State<Boolean> = _stayOpenState

    override val hideImmediately: Boolean = false

    var shouldStayOpen: Boolean
        get() = _stayOpenState.value
        protected set(value) { _stayOpenState.value = value }
}

/**
 * A [StayOpenStrategy] that keeps the popup open as long as the mouse cursor is inside the popup somewhere.
 */
class IsMouseOverStayOpenStrategy : StayOpenStrategyBase() {
    override fun init(popupElement: HTMLElement) {
        popupElement.addEventListener("mouseenter", EventListener { shouldStayOpen = true })
        popupElement.addEventListener("mouseleave", EventListener { shouldStayOpen = false })
    }
}

/**
 * A [StayOpenStrategy] that keeps the popup open as long as any elements within the popup have focus.
 */
class HasFocusStayOpenStrategy : StayOpenStrategyBase() {
    override fun init(popupElement: HTMLElement) {
        popupElement.addEventListener("focusin", { evt -> shouldStayOpen = true })
        popupElement.addEventListener("focusout", { evt ->
            val focusEvent = evt as FocusEvent
            val newFocus = focusEvent.relatedTarget as? Node
            shouldStayOpen = if (newFocus != null) popupElement.contains(newFocus) else false
        })
    }
}

/**
 * A [StayOpenStrategy] that allows the user to manually control whether the popup should stay open or not.
 *
 * This can be useful for one-off custom behavior, like a popup that stays open until you click a button elsewhere on
 * the page, or one that closes when a timer runs down, etc.
 */
class ManualStayOpenStrategy(override val hideImmediately: Boolean = true) : StayOpenStrategy {
    private val _stayOpenState = mutableStateOf(false)
    override val stayOpenState: State<Boolean> = _stayOpenState

    override fun init(popupElement: HTMLElement) { shouldStayOpen = true }

    var shouldStayOpen: Boolean
        get() = _stayOpenState.value
        set(value) { _stayOpenState.value = value }
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
class NeverStayOpenStrategy : StayOpenStrategy {
    private val _stayOpenState = mutableStateOf(false)
    override val stayOpenState: State<Boolean> = _stayOpenState
    override val hideImmediately = false

    override fun init(popupElement: HTMLElement) = Unit
}

/**
 * A [StayOpenStrategy] that combines multiple orthogonal strategies into one.
 *
 * As long as any of the strategies want to keep the popup open, this will treat that as a request to keep the popup
 * open.
 */
class CompositeStayOpenStrategy(private vararg val strategies: StayOpenStrategy) : StayOpenStrategy {
    override val stayOpenState: State<Boolean> = derivedStateOf {
        strategies.any { it.stayOpenState.value }
    }

    override val hideImmediately get() = strategies.all { it.hideImmediately }

    override fun init(popupElement: HTMLElement) {
        strategies.forEach { it.init(popupElement) }
    }
}
