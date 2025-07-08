package com.varabyte.kobweb.compose.ui

import androidx.compose.runtime.Immutable

// Inspired by the official Android API
// See also: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/Modifier.kt

/**
 * A parent interface that represents a collection of zero (or more) modifier elements.
 *
 * An individual modifier, when triggered, will run an operation responsible for modifying the look and feel of some
 * target UI component.
 */
@Immutable
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
 * Like [then] but the [other] modifier is only applied if the condition is true.
 */
fun Modifier.thenIf(condition: Boolean, other: Modifier): Modifier {
    return this.thenIf(condition) { other }
}

/**
 * Like [thenIf] but with an inverted condition.
 */
fun Modifier.thenUnless(condition: Boolean, other: Modifier): Modifier {
    return this.thenUnless(condition) { other }
}

/**
 * Like the version of [thenIf] which takes in a modifier directly, but it produces that modifier lazily.
 *
 * This is occasionally useful if you have a Modifier that is expensive to create, e.g. it takes some complicated
 * parameters you need to allocate which is a waste if the condition is false.
 */
inline fun Modifier.thenIf(condition: Boolean, lazyProduce: () -> Modifier): Modifier {
    return this.then(if (condition) lazyProduce() else Modifier)
}

inline fun <T> Modifier.thenIfNotNull(value: T?, consume: (T) -> Modifier): Modifier {
    return this.thenIf(value != null) { consume(value!!) }
}

/**
 * Like the version of [thenUnless] which takes in a modifier directly, but it produces that modifier lazily.
 *
 * This is occasionally useful if you have a Modifier that is expensive to create, e.g. it takes some complicated
 * parameters you need to allocate which is a waste if the condition is true.
 */
inline fun Modifier.thenUnless(condition: Boolean, lazyProduce: () -> Modifier): Modifier {
    return this.thenIf(!condition, lazyProduce)
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

    override fun equals(other: Any?): Boolean =
        other is ChainedModifier && current == other.current && next == other.next

    override fun hashCode(): Int = current.hashCode() + 31 * next.hashCode()
}
