package com.varabyte.kobweb.compose.ui

// Inspired by the official Android API
// See also: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/Modifier.kt

/**
 * A parent interface that represents a collection of zero (or more) modifier elements.
 *
 * An individual modifier, when triggered, will run an operation responsible for modifying the look and feel of some
 * target UI component.
 */
interface Modifier {
    /**
     * Run through all elements from left to right and apply [operation] on each one in order.
     *
     * Each operation is passed in an accumulated value which it can modify. The method ultimately returns the result
     * returned from the last operation.
     *
     * Note: This is identical to Android's `foldIn` method. However, we are calling it simply `fold` for now, as
     * we don't currently have a need for `foldOut`, as this codebase (and the web approach in general?) doesn't yet
     * have a need for the other direction, and `fold` is more consistent with [Iterable.fold].
     */
    fun <R> fold(initial: R, operation: (R, Element) -> R): R

    /**
     * Concatenates this modifier with another, returning a new modifier representing the chain.
     */
    infix fun then(other: Modifier): Modifier =
        if (other === Modifier) this else ChainedModifier(this, other)

    /**
     * A single element within a [Modifier] chain.
     */
    interface Element : Modifier {
        override fun <R> fold(initial: R, operation: (R, Element) -> R): R = operation(initial, this)
    }

    /**
     * An empty modifier that acts as the starting point for a modifier chain.
     */
    companion object : Modifier {
        override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial
        override infix fun then(other: Modifier): Modifier = other
    }
}

/**
 * An entry in a [Modifier] chain.
 */
private class ChainedModifier(
    private val current: Modifier,
    private val next: Modifier
) : Modifier {
    override fun <R> fold(initial: R, operation: (R, Modifier.Element) -> R): R {
        return next.fold(current.fold(initial, operation), operation)
    }
}

