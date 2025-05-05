package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

sealed interface AnimationIterationCount : StylePropertyValue {
    companion object {
        fun of(count: Number) = count.toString().unsafeCast<AnimationIterationCount>()
        val Infinite get() = "infinite".unsafeCast<AnimationIterationCount>()
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation
sealed interface Animation : StylePropertyValue {
    sealed interface Listable : Animation

    companion object : CssGlobalValues<Animation> {
        // A replacement for org.jetbrains.compose.web.css.CSSAnimation which is currently implemented incorrectly
        // (it exposes a 1:many relationship between an animation's name and its properties, but
        // it should be 1:1).
        fun of(
            name: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: AnimationTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            iterationCount: AnimationIterationCount? = null,
            direction: AnimationDirection? = null,
            fillMode: AnimationFillMode? = null,
            playState: AnimationPlayState? = null
        ) = buildList {
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
        }.joinToString(" ").unsafeCast<Listable>()

        fun list(vararg animations: Listable) = animations.joinToString().unsafeCast<Animation>()

        // Keyword
        val None: Animation get() = "none".unsafeCast<Animation>()
    }
}

fun StyleScope.animation(animation: Animation) {
    property("animation", animation)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.animation(animation: Animation.Listable)
{
    // Don't cast with "as", that breaks due to our internal unsafeCasting approach
    val animation: Animation = animation
    animation(animation)
}
// Remove the previous method too after removing this method
@Deprecated("Use `animation(Animation.list(...))` instead.", ReplaceWith("animation(Animation.list(*animations))"))
fun StyleScope.animation(vararg animations: Animation.Listable) {
    animation(Animation.list(*animations))
}
