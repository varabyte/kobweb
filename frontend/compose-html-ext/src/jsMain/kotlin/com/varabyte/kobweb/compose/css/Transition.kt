package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-behavior
class TransitionBehavior private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TransitionBehavior> {
        // Keywords
        val AllowDiscrete get() = TransitionBehavior("allow-discrete")
        val Normal get() = TransitionBehavior("normal")
    }
}

fun StyleScope.transitionBehavior(behavior: TransitionBehavior) {
    property("transition-behavior", behavior)
}

fun StyleScope.transitionBehavior(vararg behaviors: TransitionBehavior) {
    property("transition-behavior", behaviors.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
sealed class TransitionProperty private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : TransitionProperty(value)
    class Name internal constructor(value: String) : TransitionProperty(value)

    companion object: CssGlobalValues<Keyword> {
        // Custom
        fun of(customValue: String): Name {
            require(customValue.isNotEmpty() && customValue.none { it.isWhitespace() }) {
                "Invalid transition property name. A property shouldn't contain any spaces, but got \"$customValue\"."
            }
            return Name(customValue)
        }

        // Keywords
        val None: TransitionProperty get() = Keyword("none")
        val All get() = Name("all") // Essentially a special property name
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

    companion object: CssGlobalValues<TransitionDuration>
}

fun StyleScope.transitionDuration(duration: TransitionDuration) {
    property("transition-duration", duration)
}

fun StyleScope.transitionDuration(vararg durations: CSSTimeNumericValue) {
    property("transition-duration", durations.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
/**
 * Special values for Transition Delay Property.
 */
class TransitionDelay private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<TransitionDelay>
}

fun StyleScope.transitionDelay(delay: TransitionDelay) {
    property("transition-delay", delay)
}

fun StyleScope.transitionDelay(vararg delays: CSSTimeNumericValue) {
    property("transition-delay", delays.joinToString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
typealias TransitionTimingFunction = AnimationTimingFunction

fun StyleScope.transitionTimingFunction(value: TransitionTimingFunction) {
    property("transition-timing-function", value)
}

fun StyleScope.transitionTimingFunction(vararg values: TransitionTimingFunction) {
    property("transition-timing-function", values.joinToString { it.value })
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition
sealed class Transition private constructor(private val value: String) : StylePropertyValue {
    override fun toString(): String = value

    private class Keyword(value: String) : Transition(value)

    class Repeatable internal constructor(
        property: TransitionProperty.Name,
        duration: CSSTimeNumericValue?,
        timingFunction: TransitionTimingFunction?,
        delay: CSSTimeNumericValue?,
        behavior: TransitionBehavior?,
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
            behavior?.let { add(it.toString()) }
        }.joinToString(" ")
    )

    companion object: CssGlobalValues<Keyword> {
        // Keyword
        val None: Transition get() = Keyword("none")

        fun of(
            property: TransitionProperty.Name,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ): Repeatable = Repeatable(property, duration, timingFunction, delay, behavior)

        fun of(
            property: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ): Repeatable = Repeatable(TransitionProperty.of(property), duration, timingFunction, delay, behavior)

        /**
         * Specify transition details that should apply to every animatable property on this element.
         */
        fun all(
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ): Repeatable = of(TransitionProperty.All, duration, timingFunction, delay, behavior)

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
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ) = properties.map { property -> Transition.of(property, duration, timingFunction, delay, behavior) }
            .toTypedArray()

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
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ) = properties.map { property -> Transition.of(property, duration, timingFunction, delay, behavior) }
            .toTypedArray()
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
