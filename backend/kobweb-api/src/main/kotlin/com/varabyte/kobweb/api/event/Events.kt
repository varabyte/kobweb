package com.varabyte.kobweb.api.event

import com.varabyte.kobweb.api.dispose.DisposeApiContext

interface Events {
    fun onDispose(callback: (ctx: DisposeApiContext) -> Unit)
}

class EventsImpl : Events {

    private val disposeCallbacks = mutableListOf<(ctx: DisposeApiContext) -> Unit>()

    override fun onDispose(callback: (ctx: DisposeApiContext) -> Unit) {
        disposeCallbacks.add(callback)
    }

    internal fun dispose(ctx: DisposeApiContext) {
        disposeCallbacks.forEach { handle -> handle(ctx) }
    }

}