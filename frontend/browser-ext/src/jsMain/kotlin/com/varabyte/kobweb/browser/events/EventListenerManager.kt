package com.varabyte.kobweb.browser.events

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget

/**
 * A simple manager class for handling listeners added to an event target.
 *
 * This class is useful for when you want to add listeners to an element and then remove them all later, say when some
 * wrapping class gets disposed.
 */
class EventListenerManager(private val target: EventTarget) {
    private val listeners = mutableMapOf<String, EventListener>()

    fun addEventListener(type: String, listener: EventListener) {
        listeners[type] = listener
        target.addEventListener(type, listener)
    }

    fun addEventListener(type: String, listener: (Event) -> Unit) {
        addEventListener(type, EventListener(listener))
    }

    fun clearAllListeners() {
        listeners.forEach { (type, listener) ->
            target.removeEventListener(type, listener)
        }
        listeners.clear()
    }
}
