package com.varabyte.kobweb.silk.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import kotlin.reflect.KProperty

abstract class CssStyleVariant<K : ComponentKind> {
    infix fun then(next: CssStyleVariant<K>): CssStyleVariant<K> {
        return CompositeCssStyleVariant(this, next)
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
    ): CssStyleVariant<K> {
        return this
    }
}

/**
 * A default [CssStyleVariant] implementation that represents a single variant style.
 */
internal class SimpleCssStyleVariant<K : ComponentKind>(
    val cssStyle: CssStyle<K>,
    val baseStyle: CssStyle<K>
) : CssStyleVariant<K>() {
    constructor(
        init: CssStyleScope.() -> Unit,
        extraModifier: @Composable () -> Modifier,
        baseStyle: CssStyle<K>
    )
        : this(object : CssStyle<K>(init, extraModifier) {}, baseStyle)

    @Composable
    override fun toModifier() = cssStyle.toModifier()
}

private class CompositeCssStyleVariant<K : ComponentKind>(
    private val head: CssStyleVariant<K>,
    private val tail: CssStyleVariant<K>
) : CssStyleVariant<K>() {
    @Composable
    override fun toModifier() = head.toModifier().then(tail.toModifier())
}

fun <K : ComponentKind> CssStyleVariant<K>.thenIf(
    condition: Boolean,
    produce: () -> CssStyleVariant<K>
): CssStyleVariant<K> {
    return if (condition) this.then(produce()) else this
}

fun <K : ComponentKind> CssStyleVariant<K>.thenUnless(
    condition: Boolean,
    produce: () -> CssStyleVariant<K>
): CssStyleVariant<K> {
    return this.thenIf(!condition, produce)
}

fun <K : ComponentKind> CssStyleVariant<K>.thenIf(
    condition: Boolean,
    other: CssStyleVariant<K>
): CssStyleVariant<K> {
    return this.thenIf(condition) { other }
}

fun <K : ComponentKind> CssStyleVariant<K>.thenUnless(
    condition: Boolean,
    other: CssStyleVariant<K>
): CssStyleVariant<K> {
    return this.thenUnless(condition) { other }
}

/**
 * A convenience method for folding a list of component variants into one single one that represents all of them.
 *
 * Returns `null` if the collection is empty or entirely `null`.
 */
@Composable
fun <K : ComponentKind> Iterable<CssStyleVariant<K>?>.combine(): CssStyleVariant<K>? {
    return reduceOrNull { acc, variant ->
        if (acc != null && variant != null) acc.then(variant) else acc ?: variant
    }
}

fun <K : ComponentKind> CssStyle<K>.addVariant(
    extraModifier: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
): CssStyleVariant<K> {
    return addVariant({ extraModifier }, init)
}


fun <K : ComponentKind> CssStyle<K>.addVariant(
    extraModifier: @Composable () -> Modifier,
    init: CssStyleScope.() -> Unit
): CssStyleVariant<K> {
    return SimpleCssStyleVariant(
        object : CssStyle<K>(init, extraModifier) {},
        baseStyle = this
    )
}


fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) = addVariantBase({ extraModifier }, init)

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [CssStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    extraModifier: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
): CssStyleVariant<K> =
    SimpleCssStyleVariant(init = { base { CssStyleBaseScope(colorMode).let(init) } }, extraModifier, this)
