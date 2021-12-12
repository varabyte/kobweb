package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitTime
import org.jetbrains.compose.web.css.StyleBuilder

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
/**
 * Special values for Transition Property. You can also pass in string values directly using
 * the relaxed version of [transitionProperty]
 */
class TransitionProperty(val value: String) {
    companion object {
        // Keywords
        val None get() = TransitionProperty("none")
        val All get() = TransitionProperty("all")

        // Global values
        val Inherit get() = TransitionProperty("inherit")
        val Initial get() = TransitionProperty("initial")
        val Revert get() = TransitionProperty("revert")
        val Unset get() = TransitionProperty("unset")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration
/**
 * Special values for Transition Duration Property.
 */
class TransitionDuration(val value: String) {
    companion object {
        // Global values
        val Inherit get() = TransitionDuration("inherit")
        val Initial get() = TransitionDuration("initial")
        val Revert get() = TransitionDuration("revert")
        val Unset get() = TransitionDuration("unset")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
/**
 * Special values for Transition Delay Property.
 */
class TransitionDelay(val value: String) {
    companion object {
        // Global values
        val Inherit get() = TransitionDelay("inherit")
        val Initial get() = TransitionDelay("initial")
        val Revert get() = TransitionDelay("revert")
        val Unset get() = TransitionDelay("unset")
    }
}

fun StyleBuilder.transitionProperty(property: TransitionProperty) {
    transitionProperty(property.value)
}

fun StyleBuilder.transitionProperty(vararg properties: String) {
    property("transition-property", properties.joinToString())
}

fun StyleBuilder.transitionDuration(duration: TransitionDuration) {
    property("transition-duration", duration.value)
}

fun StyleBuilder.transitionDuration(duration: CSSSizeValue<out CSSUnitTime>) {
    property("transition-duration", duration.toString())
}

fun StyleBuilder.transitionDelay(delay: CSSSizeValue<out CSSUnitTime>) {
    property("transition-delay", delay.toString())
}

fun StyleBuilder.transitionDelay(delay: TransitionDelay) {
    property("transition-delay", delay.value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
fun StyleBuilder.transitionTimingFunction(value: AnimationTimingFunction) {
    property("transition-timing-function", value.toString())
}