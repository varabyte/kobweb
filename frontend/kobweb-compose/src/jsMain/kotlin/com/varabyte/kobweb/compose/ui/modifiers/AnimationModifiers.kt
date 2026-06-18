package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.AnimationDirection
import org.jetbrains.compose.web.css.AnimationFillMode
import org.jetbrains.compose.web.css.AnimationPlayState
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.events.SyntheticAnimationEvent

class AnimationScope internal constructor(private val styleScope: StyleScope) {
    fun delay(vararg delays: CSSTimeNumericValue) = styleScope.property("animation-delay", delays.joinToString())
    fun direction(vararg directions: AnimationDirection) = styleScope.property("animation-direction", directions.joinToString())
    fun duration(vararg durations: CSSTimeNumericValue) = styleScope.property("animation-duration", durations.joinToString())
    fun fillMode(vararg fillModes: AnimationFillMode) = styleScope.property("animation-fill-mode", fillModes.joinToString())
    fun iterationCount(vararg iterationCounts: AnimationIterationCount) = styleScope.property("animation-iteration-count", iterationCounts.joinToString())
    fun iterationCount(vararg iterationCounts: Number) = iterationCount(*iterationCounts.map { AnimationIterationCount.of(it) }.toTypedArray())
    fun name(vararg names: String) = styleScope.property("animation-name", names.joinToString())
    fun playState(vararg playStates: AnimationPlayState) = styleScope.property("animation-play-state", playStates.joinToString())
    fun timingFunction(vararg timingFunctions: AnimationTimingFunction) = styleScope.property("animation-timing-function", timingFunctions.joinToString())
}

fun Modifier.animation(animation: Animation) = styleModifier {
    animation(animation)
}

fun Modifier.animation(vararg animations: Animation.Listable) = styleModifier {
    animation(Animation.list(*animations))
}

fun Modifier.animation(animations: List<Animation.Listable>) = animation(*animations.toTypedArray())

fun Modifier.animation(scope: AnimationScope.() -> Unit) = styleModifier {
    AnimationScope(this).apply(scope)
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
