package com.varabyte.kobweb.api.event

import com.varabyte.kobweb.api.dispose.DisposeEvent

interface Events {
    fun onDispose(callback: (event: DisposeEvent) -> Unit)
}

class EventDispatcher : Events {

    private val disposeCallbacks = mutableListOf<(event: DisposeEvent) -> Unit>()

    override fun onDispose(callback: (event: DisposeEvent) -> Unit) {
        disposeCallbacks.add(callback)
    }

    fun dispose(event: DisposeEvent) {
        disposeCallbacks.forEach { handle -> handle(event) }
    }

    fun reset() {
        disposeCallbacks.clear()
    }

}