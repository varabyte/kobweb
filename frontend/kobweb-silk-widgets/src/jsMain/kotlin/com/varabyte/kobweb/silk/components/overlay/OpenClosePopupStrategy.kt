package com.varabyte.kobweb.silk.components.overlay

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventListener

enum class OpenClose {
    OPEN,
    CLOSE,
}

/**
 * A contract for a strategy that determines when a popup should open or close.
 *
 * The strategy will be informed about some target element, which represents the element that the popup is anchored to.
 * In other words, interaction with that element is what should trigger the popup to open or close.
 *
 * Children classes should implement this and fire [emitRequest] when they want to request opening or closing the popup.
 */
abstract class OpenClosePopupStrategy {
    companion object; // Declared so we can extend it with strategies

    private val _requestFlow = MutableStateFlow(OpenClose.CLOSE)

    val requestFlow: StateFlow<OpenClose> = _requestFlow.asStateFlow()

    /**
     * Initialize this strategy with some target element.
     *
     * This is *not* the element that represents the popup itself, but rather the element that the popup is anchored to.
     */
    open fun init(targetElement: HTMLElement) = Unit

    /**
     * Release resources (if necessary) allocated by [init].
     */
    open fun reset() = Unit

    protected fun emitRequest(request: OpenClose) {
        _requestFlow.tryEmit(request)
    }
}

/** A readable convenience property that queries the underlying state flow. */
val OpenClosePopupStrategy.isOpen: Boolean get() = requestFlow.value == OpenClose.OPEN

private class EventListenerManager(private val element: HTMLElement) {
    private val listeners = mutableMapOf<String, EventListener>()

    fun addEventListener(type: String, listener: EventListener) {
        listeners[type] = listener
        element.addEventListener(type, listener)
    }

    fun clearAllListeners() {
        listeners.forEach { (type, listener) ->
            element.removeEventListener(type, listener)
        }
        listeners.clear()
    }
}

/**
 * A strategy that opens the popup when the cursor enters some target element and closes it when the cursor leaves.
 */
fun OpenClosePopupStrategy.Companion.onHover() = object : OpenClosePopupStrategy() {
    private var manager: EventListenerManager? = null

    override fun init(targetElement: HTMLElement) {
        manager = EventListenerManager(targetElement).apply {
            addEventListener("mouseenter", EventListener { emitRequest(OpenClose.OPEN) })
            addEventListener("mouseleave", EventListener { emitRequest(OpenClose.CLOSE) })
            if (targetElement.matches(":hover")) emitRequest(OpenClose.OPEN)
        }
    }

    override fun reset() {
        manager!!.clearAllListeners()
        manager = null
    }
}

/**
 * A strategy that opens the popup when an element gains focus and closes it when it loses focus.
 */
fun OpenClosePopupStrategy.Companion.onFocus() = object : OpenClosePopupStrategy() {
    private var manager: EventListenerManager? = null

    override fun init(targetElement: HTMLElement) {
        manager = EventListenerManager(targetElement).apply {
            addEventListener("focusin", EventListener { emitRequest(OpenClose.OPEN) })
            addEventListener("focusout", EventListener { emitRequest(OpenClose.CLOSE) })
            if (targetElement.contains(document.activeElement)) emitRequest(OpenClose.OPEN)
        }
    }

    override fun reset() {
        manager!!.clearAllListeners()
        manager = null
    }
}

class ManualOpenClosePopupStrategy internal constructor(): OpenClosePopupStrategy() {
    var isOpen: Boolean
        get() = requestFlow.value == OpenClose.OPEN
        set(value) {
            emitRequest(if (value) OpenClose.OPEN else OpenClose.CLOSE)
        }
}

/**
 * A strategy that allows the user to manually control when a popup should open or close.
 */
fun OpenClosePopupStrategy.Companion.manual() = ManualOpenClosePopupStrategy()

class TimedOpenClosePopupStrategy(val timeoutMs: Int) : OpenClosePopupStrategy() {
    private var timeoutHandle: Int? = null

    /**
     * Show the popup and kick off a timer.
     *
     * If a second call to this method is made before the timer expires, the timer will be reset.
     *
     * The user can also call [stopEarly] to stop the timer early and hide the popup immediately.
     */
    fun showAndStartTimer() {
        timeoutHandle?.let { window.clearTimeout(it) }
        emitRequest(OpenClose.OPEN)
        timeoutHandle = window.setTimeout({
            emitRequest(OpenClose.CLOSE)
            timeoutHandle = null
        }, timeoutMs)
    }

    /**
     * Interrupt the timer, if running, and hide the popup immediately.
     *
     * If the popup is already hidden, this method is a no-op.
     */
    fun stopEarly() {
        if (timeoutHandle != null) {
            window.clearTimeout(timeoutHandle!!)
            emitRequest(OpenClose.CLOSE)
            timeoutHandle = null
        }
    }
}

/**
 * A strategy that opens when the user starts a timer and then closes when the timer expires.
 */
fun OpenClosePopupStrategy.Companion.timed(timeoutMs: Int) = TimedOpenClosePopupStrategy(timeoutMs)


/**
 * A [KeepPopupOpenStrategy] that combines multiple orthogonal strategies into one.
 *
 * If any single strategy requests that the popup should open, then the popup will open. If any strategy requests that
 * the popup should close, then it will close. In other words, this is not a democratic strategy but rather a
 * dictatorial one.
 */
fun OpenClosePopupStrategy.Companion.combine(vararg strategies: OpenClosePopupStrategy) = object : OpenClosePopupStrategy() {
    init {
        strategies
            .map { it.requestFlow }
            .merge()
            .onEach { emitRequest(it) }
            .launchIn(CoroutineScope(window.asCoroutineDispatcher()))
    }

    override fun init(targetElement: HTMLElement) {
        strategies.forEach { it.init(targetElement) }
    }

    override fun reset() {
        strategies.forEach { it.reset() }
    }
}

operator fun OpenClosePopupStrategy.plus(other: OpenClosePopupStrategy) = OpenClosePopupStrategy.combine(this, other)