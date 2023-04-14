@file:OptIn(ComposeWebInternalApi::class)

package com.varabyte.kobweb.compose.attributes

import com.varabyte.kobweb.compose.events.SyntheticEvent
import com.varabyte.kobweb.compose.events.SyntheticTransitionEvent
import com.varabyte.kobweb.compose.events.TransitionEventDetails
import org.jetbrains.compose.web.internal.runtime.ComposeWebInternalApi
import org.jetbrains.compose.web.internal.runtime.NamedEventListener
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget

// NOTE: This is a fork of JB's org.jetbrains.compose.web.attributes.SyntheticEventListener, which we need to fork here
// because we also forked SyntheticEvent. See that class for details.
open class SyntheticEventListener<T : SyntheticEvent<*>>(
    val event: String,
    val listener: (T) -> Unit
) : EventListener, NamedEventListener {
    override val name: String = event

    override fun handleEvent(event: Event) {
        listener(SyntheticEvent<EventTarget>(event).unsafeCast<T>())
    }
}

class TransitionEventListener(
    event: String,
    listener: (SyntheticTransitionEvent) -> Unit
) : SyntheticEventListener<SyntheticTransitionEvent>(
    event, listener
) {
    override fun handleEvent(event: Event) {
        listener(SyntheticTransitionEvent(event, event.unsafeCast<TransitionEventDetails>()))
    }
}
