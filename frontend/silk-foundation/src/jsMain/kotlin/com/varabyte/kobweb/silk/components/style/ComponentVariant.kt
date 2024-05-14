@file:Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION") // ReplaceWith doesn't work great for extension methods

package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.util.internal.CacheByPropertyNameDelegate
import com.varabyte.kobweb.silk.style.ClassSelectors
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleBaseScope
import com.varabyte.kobweb.silk.style.CssStyleScope
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.SimpleCssStyle
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.toModifier

@Deprecated("You need to migrate your use of `ComponentStyle` to the new, typed `CssStyle<T>` version. Please see https://github.com/varabyte/kobweb/blob/main/docs/css-style.md#migration for more guidance.")
sealed class ComponentVariant {
    infix fun then(next: ComponentVariant): ComponentVariant {
        return CompositeComponentVariant(this, next)
    }

    @Composable
    internal abstract fun toModifier(): Modifier
}

/**
 * A default [ComponentVariant] implementation that represents a single variant style.
 */
@Deprecated("Code should migrate to `com.varabyte.kobweb.silk.style.SimpleCssStyleVariant`")
internal class SimpleComponentVariant(
    val name: String,
    val cssStyle: SimpleCssStyle,
    val baseStyle: ComponentStyle
) : ComponentVariant() {
    @Composable
    override fun toModifier() = cssStyle.toModifier()
    fun intoImmutableStyle(classSelectors: ClassSelectors) = cssStyle.intoImmutableStyle(classSelectors)
}

private class CompositeComponentVariant(
    private val head: ComponentVariant,
    private val tail: ComponentVariant
) : ComponentVariant() {
    @Composable
    override fun toModifier() = head.toModifier().then(tail.toModifier())
}

fun ComponentVariant.thenIf(
    condition: Boolean,
    produce: () -> ComponentVariant
): ComponentVariant {
    return if (condition) this.then(produce()) else this
}

fun ComponentVariant.thenUnless(
    condition: Boolean,
    produce: () -> ComponentVariant
): ComponentVariant {
    return this.thenIf(!condition, produce)
}

fun ComponentVariant.thenIf(
    condition: Boolean,
    other: ComponentVariant
): ComponentVariant {
    return this.thenIf(condition) { other }
}

fun ComponentVariant.thenUnless(
    condition: Boolean,
    other: ComponentVariant
): ComponentVariant {
    return this.thenUnless(condition) { other }
}

/**
 * A convenience method for folding a list of component variants into one single one that represents all of them.
 *
 * Returns `null` if the collection is empty or entirely `null`.
 */
@Composable
fun Iterable<ComponentVariant?>.combine(): ComponentVariant? {
    return reduceOrNull { acc, variant ->
        if (acc != null && variant != null) acc.then(variant) else acc ?: variant
    }
}

/**
 * A delegate provider class which allows you to create a [ComponentVariant] via the `by` keyword.
 */
class ComponentVariantProvider internal constructor(
    private val style: ComponentStyle,
    private val extraModifiers: @Composable () -> Modifier,
    private val init: CssStyleScope.() -> Unit,
) : CacheByPropertyNameDelegate<ComponentVariant>() {
    override fun create(propertyName: String): ComponentVariant {
        // Given a style called "ExampleStyle", we want to support the following variant name simplifications:
        // - "OutlinedExampleVariant" -> "outlined" // Preferred variant naming style
        // - "ExampleOutlinedVariant" -> "outlined" // Acceptable variant naming style
        // - "OutlinedVariant"        -> "outlined" // But really the user should have kept "Example" in the name
        // - "ExampleVariant"         -> "example" // In other words, protect against empty strings!
        // - "ExampleExampleVariant"  -> "example"

        // Step 1, remove variant suffix and turn the code style into CSS sytle,
        // e.g. "OutlinedExampleVariant" -> "outlined-example"
        val withoutSuffix = propertyName.removeSuffix("Variant").titleCamelCaseToKebabCase()

        val name =
            withoutSuffix.removePrefix("${style.nameWithoutPrefix}-").removeSuffix("-${style.nameWithoutPrefix}")
                .takeIf { it.isNotEmpty() } ?: withoutSuffix
        return style.addVariant(name, extraModifiers, init)
    }
}

fun ComponentStyle.addVariant(
    extraModifiers: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
) =
    addVariant({ extraModifiers }, init)

fun ComponentStyle.addVariant(
    extraModifiers: @Composable () -> Modifier,
    init: CssStyleScope.() -> Unit
) =
    ComponentVariantProvider(this, extraModifiers, init)

fun ComponentStyle.addVariantBase(
    extraModifiers: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) =
    addVariantBase({ extraModifiers }, init)

fun ComponentStyle.addVariantBase(
    extraModifiers: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
) = ComponentVariantProvider(this, extraModifiers, init = { base { CssStyleBaseScope(colorMode).let(init) } })

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [ComponentStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.addVariantBase(
    name: String,
    extraModifiers: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
): ComponentVariant {
    return addVariant(name, extraModifiers) {
        base {
            CssStyleBaseScope(colorMode).let(init)
        }
    }
}

// region Deprecated

// The following methods are only provided so that a user's old code will still compile.
// For example, `LinkStyle` used to be a `silk.components.style.ComponentStyle`, but now it's a
// `silk.style.component.CssStyle<T>`. If the user has `LinkStyle.addVariant { ... }` in their code, it should
// continue to compile!

@Deprecated("Please change the import for this extension method to `com.varabyte.kobweb.silk.style.addVariantBase`.")
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    extraModifiers: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) = addVariantBase({ extraModifiers }, init)

@Deprecated("Please change the import for this extension method to `com.varabyte.kobweb.silk.style.addVariantBase`.")
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    extraModifiers: @Composable () -> Modifier,
    init: CssStyleBaseScope.() -> Modifier
): CssStyleVariant<K> {
    return addVariant(extraModifiers) {
        base {
            CssStyleBaseScope(colorMode).let(init)
        }
    }
}

@Deprecated("Please change the import for this extension method to `com.varabyte.kobweb.silk.style.addVariantBase`. You should use `@CssName` to specify the custom name for this variant (but leading with a dash; so `name = \"example\"` becomes `CssName(\"-example\")`.")
fun <K : ComponentKind> CssStyle<K>.addVariantBase(
    @Suppress("UNUSED_PARAMETER") name: String,
    extraModifiers: Modifier = Modifier,
    init: CssStyleBaseScope.() -> Modifier
) = addVariantBase(extraModifiers, init)

@Deprecated("Please change the import for this extension method to `com.varabyte.kobweb.silk.style.addVariant`.")
fun <K : ComponentKind> CssStyle<K>.addVariant(
    extraModifiers: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
) = addVariant({ extraModifiers }, init)

@Deprecated("Please change the import for this extension method to `com.varabyte.kobweb.silk.style.addVariant`.")
fun <K : ComponentKind> CssStyle<K>.addVariant(
    extraModifiers: @Composable () -> Modifier,
    init: CssStyleScope.() -> Unit
): CssStyleVariant<K> {
    return addVariant(extraModifiers, init)
}

@Deprecated("Please change the import for this extension method to `com.varabyte.kobweb.silk.style.addVariant`. You should use `@CssName` to specify the custom name for this variant (but leading with a dash; so `name = \"example\"` becomes `CssName(\"-example\")`.")
fun <K : ComponentKind> CssStyle<K>.addVariant(
    @Suppress("UNUSED_PARAMETER") name: String,
    extraModifiers: Modifier = Modifier,
    init: CssStyleScope.() -> Unit
) = addVariant(extraModifiers, init)


// endregion
