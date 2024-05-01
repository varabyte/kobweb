@file:Suppress("FunctionName")

package com.varabyte.kobweb.silk.style.component

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.ComponentBaseModifier
import com.varabyte.kobweb.silk.style.ComponentModifier
import com.varabyte.kobweb.silk.style.ComponentModifiers
import com.varabyte.kobweb.silk.style.CssStyle
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*

// TODO: docs
interface ComponentKind

/**
 * A class which allows a user to define styles that drive the look and feel of a component.
 *
 * You should compare this with [CssStyle], which is more broadly applicable and most often what users are expected to
 * use.
 *
 * In contrast, `ComponentStyle` is a specific kind of `CssStyle` which allows users to create variants of its styles
 * that tweak them. For example, you might have a button style which renders rectangular by default, but you can provide
 * a circular variant, or a semi-transparent variant, etc.
 *
 * To create a new component style, you must first declare a new [ComponentKind] that is uniquely associated with your
 * widget. This will allow you to enjoy type safety when layering variants on top of the style.

 * Once the style is defined, you can simply use the [toModifier] method to apply it:
 *
 * ```
 * val CustomKind : ComponentKind
 * val CustomStyle by ComponentStyle<CustomKind> { ... }
 *
 * @Composable
 * fun CustomWidget(..., variant: ComponentVariant<CustomKind>? = null, ...) {
 *   val modifier = CustomStyle.toModifier(variant).then(...)
 *   // ^ This modifier is now set with your registered styles.
 * }
 * ```
 *
 * NOTE: Even if you don't provide any variants yourself, it is a good practice to accept a nullable variant in your
 * widget's parameters. This way, a user can create their own variants and pass them in.
 *
 * @param extraModifier Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 *
 * @see CssStyle
 */
abstract class ComponentStyle<T : ComponentKind>(
    internal val init: ComponentModifiers.() -> Unit,
    internal val extraModifier: @Composable () -> Modifier,
) {
    internal val cssStyle = object : CssStyle(init, extraModifier) {}

    companion object // for extensions

    /**
     * Create a new variant that builds on top of this style.
     *
     *  @param extraModifier Additional modifiers that can be tacked onto this component style, convenient for
     *    including non-style attributes that should always get applied whenever this variant style is applied.
     */
    fun addVariant(
        extraModifier: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ): ComponentVariant<T> {
        return addVariant({ extraModifier }, init)
    }

    /**
     * Create a new variant that builds on top of this style.
     *
     *  @param extraModifier Additional modifiers that can be tacked onto this component style, convenient for
     *    including non-style attributes that should always get applied whenever this variant style is applied.
     */
    fun addVariant(
        extraModifier: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ): ComponentVariant<T> {
        return SimpleComponentVariant(
            object : CssStyle(init, extraModifier) {},
            baseStyle = this
        )
    }
}

/** Represents the class selectors associated with a [ComponentStyle]. */
internal value class ClassSelectors(private val value: List<String>) {
    // Convert selectors (".someClass") to class names ("someClass")
    val classNames get() = value.map { it.removePrefix(".") }
    operator fun plus(other: ClassSelectors) = ClassSelectors(value + other.value)
}

/**
 * Convenience method when you only care about registering the base style, which can help avoid a few extra lines.
 *
 * So this:
 *
 * ```
 * ComponentStyle.base {
 *   Modifier.fontSize(48.px)
 * }
 * ```
 *
 * replaces this:
 *
 * ```
 * ComponentStyle {
 *   base {
 *     Modifier.fontSize(48.px)
 *   }
 * }
 * ```
 *
 * You may still wish to construct a [ComponentStyle] directly instead if you expect that at some point in the future
 * you'll want to add additional, non-base styles.
 */
fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifier: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
): ComponentStyle<T> {
    return base({ extraModifier }, init)
}

fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifier: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
): ComponentStyle<T> {
    return object : ComponentStyle<T>(init = {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }, extraModifier) {}
}

fun <T : ComponentKind> ComponentStyle(
    extraModifier: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) = ComponentStyle<T>({ extraModifier }, init)

fun <T : ComponentKind> ComponentStyle(
    extraModifier: @Composable () -> Modifier,
    init: ComponentModifiers.() -> Unit
) = object : ComponentStyle<T>(init, extraModifier) {}

fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifier: Modifier = Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = base<T>({ extraModifier }, init)

fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifier: @Composable () -> Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = ComponentStyle<T>(extraModifier, init = { base { ComponentBaseModifier(colorMode).let(init) } })

/**
 * Convert a user's component style into a [Modifier].
 *
 * @param variants 0 or more variants that can potentially extend the base style. Although it may seem odd at first that
 *   nullable values are accepted here, that's because Silk widgets all default their `variant` parameter to null, so
 *   it's easier to just accept null here rather than require users to branch based on whether the variant is null or
 *   not.
 */
@Composable
fun <T : ComponentKind> ComponentStyle<T>.toModifier(vararg variants: ComponentVariant<T>?): Modifier {
    return cssStyle.toModifier()
        .then(variants.toList().combine()?.toModifier() ?: Modifier)
}

/**
 * Convert a user's component style into an [AttrsScope] builder.
 *
 * This is useful if you need to convert a style into something directly consumable by a Compose HTML widget.
 */
@Composable
fun <A : AttrsScope<*>, T : ComponentKind> ComponentStyle<T>.toAttrs(
    vararg variant: ComponentVariant<T>?,
    finalHandler: (A.() -> Unit)? = null
): A.() -> Unit {
    return this.toModifier(*variant).toAttrs(finalHandler)
}

/**
 * A convenience method for chaining a collection of styles into a single modifier.
 *
 * This can be useful as sometimes you might break up many css rules across multiple styles for re-usability, and it's
 * much easier to type `listOf(Style1, Style2, Style3).toModifier()` than
 * `Style1.toModifier().then(Style2.toModifier())...`
 */
@Composable
fun Iterable<ComponentStyle<*>>.toModifier(): Modifier {
    return fold<_, Modifier>(Modifier) { acc, style -> acc.then(style.toModifier()) }
}

/**
 * A convenience method for chaining a collection of styles into a single [AttrsScope] builder.
 */
@Composable
fun <A : AttrsScope<*>> Iterable<ComponentStyle<*>>.toAttrs(finalHandler: (A.() -> Unit)? = null): A.() -> Unit {
    return this.toModifier().toAttrs(finalHandler)
}
