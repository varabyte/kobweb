package com.varabyte.kobweb.silk.components.overlay

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventListener

enum class OpenCloseRequest {
    OPEN,
    CLOSE,
}

/**
 * A contract for a strategy that determines when a popup should open or close.
 *
 * Children classes shold implement this and fire [emitRequest] when they want to open or close the popup.
 */
abstract class OpenCloseStrategy {
    private val _requestFlow = MutableStateFlow(OpenCloseRequest.CLOSE)

    val requestFlow: StateFlow<OpenCloseRequest> = _requestFlow.asStateFlow()

    /**
     * Initialize this strategy with some target element.
     *
     * This is *not* the element that represents the popup itself, but rather the element that the popup is anchored to.
     */
    open fun init(targetElement: HTMLElement) = Unit

    /**
     * Release resources (if necessary) allocated by [init].
     */
    open fun dispose() = Unit

    protected fun emitRequest(request: OpenCloseRequest) {
        _requestFlow.tryEmit(request)
    }
}

/** A readable convenience property that queries the underlying state flow. */
val OpenCloseStrategy.isOpen: Boolean get() = requestFlow.value == OpenCloseRequest.OPEN

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
class IsMouseOverOpenCloseStrategy : OpenCloseStrategy() {
    private var manager: EventListenerManager? = null

    override fun init(targetElement: HTMLElement) {
        manager = EventListenerManager(targetElement).apply {
            addEventListener("mouseenter", EventListener { emitRequest(OpenCloseRequest.OPEN) })
            addEventListener("mouseleave", EventListener { emitRequest(OpenCloseRequest.CLOSE) })
        }
    }

    override fun dispose() {
        manager!!.clearAllListeners()
        manager = null
    }
}

/**
 * A strategy that opens the popup when an element gains focus and closes it when it loses focus.
 */
class HasFocusOpenCloseStrategy : OpenCloseStrategy() {
    private var manager: EventListenerManager? = null

    override fun init(targetElement: HTMLElement) {
        manager = EventListenerManager(targetElement).apply {
            addEventListener("focusin", EventListener { emitRequest(OpenCloseRequest.OPEN) })
            addEventListener("focusout", EventListener { emitRequest(OpenCloseRequest.CLOSE) })
        }
    }

    override fun dispose() {
        manager!!.clearAllListeners()
        manager = null
    }
}

/**
 * A strategy that allows the user to manually control when a popup should open or close.
 */
class ManualOpenCloseStrategy : OpenCloseStrategy() {
    var isOpen: Boolean
        get() = requestFlow.value == OpenCloseRequest.OPEN
        set(value) {
            emitRequest(if (value) OpenCloseRequest.OPEN else OpenCloseRequest.CLOSE)
        }
}

/**
 * A [StayOpenStrategy] that combines multiple orthogonal strategies into one.
 *
 * If any single strategy requests that the popup should open, then the popup will open. If any strategy requests that
 * the popup should close, then it will close. In other words, this is not a democratic strategy but rather a
 * dictatorial one.
 */
class CompositeOpenCloseStrategy(private vararg val strategies: OpenCloseStrategy) : OpenCloseStrategy() {
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

    override fun dispose() {
        strategies.forEach { it.dispose() }
    }
}
