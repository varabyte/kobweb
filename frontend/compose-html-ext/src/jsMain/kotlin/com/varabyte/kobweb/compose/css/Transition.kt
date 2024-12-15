package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
sealed class TransitionProperty private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : TransitionProperty(value)
    class Name(value: String) : TransitionProperty(value)

    companion object {
        // Custom
        fun of(customValue: String): Name {
            check(customValue.isNotEmpty() && customValue.none { it.isWhitespace() }) {
                "Invalid transition property name. A property shouldn't contain any spaces, but got \"$customValue\"."
            }
            return Name(customValue)
        }

        // Keywords
        val None: TransitionProperty get() = Keyword("none")
        val All get() = Name("all") // Essentially a special property name

        // Global values
        val Inherit: TransitionProperty get() = Keyword("inherit")
        val Initial: TransitionProperty get() = Keyword("initial")
        val Revert: TransitionProperty get() = Keyword("revert")
        val Unset: TransitionProperty get() = Keyword("unset")
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
class TransitionDuration private constructor(private val value: String) : StylePropertyValue {
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

fun StyleScope.transitionDuration(vararg duration: CSSTimeNumericValue) {
    property("transition-duration", duration.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
/**
 * Special values for Transition Delay Property.
 */
class TransitionDelay private constructor(private val value: String) : StylePropertyValue {
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
fun StyleScope.transitionDelay(vararg delay: CSSTimeNumericValue) {
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
sealed class Transition private constructor(private val value: String) : StylePropertyValue {
    override fun toString(): String = value

    private class Keyword(value: String) : Transition(value)

    class Repeatable internal constructor(
        property: TransitionProperty,
        duration: CSSTimeNumericValue?,
        timingFunction: TransitionTimingFunction?,
        delay: CSSTimeNumericValue?,
    ) : Transition(
        buildList {
            add(property.toString())
            // https://developer.mozilla.org/en-US/docs/Web/CSS/transition#syntax
            duration?.let { add(it.toString()) }
            timingFunction?.let { add(it.toString()) }
            if (delay != null) {
                if (duration == null) {
                    add("0s") // Needed so parser knows that the next time string is for "delay"
                }
                add(delay.toString())
            }
        }.joinToString(" ")
    )

    companion object {
        // Keyword
        val None: Transition get() = Keyword("none")

        // Global Keywords
        val Inherit: Transition get() = Keyword("inherit")
        val Initial: Transition get() = Keyword("initial")
        val Revert: Transition get() = Keyword("revert")
        val Unset: Transition get() = Keyword("unset")

        fun of(
            property: TransitionProperty.Name,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
        ): Repeatable = Repeatable(property, duration, timingFunction, delay)

        fun of(
            property: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
        ): Repeatable = Repeatable(TransitionProperty.of(property), duration, timingFunction, delay)

        /**
         * Specify transition details that should apply to every animatable property on this element.
         */
        fun all(
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
        ): Repeatable = of(TransitionProperty.All, duration, timingFunction, delay)

        /**
         * A convenience method for when you want to animate multiple properties with the same values.
         *
         * Returns an array, so you can use feed it into [transition] as vararg parameters using the spread operator:
         *
         * ```
         * transition(Transition.group(listOf("width", ...), ...))
         * ```
         */
        @Suppress("RemoveRedundantQualifierName") // Transition.of reads nicer
        fun group(
            properties: Iterable<String>,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null
        ) = properties.map { property -> Transition.of(property, duration, timingFunction, delay) }.toTypedArray()

        /**
         * A convenience method for when you want to animate multiple properties with the same values.
         *
         * Returns an array, so you can use feed it into [transition] as vararg parameters using the spread operator:
         *
         * ```
         * transition(Transition.group(listOf(TransitionProperty.of("width"), ...), ...))
         * ```
         */
        @Suppress("RemoveRedundantQualifierName") // Transition.of reads nicer
        fun group(
            properties: Iterable<TransitionProperty.Name>,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null
        ) = properties.map { property -> Transition.of(property, duration, timingFunction, delay) }.toTypedArray()
    }
}

fun StyleScope.transition(transition: Transition) {
    property("transition", transition)
}

fun StyleScope.transition(vararg transitions: Transition.Repeatable) {
    if (transitions.isNotEmpty()) {
        property("transition", transitions.joinToString())
    }
}
