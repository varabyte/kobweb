package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// A replacement for the upstream CSSAnimation which is currently implemented incorrectly
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation
data class CSSAnimation(
    val name: String,
    val duration: CSSSizeValue<out CSSUnitTime>? = null,
    val timingFunction: AnimationTimingFunction? = null,
    val delay: CSSSizeValue<out CSSUnitTime>? = null,
    val iterationCount: IterationCount? = null,
    val direction: AnimationDirection? = null,
    val fillMode: AnimationFillMode? = null,
    val playState: AnimationPlayState? = null
) : CSSStyleValue {
    value class IterationCount private constructor(private val count: Number?) {
        companion object {
            fun of(count: Number) = IterationCount(count)
            val Infinite = IterationCount(null)
        }

        override fun toString() = count?.toString() ?: "infinite"
    }

    override fun toString() = buildList {
        // https://developer.mozilla.org/en-US/docs/Web/CSS/animation#syntax
        duration?.let { add(it.toString()) }
        timingFunction?.let { add(it.toString()) }
        if (delay != null && duration == null) {
            add("0s") // Needed so parser knows that the next time string is for "delay"
            add(delay.toString())
        }
        iterationCount?.let { add(it.toString()) }
        direction?.let { add(it.toString()) }
        fillMode?.let { add(it.toString()) }
        playState?.let { add(it.toString()) }

        add(name)
    }.joinToString(" ")
}

fun StyleScope.animation(vararg animations: CSSAnimation) {
    if (animations.isNotEmpty()) {
        property("animation", animations.joinToString(", "))
    }
}