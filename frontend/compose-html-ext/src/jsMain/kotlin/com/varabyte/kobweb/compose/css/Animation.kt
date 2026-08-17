package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation-composition
sealed interface AnimationComposition : StylePropertyValue {
    sealed interface Listable : AnimationComposition

    companion object : CssGlobalValues<AnimationComposition> {
        fun list(vararg compositions: Listable): AnimationComposition =
            compositions.joinToString().unsafeCast<AnimationComposition>()

        // Keyword
        val Accumulate get() = "accumulate".unsafeCast<Listable>()
        val Add get() = "add".unsafeCast<Listable>()
        val Replace get() = "replace".unsafeCast<Listable>()
    }
}

fun StyleScope.animationComposition(value: AnimationComposition) {
    property("animation-composition", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation-iteration-count
sealed interface AnimationIterationCount : StylePropertyValue {
    companion object : CssGlobalValues<AnimationIterationCount> {
        fun of(count: Number) = count.unsafeCast<AnimationIterationCount>()
        val Infinite get() = "infinite".unsafeCast<AnimationIterationCount>()
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/animation
sealed interface Animation : StylePropertyValue {
    sealed interface Listable : Animation

    companion object : CssGlobalValues<Animation> {
        // A replacement for org.jetbrains.compose.web.css.CSSAnimation -- theirs is currently implemented incorrectly
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
