package com.varabyte.kobweb.browser.events

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

/**
 * A simple manager class for handling listeners added to a target element.
 *
 * This class is useful for when you want to add listeners to an element and then remove them all later, say when some
 * wrapping class gets disposed.
 */
class EventListenerManager(private val element: HTMLElement) {
    private val listeners = mutableMapOf<String, EventListener>()

    fun addEventListener(type: String, listener: EventListener) {
        listeners[type] = listener
        element.addEventListener(type, listener)
    }

    fun addEventListener(type: String, listener: (Event) -> Unit) {
        addEventListener(type, EventListener(listener))
    }

    fun clearAllListeners() {
        listeners.forEach { (type, listener) ->
            element.removeEventListener(type, listener)
        }
        listeners.clear()
    }
}
