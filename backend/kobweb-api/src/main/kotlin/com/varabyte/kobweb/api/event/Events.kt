package com.varabyte.kobweb.api.event

import com.varabyte.kobweb.api.event.dispose.DisposeEvent

/**
 * Register custom events handlers that will be called during the lifetime of the server.
 */
interface Events {
    /**
     * Registers a handler for resource cleanup when server is shutting down.
     *
     * This callback is intended for the explicit release of unmanaged resources that the server might
     * have initialized. Invoked during the server's lifecycle termination and when the dev server is live reloaded.
     * For example, this could be used to close a database connection.
     *
     * ### Limitations
     * Not guaranteed to be invoked (e.g., abrupt termination, power loss).
     * In other words, even though this event is provided, you have to assume there's a chance it won't get called,
     * and you must write your server logic in a way that can recover from these events.
     *
     * ### Use Cases
     * - Useful in development for handling resource leaks during frequent server restarts due to live reloading.
     * - Releasing locks on resources that would prevent the server from reacquiring them after the live reloading/restart.
     *
     * @param callback Function to execute for resource disposal, receiving a [DisposeEvent] as parameter.
     */
    fun onDispose(callback: (event: DisposeEvent) -> Unit)
}


/**
 * Primary implementation of the [Events] interface. Keeps track of the registered event handlers and allows to dispatch
 * the events
 */
class EventDispatcher : Events {

    private val disposeCallbacks = mutableListOf<(event: DisposeEvent) -> Unit>()

    override fun onDispose(callback: (event: DisposeEvent) -> Unit) {
        disposeCallbacks.add(callback)
    }

    fun dispose(event: DisposeEvent) {
        disposeCallbacks.forEach { handle -> handle(event) }
    }

    /**
     * Removes all registered event handlers. Needed when the server live reloads the api.
     */
    fun reset() {
        disposeCallbacks.clear()
    }

}