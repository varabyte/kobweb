@file:Suppress("DEPRECATION") // Remove this after deleting ComponentVariant references

package com.varabyte.kobweb.silk.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.StubbedComponentVariant
import kotlin.reflect.KProperty

@Immutable // TODO: Remove when CMP-5680 is fixed
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


// region Deprecated

// The following methods are only added to prevent potential compile errors when migrating code over to the new
// CssStyle APIs. They should be removed when we remove ComponentStyle from the codebase.

@Suppress("DeprecatedCallableAddReplaceWith", "UNUSED_PARAMETER")
@Deprecated("You are likely seeing this after a migration to use `CssStyle`. You should now use `@CssName` to specify the custom name for this style (`name = \"example\"` becomes `CssName(\"example\")`.")
fun <K : ComponentKind> CssStyle<K>.addVariant(
    name: String,
    extraModifier: Modifier = Modifier,
    prefix: String? = null,
    init: CssStyleScope.() -> Unit
): CssStyleVariant<K> = addVariant(extraModifier, init)

@Suppress("DeprecatedCallableAddReplaceWith", "UNUSED_PARAMETER")
@Deprecated("You are likely seeing this after a migration to use `CssStyle`. You should now use `@CssName` to specify the custom name for this style (`name = \"example\"` becomes `CssName(\"example\")`.")
fun <K : ComponentKind> CssStyle<K>.addVariant(
    name: String,
    extraModifier: @Composable () -> Modifier,
    prefix: String? = null,
    init: CssStyleScope.() -> Unit
): CssStyleVariant<K> = addVariant(extraModifier, init)

@Suppress("DeprecatedCallableAddReplaceWith", "UNUSED_PARAMETER")
@Deprecated("You are likely seeing this after a migration to use `CssStyle`. You should now use `@CssName` to specify the custom name for this style (`name = \"example\"` becomes `CssName(\"example\")`.")
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    name: String,
    extraModifier: Modifier = Modifier,
    prefix: String? = null,
    init: CssStyleBaseScope.() -> Modifier
): CssStyleVariant<K> = addVariantBase(extraModifier, init)

@Suppress("DeprecatedCallableAddReplaceWith", "UNUSED_PARAMETER")
@Deprecated("You are likely seeing this after a migration to use `CssStyle`. You should now use `@CssName` to specify the custom name for this style (`name = \"example\"` becomes `CssName(\"example\")`.")
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    name: String,
    extraModifier: @Composable () -> Modifier,
    prefix: String? = null,
    init: CssStyleBaseScope.() -> Modifier
): CssStyleVariant<K> = addVariantBase(extraModifier, init)

private fun stubbedComponentVariant() = StubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariant(
    extraModifier: Modifier = Modifier,
    prefix: String? = null,
    init: CssStyleScope.() -> Unit
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariant(
    extraModifier: @Composable () -> Modifier,
    prefix: String? = null,
    init: CssStyleScope.() -> Unit
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariantBase(
    extraModifier: Modifier = Modifier,
    prefix: String? = null,
    init: CssStyleBaseScope.() -> Modifier
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariantBase(
    extraModifier: @Composable () -> Modifier,
    prefix: String? = null,
    init: CssStyleBaseScope.() -> Modifier
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariant(
    name: String,
    extraModifier: Modifier = Modifier,
    prefix: String? = null,
    init: CssStyleScope.() -> Unit
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariant(
    name: String,
    extraModifier: @Composable () -> Modifier,
    prefix: String? = null,
    init: CssStyleScope.() -> Unit
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariantBase(
    name: String,
    extraModifier: Modifier = Modifier,
    prefix: String? = null,
    init: CssStyleBaseScope.() -> Modifier
): ComponentVariant = stubbedComponentVariant()

@Suppress("UNUSED_PARAMETER", "UnusedReceiverParameter")
@Deprecated("This call to `addVariant` is not supported for untyped `CssStyle`s and is likely the result of an incomplete migration. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#converting-a-legacy-componentstyle-into-a-cssstyle for more guidance.", level = DeprecationLevel.ERROR)
fun CssStyle<GeneralKind>.addVariantBase(
    name: String,
    extraModifier: @Composable () -> Modifier,
    prefix: String? = null,
    init: CssStyleBaseScope.() -> Modifier
): ComponentVariant = stubbedComponentVariant()

// endregion
