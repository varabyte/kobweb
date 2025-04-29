// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

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

    private class ValueList(values: List<Listable>) : Animation(values.joinToString())

    // A replacement for org.jetbrains.compose.web.css.CSSAnimation which is currently implemented incorrectly
    // (it exposes a 1:many relationship between an animation's name and its properties, but
    // it should be 1:1).
    class Listable internal constructor(
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
            // JB enum value for "paused" is uppercased (probably copy/paste error)
            playState?.let { add(it.toString().lowercase()) }

            add(name)
        }.joinToString(" ")
    )

    companion object : CssGlobalValues<Animation> {
        fun of(
            name: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: AnimationTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            iterationCount: AnimationIterationCount? = null,
            direction: AnimationDirection? = null,
            fillMode: AnimationFillMode? = null,
            playState: AnimationPlayState? = null
        ): Listable =
            Listable(name, duration, timingFunction, delay, iterationCount, direction, fillMode, playState)

        fun list(vararg animations: Listable): Animation = ValueList(animations.toList())

        // Keyword
        val None: Animation get() = Keyword("none")
    }
}

fun StyleScope.animation(animation: Animation) {
    property("animation", animation)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.animation(animation: Animation.Listable) {
    animation(animation as Animation)
}
// Remove the previous method too after removing this method
@Deprecated("Use `animation(Animation.list(...))` instead.", ReplaceWith("animation(Animation.list(*animations))"))
fun StyleScope.animation(vararg animations: Animation.Listable) {
    animation(Animation.list(*animations))
}
