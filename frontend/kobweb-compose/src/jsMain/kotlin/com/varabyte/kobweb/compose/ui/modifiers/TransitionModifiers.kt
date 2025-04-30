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
import org.jetbrains.compose.web.css.StyleScope

fun Modifier.transition(transition: Transition) = styleModifier {
    transition(transition)
}

fun Modifier.transition(vararg transitions: Transition.Listable) = styleModifier {
    transition(*transitions)
}

fun Modifier.transition(transitions: List<Transition.Listable>) = styleModifier {
    transition(*transitions.toTypedArray())
}

// Convenience method for accepting the output of Transition.group(...)
fun Modifier.transition(transitions: Array<Transition.Listable>) = styleModifier {
    transition(*transitions)
}

class TransitionScope(private val styleScope: StyleScope) {
    fun property(vararg properties: TransitionProperty.Name) = styleScope.transitionProperty(*properties)
    fun property(vararg properties: String) = styleScope.transitionProperty(*properties)
    fun duration(vararg durations: CSSTimeNumericValue) = styleScope.transitionDuration(*durations)
    fun timingFunction(vararg timingFunctions: TransitionTimingFunction) = styleScope.transitionTimingFunction(*timingFunctions)
    fun delay(vararg delays: CSSTimeNumericValue) = styleScope.transitionDelay(*delays)
    fun behavior(vararg behaviors: TransitionBehavior.Listable) = styleScope.transitionBehavior(*behaviors)

    fun property(properties: List<TransitionProperty.Name>) = property(*properties.toTypedArray())
    fun property(properties: List<String>) = property(*properties.toTypedArray())
    fun duration(durations: List<CSSTimeNumericValue>) = duration(*durations.toTypedArray())
    fun timingFunction(timingFunctions: List<TransitionTimingFunction>) = timingFunction(*timingFunctions.toTypedArray())
    fun delay(delays: List<CSSTimeNumericValue>) = delay(*delays.toTypedArray())
    fun behavior(behaviors: List<TransitionBehavior.Listable>) = behavior(*behaviors.toTypedArray())
}

fun Modifier.transition(scope: TransitionScope.() -> Unit) = styleModifier {
    TransitionScope(this).apply(scope)
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
