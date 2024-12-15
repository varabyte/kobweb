package com.varabyte.kobweb.silk.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier

@Immutable // TODO: Remove when CMP-5680 is fixed
abstract class CssStyleVariant<K : ComponentKind> {
    infix fun then(next: CssStyleVariant<K>): CssStyleVariant<K> {
        return CompositeCssStyleVariant(this, next)
    }

    @Composable
    internal abstract fun toModifier(): Modifier
}

/**
 * A default [CssStyleVariant] implementation that represents a single variant style.
 */
internal open class SimpleCssStyleVariant<K : ComponentKind>(
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

internal class ExtendingCssStyleVariant<K : ComponentKind>(
    init: CssStyleScope.() -> Unit,
    extraModifier: @Composable () -> Modifier,
    internal val baseVariant: SimpleCssStyleVariant<K>
) : SimpleCssStyleVariant<K>(init, { extraModifier().then(baseVariant.toModifier()) }, baseVariant.baseStyle)

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

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [CssStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
): CssStyleVariant<K> = addVariantBase({ extraModifier }, init)

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

fun <K : ComponentKind> CssStyleVariant<K>.extendedBy(
    extraModifier: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
) =
    extendedBy({ extraModifier }, init)

fun <K : ComponentKind> CssStyleVariant<K>.extendedBy(
    extraModifier: @Composable () -> Modifier,
    init: CssStyleScope.() -> Unit
): CssStyleVariant<K> = ExtendingCssStyleVariant(init, extraModifier, this as SimpleCssStyleVariant<K>)

fun <K : ComponentKind> CssStyleVariant<K>.extendedByBase(
    extraModifier: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) =
    extendedByBase({ extraModifier }, init)

fun <K : ComponentKind> CssStyleVariant<K>.extendedByBase(
    extraModifier: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
) = extendedBy(extraModifier) {
    base { CssStyleBaseScope(colorMode).let(init) }
}
