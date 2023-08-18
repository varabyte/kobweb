package com.varabyte.kobweb.silk.components.overlay

import com.varabyte.kobweb.compose.events.EventListenerManager
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
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
 * Popover(..., keepOpenStrategy = manualStrategy) {
 *   Button(onClick = { manualStrategy.shouldKeepOpen = false }) { Text("Close") }
 * }
 * ```
 *
 * Children classes should implement this and fire [emitShouldKeepOpen] when they detect some change that determines if
 * the popup should stay open or not.
 *
 * @param defaultValue The initial "keep open" value for this strategy. Defaults to `false`. Note that the same strategy
 *   can be reused for multiple lifecycles of a popup opening and closing, so each time it is reset, it will set
 *   its state back to this default value.
 */
abstract class KeepPopupOpenStrategy(private val defaultValue: Boolean = false) {
    companion object; // Declared so we can extend it with strategies

    private val _keepOpenFlow = MutableStateFlow(defaultValue)

    /** Whether this strategy is tied to an actively open popup or not. */
    private var isActive = false

    /**
     * A flow which represents a stream of decisions by this strategy on whether the popup should stay open or not.
     *
     * Listeners can either collect the flow as normal, or check its value directly via `keepOpenFlow.value`.
     *
     * @see [shouldKeepOpen]
     */
    val keepOpenFlow: StateFlow<Boolean> = _keepOpenFlow.asStateFlow()

    /**
     * Initialize any state needed by the strategy.
     *
     * @param popupElement The raw DOM element that represents the popup. This is useful for attaching event listeners.
     */
    fun init(popupElement: HTMLElement) {
        _keepOpenFlow.value = defaultValue
        onInit(popupElement)
        isActive = true
    }

    protected open fun onInit(popupElement: HTMLElement) = Unit

    protected fun emitShouldKeepOpen(shouldKeepOpen: Boolean) {
        // Avoid updating state when this strategy is not active, since it will just get overwritten on init anyway
        if (isActive) _keepOpenFlow.value = shouldKeepOpen
    }

    /**
     * Reset the strategy to its initial state.
     *
     * This will be called whenever the popup is closed.
     */
    fun reset() {
        isActive = false
        onResetting()
        _keepOpenFlow.value = defaultValue
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
    private var manager: EventListenerManager? = null

    override fun onInit(popupElement: HTMLElement) {
        manager = EventListenerManager(popupElement).apply {
            addEventListener("mouseenter") { emitShouldKeepOpen(true) }
            addEventListener("mouseleave") { emitShouldKeepOpen(false) }
        }
    }

    override fun onResetting() {
        manager!!.clearAllListeners()
        manager = null
    }
}

/**
 * A [KeepPopupOpenStrategy] that keeps the popup open as long as any elements within the popup have focus.
 */
fun KeepPopupOpenStrategy.Companion.onFocus() = object : KeepPopupOpenStrategy() {
    private var manager: EventListenerManager? = null

    override fun onInit(popupElement: HTMLElement) {
        manager = EventListenerManager(popupElement).apply {
            addEventListener("focusin") { emitShouldKeepOpen(true) }
            addEventListener("focusout") { evt ->
                val focusEvent = evt as FocusEvent
                val newFocus = focusEvent.relatedTarget as? Node
                emitShouldKeepOpen(if (newFocus != null) popupElement.contains(newFocus) else false)
            }
        }
    }

    override fun onResetting() {
        manager!!.clearAllListeners()
        manager = null
    }
}

class ManualKeepPopupOpenStrategy internal constructor(defaultValue: Boolean) : KeepPopupOpenStrategy(defaultValue) {
    var shouldKeepOpen: Boolean
        get() = keepOpenFlow.value
        set(value) {
            emitShouldKeepOpen(value)
        }
}

/**
 * A [KeepPopupOpenStrategy] that allows the user to manually control whether the popup should stay open or not.
 *
 * This strategy defaults to `true`, meaning if you want to close the popup, you must explicitly set [shouldKeepOpen] to
 * false. A common use-case here is that your popup has a close button that the user can click to close the popup.
 * Another approach could be a popup that closes when a timer runs down, etc.
 */
fun KeepPopupOpenStrategy.Companion.manual(defaultValue: Boolean = true) = ManualKeepPopupOpenStrategy(defaultValue)

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
fun KeepPopupOpenStrategy.Companion.combine(vararg strategies: KeepPopupOpenStrategy) =
    object : KeepPopupOpenStrategy() {
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

        override fun onInit(popupElement: HTMLElement) {
            strategies.forEach { it.init(popupElement) }
        }

        override fun onResetting() {
            strategies.forEach { it.reset() }
        }
    }

/**
 * A strategy that observes another strategy and reflects its state.
 *
 * Reset and init intentionally do nothing in this strategy, as it exists to be a read-only layer on top of another
 * strategy.
 *
 * A useful case for this is if you have a primary popup that stays up as long as a secondary popup is open. In that
 * case, the secondary popup would have its own strategy for staying open, and the primary popup can observe it.
 */
fun KeepPopupOpenStrategy.observed() = run {
    val self = this
    object : KeepPopupOpenStrategy(self.shouldKeepOpen) {
        init {
            self.keepOpenFlow
                .onEach { emitShouldKeepOpen(it) }
                .launchIn(CoroutineScope(window.asCoroutineDispatcher()))
        }
    }
}

operator fun KeepPopupOpenStrategy.plus(other: KeepPopupOpenStrategy) = KeepPopupOpenStrategy.combine(this, other)
