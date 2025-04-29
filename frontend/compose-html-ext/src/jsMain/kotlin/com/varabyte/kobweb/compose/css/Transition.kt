// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-behavior
class TransitionBehavior private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        fun list(vararg behaviors: TransitionBehavior) = TransitionBehavior(behaviors.joinToString())

        // Keywords
        val AllowDiscrete get() = TransitionBehavior("allow-discrete")
        val Normal get() = TransitionBehavior("normal")

        // Global values
        val Inherit get() = TransitionBehavior("inherit")
        val Initial get() = TransitionBehavior("initial")
        val Revert get() = TransitionBehavior("revert")
        val Unset get() = TransitionBehavior("unset")
    }
}

fun StyleScope.transitionBehavior(behavior: TransitionBehavior) {
    property("transition-behavior", behavior)
}

@Deprecated("Use transitionBehavior(TransitionBehavior.list(...)) instead.", ReplaceWith("transitionBehavior(TransitionBehavior.list(*behaviors))"))
fun StyleScope.transitionBehavior(vararg behaviors: TransitionBehavior) {
    transitionBehavior(TransitionBehavior.list(*behaviors))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
sealed class TransitionProperty private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    private class Keyword(value: String) : TransitionProperty(value)
    class Name internal constructor(value: String) : TransitionProperty(value)

    companion object {
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

fun StyleScope.transitionProperty(vararg properties: TransitionProperty.Name) {
    if (properties.isNotEmpty()) {
        property("transition-property", properties.joinToString())
    }
}

fun StyleScope.transitionProperty(vararg properties: String) {
    transitionProperty(*properties.map { TransitionProperty.of(it) }.toTypedArray())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration
fun StyleScope.transitionDuration(vararg durations: CSSTimeNumericValue) {
    if (durations.isNotEmpty()) {
        property("transition-duration", durations.joinToString())
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
fun StyleScope.transitionDelay(vararg delays: CSSTimeNumericValue) {
    if (delays.isNotEmpty()) {
        property("transition-delay", delays.joinToString())
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
typealias TransitionTimingFunction = AnimationTimingFunction

fun StyleScope.transitionTimingFunction(timingFunction: TransitionTimingFunction) {
    property("transition-timing-function", timingFunction)
}

fun StyleScope.transitionTimingFunction(vararg timingFunctions: TransitionTimingFunction) {
    if (timingFunctions.isNotEmpty()) {
        property("transition-timing-function", timingFunctions.joinToString { it.value })
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition
sealed class Transition private constructor(private val value: String) : StylePropertyValue {
    override fun toString(): String = value

    private class Keyword(value: String) : Transition(value)
    private class ValueList(values: List<Repeatable>) : Transition(values.joinToString())

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
            behavior: TransitionBehavior? = null,
        ): Repeatable = Repeatable(property, duration, timingFunction, delay, behavior)

        fun of(
            property: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ): Repeatable = Repeatable(TransitionProperty.of(property), duration, timingFunction, delay, behavior)

        fun list(vararg transitions: Repeatable): Transition = ValueList(transitions.toList())

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
        ): Transition = ValueList(properties.map { property -> Transition.of(property, duration, timingFunction, delay, behavior) })

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
        ): Transition = ValueList(properties.map { property -> Transition.of(property, duration, timingFunction, delay, behavior) })
    }
}

fun StyleScope.transition(transition: Transition) {
    property("transition", transition)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.transition(transition: Transition.Repeatable) {
    transition(transition as Transition)
}
// Remove the previous method too after removing this method
@Deprecated("Use transition(Transition.list(...)) instead.", ReplaceWith("transition(Transition.list(*transitions))"))
fun StyleScope.transition(vararg transitions: Transition.Repeatable) {
    transition(Transition.list(*transitions))
}
