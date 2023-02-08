package com.varabyte.kobweb.compose.events

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

// NOTE: This is a fork of JB's androidx.compose.web.events.SyntheticEvent, with the only change being we open access to
// the constructor. Eventually, it would be nice to remove this copy, which we can do if...
// 1) JB changes their SyntheticEvent class allowing users to construct it themselves.
// 2) JB implements all the events themselves so we don't have to do this anymore.
open class SyntheticEvent<Element : EventTarget>(
    val nativeEvent: Event
) {
    val target: Element = nativeEvent.target.unsafeCast<Element>()
    val bubbles: Boolean = nativeEvent.bubbles
    val cancelable: Boolean = nativeEvent.cancelable
    val composed: Boolean = nativeEvent.composed
    val currentTarget: EventTarget? = nativeEvent.currentTarget
    val eventPhase: Short = nativeEvent.eventPhase
    val defaultPrevented: Boolean = nativeEvent.defaultPrevented
    val timestamp: Number = nativeEvent.timeStamp
    val type: String = nativeEvent.type
    val isTrusted: Boolean = nativeEvent.isTrusted

    fun preventDefault(): Unit = nativeEvent.preventDefault()
    fun stopPropagation(): Unit = nativeEvent.stopPropagation()
    fun stopImmediatePropagation(): Unit = nativeEvent.stopImmediatePropagation()
    fun composedPath(): Array<EventTarget> = nativeEvent.composedPath()
}

class SyntheticTransitionEvent internal constructor(
    nativeEvent: Event,
    transitionEventDetails: TransitionEventDetails
) : SyntheticEvent<EventTarget>(nativeEvent) {
    val propertyName: String = transitionEventDetails.propertyName
    val elapsedTime: Number = transitionEventDetails.elapsedTime
    val pseudoElement: String = transitionEventDetails.pseudoElement
}

internal external interface TransitionEventDetails {
    val propertyName: String
    val elapsedTime: Number
    val pseudoElement: String
}
