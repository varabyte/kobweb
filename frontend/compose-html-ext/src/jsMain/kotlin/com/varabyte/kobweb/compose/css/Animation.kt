package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

value class AnimationIterationCount private constructor(private val count: Number?) : StylePropertyValue {
    companion object {
        fun of(count: Number) = AnimationIterationCount(count)
        val Infinite = AnimationIterationCount(null)
    }

    override fun toString() = count?.toString() ?: "infinite"
}

// A replacement for the upstream CSSAnimation which is currently implemented incorrectly
// (it exposes a 1:many relationship between an animation's name and its properties, but
// it should be 1:1).
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation
data class CSSAnimation(
    val name: String,
    val duration: CSSSizeValue<out CSSUnitTime>? = null,
    val timingFunction: AnimationTimingFunction? = null,
    val delay: CSSSizeValue<out CSSUnitTime>? = null,
    val iterationCount: AnimationIterationCount? = null,
    val direction: AnimationDirection? = null,
    val fillMode: AnimationFillMode? = null,
    val playState: AnimationPlayState? = null
) : CSSStyleValue {
    override fun toString() = buildList {
        // https://developer.mozilla.org/en-US/docs/Web/CSS/animation#syntax
        duration?.let { add(it.toString()) }
        timingFunction?.let { add(it.toString()) }
        if (delay != null) {
            if (duration == null) {
                add("0s") // Needed so parser knows that the next time string is for "delay"
            }
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
