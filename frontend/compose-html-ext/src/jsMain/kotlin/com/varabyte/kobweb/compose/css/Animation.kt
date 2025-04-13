package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
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

    // A replacement for org.jetbrains.compose.web.css.CSSAnimation which is currently implemented incorrectly
    // (it exposes a 1:many relationship between an animation's name and its properties, but
    // it should be 1:1).
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

    companion object: CssGlobalValues<Keyword> {
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
        val None: Animation get() = Keyword("none")
    }
}

fun StyleScope.animation(animation: Animation) {
    property("animation", animation)
}

fun StyleScope.animation(vararg animations: Animation.Repeatable) {
    if (animations.isNotEmpty()) {
        property("animation", animations.joinToString(", "))
    }
}
