package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

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

fun StyleScope.transitionProperty(property: TransitionProperty) {
    transitionProperty(property.value)
}

fun StyleScope.transitionProperty(vararg properties: String) {
    property("transition-property", properties.joinToString())
}

fun StyleScope.transitionDuration(duration: TransitionDuration) {
    property("transition-duration", duration.value)
}

fun StyleScope.transitionDuration(vararg duration: CSSSizeValue<out CSSUnitTime>) {
    property("transition-duration", duration.joinToString())
}

fun StyleScope.transitionDelay(vararg delay: CSSSizeValue<out CSSUnitTime>) {
    property("transition-delay", delay.joinToString())
}

fun StyleScope.transitionDelay(delay: TransitionDelay) {
    property("transition-delay", delay.value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
fun StyleScope.transitionTimingFunction(vararg value: AnimationTimingFunction) {
    property("transition-timing-function", value.joinToString() { it.value })
}