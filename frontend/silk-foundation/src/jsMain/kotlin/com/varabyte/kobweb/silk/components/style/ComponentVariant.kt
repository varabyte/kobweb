package com.varabyte.kobweb.silk.components.style

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.silk.components.util.internal.CacheByPropertyNameDelegate
import org.jetbrains.compose.web.css.*

sealed class ComponentVariant {

    infix fun then(next: ComponentVariant): ComponentVariant {
        return CompositeComponentVariant(this, next)
    }

    /**
     * Add this [ComponentVariant]'s styles to the target [StyleSheet].
     *
     * @return The CSS class selectors associated specifically with this variant. For example, if the selector
     *  for this variant is `.some-style.some-variant`, then this method will only contain `some-variant`.
     *
     *  @see ComponentStyle.addStylesInto
     */
    internal abstract fun addStylesInto(styleSheet: StyleSheet): ClassSelectors

    @Composable
    internal abstract fun toModifier(): Modifier
}

/**
 * A default [ComponentVariant] implementation that represents a single variant style.
 */
internal class SimpleComponentVariant(val style: ComponentStyle, val baseStyle: ComponentStyle) : ComponentVariant() {
    /**
     * The raw variant name, unqualified by its parent base style.
     *
     * This name is not guaranteed to be unique across all variants. If you need that, check `style.name` instead.
     */
    val name: String
        get() = style.name.removePrefix("${baseStyle.name}-")

    override fun addStylesInto(styleSheet: StyleSheet): ClassSelectors {
        // If you are using a variant, require it be associated with a tag already associated with the base style
        // e.g. if you have a link variant ("silk-link-undecorated") it should only be applied if the tag is also
        // a link (so this would be registered as ".silk-link.silk-link-undecorated").
        // To put it another way, if you use a link variant with a surface widget, it won't be applied.
        return style.addStylesInto(styleSheet, ".${baseStyle.name}.${style.name}")
    }

    @Composable
    override fun toModifier() = style.toModifier()
    fun intoImmutableStyle(classSelectors: ClassSelectors) = style.intoImmutableStyle(classSelectors)
}

private class CompositeComponentVariant(private val head: ComponentVariant, private val tail: ComponentVariant) :
    ComponentVariant() {
    override fun addStylesInto(styleSheet: StyleSheet): ClassSelectors {
        return head.addStylesInto(styleSheet) + tail.addStylesInto(styleSheet)
    }

    @Composable
    override fun toModifier() = head.toModifier().then(tail.toModifier())
}

fun ComponentVariant.thenIf(condition: Boolean, produce: () -> ComponentVariant): ComponentVariant {
    return if (condition) this.then(produce()) else this
}

fun ComponentVariant.thenUnless(condition: Boolean, produce: () -> ComponentVariant): ComponentVariant {
    return this.thenIf(!condition, produce)
}

fun ComponentVariant.thenIf(condition: Boolean, other: ComponentVariant): ComponentVariant {
    return this.thenIf(condition) { other }
}

fun ComponentVariant.thenUnless(condition: Boolean, other: ComponentVariant): ComponentVariant {
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
    private val init: ComponentModifiers.() -> Unit,
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

fun ComponentStyle.addVariant(extraModifiers: Modifier = Modifier, init: ComponentModifiers.() -> Unit) =
    addVariant({ extraModifiers }, init)

fun ComponentStyle.addVariant(extraModifiers: @Composable () -> Modifier, init: ComponentModifiers.() -> Unit) =
    ComponentVariantProvider(this, extraModifiers, init)

fun ComponentStyle.addVariantBase(extraModifiers: Modifier = Modifier, init: ComponentBaseModifier.() -> Modifier) =
    addVariantBase({ extraModifiers }, init)

fun ComponentStyle.addVariantBase(
    extraModifiers: @Composable () -> Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = ComponentVariantProvider(this, extraModifiers, init = { base { ComponentBaseModifier(colorMode).let(init) } })

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * You may still wish to use [ComponentStyle.addVariant] instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun ComponentStyle.addVariantBase(
    name: String,
    extraModifiers: Modifier = Modifier,
    init: ComponentBaseModifier.() -> Modifier
): ComponentVariant {
    return addVariant(name, extraModifiers) {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }
}
