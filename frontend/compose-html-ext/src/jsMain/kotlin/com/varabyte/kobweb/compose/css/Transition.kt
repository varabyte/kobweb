package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Transitions/Using_CSS_transitions

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-behavior
sealed interface TransitionBehavior : StylePropertyValue {
    sealed interface Listable : TransitionBehavior
    companion object : CssGlobalValues<TransitionBehavior> {
        fun list(vararg behaviors: Listable) = behaviors.joinToString().unsafeCast<TransitionBehavior>()

        // Keywords
        val AllowDiscrete get() = "allow-discrete".unsafeCast<Listable>()
        val Normal get() = "normal".unsafeCast<Listable>()
    }
}

fun StyleScope.transitionBehavior(behavior: TransitionBehavior) {
    property("transition-behavior", behavior)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.transitionBehavior(behavior: TransitionBehavior.Listable) {
    transitionBehavior(behavior.unsafeCast<TransitionBehavior>())
}
// Remove the previous method too after removing this method
@Deprecated("Use transitionBehavior(TransitionBehavior.list(...)) instead.", ReplaceWith("transitionBehavior(TransitionBehavior.list(*behaviors))"))
fun StyleScope.transitionBehavior(vararg behaviors: TransitionBehavior.Listable) {
    transitionBehavior(TransitionBehavior.list(*behaviors))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
sealed interface TransitionProperty : StylePropertyValue {
    sealed interface Name : TransitionProperty

    companion object : CssGlobalValues<TransitionProperty> {
        // Custom
        fun of(customValue: String): Name {
            require(customValue.isNotEmpty() && customValue.none { it.isWhitespace() }) {
                "Invalid transition property name. A property shouldn't contain any spaces, but got \"$customValue\"."
            }
            return customValue.unsafeCast<Name>()
        }

        // Keywords
        val None get() = "none".unsafeCast<TransitionProperty>()
        val All get() = of("all") // Essentially a special property name
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
sealed interface Transition : StylePropertyValue {
    sealed interface Listable : Transition

    companion object : CssGlobalValues<Transition> {
        // Keyword
        val None: Transition get() = "none".unsafeCast<Transition>()

        fun of(
            property: TransitionProperty.Name,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ) = buildList {
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
        }.joinToString(" ").unsafeCast<Listable>()


        fun of(
            property: String,
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ) = of(TransitionProperty.of(property), duration, timingFunction, delay, behavior)

        fun list(vararg transitions: Listable) = transitions.joinToString().unsafeCast<Transition>()

        /**
         * Specify transition details that should apply to every animatable property on this element.
         */
        fun all(
            duration: CSSTimeNumericValue? = null,
            timingFunction: TransitionTimingFunction? = null,
            delay: CSSTimeNumericValue? = null,
            behavior: TransitionBehavior? = null,
        ) = of(TransitionProperty.All, duration, timingFunction, delay, behavior)

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
        ) = properties.map { property -> Transition.of(property, duration, timingFunction, delay, behavior) }.joinToString().unsafeCast<Transition>()

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
        ): Transition = properties.map { property -> Transition.of(property, duration, timingFunction, delay, behavior) }.joinToString().unsafeCast<Transition>()
    }
}

fun StyleScope.transition(transition: Transition) {
    property("transition", transition)
}

// Needed temporarily until we can remove the deprecated `vararg` version
fun StyleScope.transition(transition: Transition.Listable) {
    transition(transition.unsafeCast<Transition>())
}
// Remove the previous method too after removing this method
@Deprecated("Use transition(Transition.list(...)) instead.", ReplaceWith("transition(Transition.list(*transitions))"))
fun StyleScope.transition(vararg transitions: Transition.Listable) {
    transition(Transition.list(*transitions))
}
