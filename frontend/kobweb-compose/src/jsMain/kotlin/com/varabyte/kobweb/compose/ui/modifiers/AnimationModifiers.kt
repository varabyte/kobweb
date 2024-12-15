package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.events.SyntheticAnimationEvent

fun Modifier.animation(animation: Animation) = styleModifier {
    animation(animation)
}

fun Modifier.animation(vararg animations: Animation.Repeatable) = styleModifier {
    animation(*animations)
}

fun Modifier.onAnimationEnd(listener: (SyntheticAnimationEvent) -> Unit): Modifier = attrsModifier {
    onAnimationEnd(listener)
}

fun Modifier.onAnimationIteration(listener: (SyntheticAnimationEvent) -> Unit): Modifier = attrsModifier {
    onAnimationIteration(listener)
}

fun Modifier.onAnimationStart(listener: (SyntheticAnimationEvent) -> Unit): Modifier = attrsModifier {
    onAnimationStart(listener)
}
