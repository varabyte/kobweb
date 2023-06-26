@file:Suppress("DeprecatedCallableAddReplaceWith")

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
import org.jetbrains.compose.web.css.*

// TODO(#168): Remove in v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"transition\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    ),
)
fun Modifier.transition(value: String) = styleModifier {
    property("transition", value)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionProperty(vararg properties: String) = styleModifier {
    transitionProperty(*properties)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionProperty(property: TransitionProperty) = styleModifier {
    transitionProperty(property)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionDuration(vararg durations: CSSSizeValue<out CSSUnitTime>) = styleModifier {
    transitionDuration(*durations)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionDuration(duration: TransitionDuration) = styleModifier {
    transitionDuration(duration)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionDelay(vararg delays: CSSSizeValue<out CSSUnitTime>) = styleModifier {
    transitionDelay(*delays)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionDelay(delay: TransitionDelay) = styleModifier {
    transitionDelay(delay)
}

// TODO(#168): Remove in v1.0
@Deprecated("We will be simplifying Modifier APIs in v1.0. Use Modifier.transition(...) instead.")
fun Modifier.transitionTimingFunction(vararg timingFunctions: AnimationTimingFunction) = styleModifier {
    transitionTimingFunction(*timingFunctions)
}

fun Modifier.transition(vararg transitions: CSSTransition) = styleModifier {
    transition(*transitions)
}

// Convenience method for accepting the output of CSSTransition.group(...)
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
