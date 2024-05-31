@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

value class AnimationIterationCount private constructor(private val count: Number?) : StylePropertyValue {
    companion object {
        fun of(count: Number) = AnimationIterationCount(count)
        val Infinite get() = AnimationIterationCount(null)
    }

    override fun toString() = count?.toString() ?: "infinite"
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation
sealed class Animation private constructor(private val value: String) : StylePropertyValue {
    override fun toString(): String = value

    private class Keyword(value: String) : Animation(value)

    class Repeatable internal constructor(
        name: String,
        duration: CSSTimeNumericValue?,
        timingFunction: AnimationTimingFunction?,
        delay: CSSTimeNumericValue?,
        iterationCount: AnimationIterationCount?,
        direction: AnimationDirection?,
        fillMode: AnimationFillMode?,
        playState: AnimationPlayState?
    ) : Animation(
        buildList {
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
    )

    companion object {
        fun of(
            name: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: AnimationTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            iterationCount: AnimationIterationCount? = null,
            direction: AnimationDirection? = null,
            fillMode: AnimationFillMode? = null,
            playState: AnimationPlayState? = null
        ): Repeatable =
            Repeatable(name, duration, timingFunction, delay, iterationCount, direction, fillMode, playState)

        // Keyword
        val None: Animation = Keyword("none")

        // Global Keywords
        val Inherit: Animation = Keyword("inherit")
        val Initial: Animation = Keyword("initial")
        val Revert: Animation = Keyword("revert")
        val Unset: Animation = Keyword("unset")
    }
}


// A replacement for the upstream CSSAnimation which is currently implemented incorrectly
// (it exposes a 1:many relationship between an animation's name and its properties, but
// it should be 1:1).
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation
@Deprecated(
    "Please use `Animation.of` instead.",
    ReplaceWith("Animation.of(name, duration, timingFunction, delay, iterationCount, direction, fillMode, playState)")
)
data class CSSAnimation(
    val name: String,
    val duration: CSSTimeNumericValue? = null,
    val timingFunction: AnimationTimingFunction? = null,
    val delay: CSSTimeNumericValue? = null,
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

fun StyleScope.animation(animation: Animation) {
    property("animation", animation)
}

fun StyleScope.animation(vararg animations: Animation.Repeatable) {
    if (animations.isNotEmpty()) {
        property("animation", animations.joinToString(", "))
    }
}

fun StyleScope.animation(vararg animations: CSSAnimation) {
    if (animations.isNotEmpty()) {
        property("animation", animations.joinToString(", "))
    }
}
