package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
class TransitionProperty private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Custom
        fun of(customValue: String): TransitionProperty {
            check(customValue.isNotEmpty() && customValue.none { it.isWhitespace() }) {
                "Invalid transition property name. A property shouldn't contain any spaces, but got \"$customValue\"."
            }
            return TransitionProperty(customValue)
        }

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

fun StyleScope.transitionProperty(property: TransitionProperty) {
    transitionProperty(property.toString())
}

fun StyleScope.transitionProperty(vararg properties: String) {
    property("transition-property", properties.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration
/**
 * Special values for Transition Duration Property.
 */
class TransitionDuration private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global values
        val Inherit get() = TransitionDuration("inherit")
        val Initial get() = TransitionDuration("initial")
        val Revert get() = TransitionDuration("revert")
        val Unset get() = TransitionDuration("unset")
    }
}

fun StyleScope.transitionDuration(duration: TransitionDuration) {
    property("transition-duration", duration)
}

fun StyleScope.transitionDuration(vararg duration: CSSSizeValue<out CSSUnitTime>) {
    property("transition-duration", duration.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
/**
 * Special values for Transition Delay Property.
 */
class TransitionDelay private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global values
        val Inherit get() = TransitionDelay("inherit")
        val Initial get() = TransitionDelay("initial")
        val Revert get() = TransitionDelay("revert")
        val Unset get() = TransitionDelay("unset")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
fun StyleScope.transitionDelay(vararg delay: CSSSizeValue<out CSSUnitTime>) {
    property("transition-delay", delay.joinToString())
}

fun StyleScope.transitionDelay(delay: TransitionDelay) {
    property("transition-delay", delay)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
typealias TransitionTimingFunction = AnimationTimingFunction

fun StyleScope.transitionTimingFunction(vararg value: TransitionTimingFunction) {
    property("transition-timing-function", value.joinToString { it.value })
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition
data class CSSTransition(
    val property: TransitionProperty,
    val duration: CSSSizeValue<out CSSUnitTime>? = null,
    val timingFunction: TransitionTimingFunction? = null,
    val delay: CSSSizeValue<out CSSUnitTime>? = null,
) : CSSStyleValue {
    companion object {

        /**
         * A convenience method for when you want to animate multiple properties with the same values.
         *
         * Returns an array so you can use feed it into [transition] as vararg parameters using the spread operator:
         *
         * ```
         * transition(*CSSTrasition.group(listOf("width", "height"), ...))
         * ```
         */
        fun group(
            properties: Iterable<String>,
            duration: CSSSizeValue<out CSSUnitTime>? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSSizeValue<out CSSUnitTime>? = null
        ) = properties.map { property -> CSSTransition(property, duration, timingFunction, delay) }.toTypedArray()

        /**
         * A convenience method for when you want to animate multiple properties with the same values.
         *
         * Returns an array so you can use feed it into [transition] as vararg parameters using the spread operator:
         *
         * ```
         * transition(*CSSTrasition.group(listOf("width", "height"), ...))
         * ```
         */
        fun group(
            properties: Iterable<TransitionProperty>,
            duration: CSSSizeValue<out CSSUnitTime>? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSSizeValue<out CSSUnitTime>? = null
        ) = properties.map { property -> CSSTransition(property, duration, timingFunction, delay) }.toTypedArray()
    }

    constructor(
        property: String, duration: CSSSizeValue<out CSSUnitTime>? = null,
        timingFunction: TransitionTimingFunction? = null,
        delay: CSSSizeValue<out CSSUnitTime>? = null
    ) : this(TransitionProperty.of(property), duration, timingFunction, delay)

    override fun toString() = buildList {
        add(property.toString())
        // https://developer.mozilla.org/en-US/docs/Web/CSS/animation#syntax
        duration?.let { add(it.toString()) }
        timingFunction?.let { add(it.toString()) }
        if (delay != null) {
            if (duration == null) {
                add("0s") // Needed so parser knows that the next time string is for "delay"
            }
            add(delay.toString())
        }
    }.joinToString(" ")
}

fun StyleScope.transition(vararg transitions: CSSTransition) {
    if (transitions.isNotEmpty()) {
        property("transition", transitions.joinToString())
    }
}
