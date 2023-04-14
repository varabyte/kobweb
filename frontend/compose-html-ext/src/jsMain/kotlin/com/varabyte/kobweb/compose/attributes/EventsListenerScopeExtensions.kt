@file:OptIn(ComposeWebInternalApi::class)

package com.varabyte.kobweb.compose.attributes

import com.varabyte.kobweb.compose.events.SyntheticTransitionEvent
import org.jetbrains.compose.web.attributes.EventsListenerScope
import org.jetbrains.compose.web.attributes.SyntheticEventListener
import org.jetbrains.compose.web.internal.runtime.ComposeWebInternalApi

// https://developer.mozilla.org/en-US/docs/Web/API/Element/transitioncancel_event
fun EventsListenerScope.onTransitionCancel(listener: (SyntheticTransitionEvent) -> Unit) {
    val evtListener = TransitionEventListener("transitioncancel", listener)
    registerEventListener(evtListener.unsafeCast<SyntheticEventListener<*>>())
}

// https://developer.mozilla.org/en-US/docs/Web/API/Element/transitionend_event
fun EventsListenerScope.onTransitionEnd(listener: (SyntheticTransitionEvent) -> Unit) {
    val evtListener = TransitionEventListener("transitionend", listener)
    registerEventListener(evtListener.unsafeCast<SyntheticEventListener<*>>())
}

// https://developer.mozilla.org/en-US/docs/Web/API/Element/transitionrun_event
fun EventsListenerScope.onTransitionRun(listener: (SyntheticTransitionEvent) -> Unit) {
    val evtListener = TransitionEventListener("transitionrun", listener)
    registerEventListener(evtListener.unsafeCast<SyntheticEventListener<*>>())
}

// https://developer.mozilla.org/en-US/docs/Web/API/Element/transitionstart_event
fun EventsListenerScope.onTransitionStart(listener: (SyntheticTransitionEvent) -> Unit) {
    val evtListener = TransitionEventListener("transitionstart", listener)
    registerEventListener(evtListener.unsafeCast<SyntheticEventListener<*>>())
}

