package com.varabyte.kobweb.silk.style.component

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.ComponentBaseModifier
import com.varabyte.kobweb.silk.style.ComponentModifiers
import com.varabyte.kobweb.silk.style.CssStyle
import kotlin.reflect.KProperty

abstract class ComponentVariant<T : ComponentKind> {
    infix fun then(next: ComponentVariant<T>): ComponentVariant<T> {
        return CompositeComponentVariant(this, next)
    }

    @Composable
    internal abstract fun toModifier(): Modifier

    // This weird operator is provided for legacy code purposes.
    // Old code: `val ExampleVariant by ExampleStyle.addVariant { ... }`
    // New code: `val ExampleVariant = ExampleStyle.addVariant { ... }`
    // This lets us support the `by` keyword for now, although we will remove it eventually.
    @Deprecated("Please change the `by` keyword here to an `=` assignment instead.")
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): ComponentVariant<T> {
        return this
    }
}

/**
 * A default [ComponentVariant] implementation that represents a single variant style.
 */
internal class SimpleComponentVariant<T : ComponentKind>(
    val cssStyle: CssStyle,
    val baseStyle: ComponentStyle<T>
) : ComponentVariant<T>() {
    constructor(
        init: ComponentModifiers.() -> Unit,
        extraModifier: @Composable () -> Modifier,
        baseStyle: ComponentStyle<T>
    )
        : this(object : CssStyle(init, extraModifier) {}, baseStyle)

    @Composable
    override fun toModifier() = cssStyle.toModifier()
    fun intoImmutableStyle(classSelectors: ClassSelectors) = cssStyle.intoImmutableStyle(classSelectors)
}

private class CompositeComponentVariant<T : ComponentKind>(
    private val head: ComponentVariant<T>,
    private val tail: ComponentVariant<T>
) : ComponentVariant<T>() {
    @Composable
    override fun toModifier() = head.toModifier().then(tail.toModifier())
}

fun <T : ComponentKind> ComponentVariant<T>.thenIf(
    condition: Boolean,
    produce: () -> ComponentVariant<T>
): ComponentVariant<T> {
    return if (condition) this.then(produce()) else this
}

fun <T : ComponentKind> ComponentVariant<T>.thenUnless(
    condition: Boolean,
    produce: () -> ComponentVariant<T>
): ComponentVariant<T> {
    return this.thenIf(!condition, produce)
}

fun <T : ComponentKind> ComponentVariant<T>.thenIf(
    condition: Boolean,
    other: ComponentVariant<T>
): ComponentVariant<T> {
    return this.thenIf(condition) { other }
}

fun <T : ComponentKind> ComponentVariant<T>.thenUnless(
    condition: Boolean,
    other: ComponentVariant<T>
): ComponentVariant<T> {
    return this.thenUnless(condition) { other }
}

/**
 * A convenience method for folding a list of component variants into one single one that represents all of them.
 *
 * Returns `null` if the collection is empty or entirely `null`.
 */
@Composable
fun <T : ComponentKind> Iterable<ComponentVariant<T>?>.combine(): ComponentVariant<T>? {
    return reduceOrNull { acc, variant ->
        if (acc != null && variant != null) acc.then(variant) else acc ?: variant
    }
}

fun <T : ComponentKind> ComponentStyle<T>.addVariantBase(
    extraModifier: Modifier = Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = addVariantBase({ extraModifier }, init)

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [ComponentStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun <T : ComponentKind> ComponentStyle<T>.addVariantBase(
    extraModifier: @Composable () -> Modifier,
    init: ComponentBaseModifier.() -> Modifier
): ComponentVariant<T> =
    SimpleComponentVariant(init = { base { ComponentBaseModifier(colorMode).let(init) } }, extraModifier, this)
