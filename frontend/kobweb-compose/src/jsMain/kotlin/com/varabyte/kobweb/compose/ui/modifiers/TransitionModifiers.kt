package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.attributes.onTransitionCancel
import com.varabyte.kobweb.compose.attributes.onTransitionEnd
import com.varabyte.kobweb.compose.attributes.onTransitionRun
import com.varabyte.kobweb.compose.attributes.onTransitionStart
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.events.SyntheticTransitionEvent
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.transition(transition: Transition) = styleModifier {
    transition(transition)
}

fun Modifier.transition(vararg transitions: Transition.Repeatable) = styleModifier {
    transition(*transitions)
}

// Convenience method for accepting the output of Transition.group(...)
fun Modifier.transition(transitions: Array<Transition.Repeatable>) = styleModifier {
    transition(*transitions)
}

@Suppress("DEPRECATION")
fun Modifier.transition(vararg transitions: CSSTransition) = styleModifier {
    transition(*transitions)
}

// Convenience method for accepting the output of CSSTransition.group(...)
@Suppress("DEPRECATION")
fun Modifier.transition(transitions: Array<CSSTransition>) = styleModifier {
    transition(*transitions)
}

fun Modifier.onTransitionCancel(listener: (SyntheticTransitionEvent) -> Unit): Modifier = attrsModifier {
    onTransitionCancel(listener)
}

fun Modifier.onTransitionEnd(listener: (SyntheticTransitionEvent) -> Unit): Modifier = attrsModifier {
    onTransitionEnd(listener)
}

fun Modifier.onTransitionRun(listener: (SyntheticTransitionEvent) -> Unit): Modifier = attrsModifier {
    onTransitionRun(listener)
}

fun Modifier.onTransitionStart(listener: (SyntheticTransitionEvent) -> Unit): Modifier = attrsModifier {
    onTransitionStart(listener)
}
