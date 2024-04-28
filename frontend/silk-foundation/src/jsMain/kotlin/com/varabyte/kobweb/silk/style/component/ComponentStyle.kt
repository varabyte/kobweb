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
 * A class which allows a user to define styles that get added to the page's stylesheet, instead of inline styles.
 *
 * This is important because some functionality is only available when defined in the stylesheet, e.g. link colors,
 * media queries, and pseudo classes.
 *
 * If defining a style for a custom widget, you should call the [toModifier] method to apply it:
 *
 * ```
 * val CustomStyle = ComponentStyle("my-style") { ... }
 *
 * @Composable
 * fun CustomWidget(..., variant: ComponentVariant<*>? = null, ...) {
 *   val modifier = CustomStyle.toModifier(variant).then(...)
 *   // ^ This modifier is now set with your registered styles.
 * }
 * ```
 *
  * @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for including
 *   non-style attributes whenever this style is applied.
 */
abstract class ComponentStyle<T : ComponentKind>(
    internal val init: ComponentModifiers.() -> Unit,
    internal val extraModifiers: @Composable () -> Modifier,
) {
    internal val cssStyle = object : CssStyle(init, extraModifiers) {}

    companion object // for extensions

    /**
     * Create a new variant that builds on top of this style.
     *
     *  @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for
     *    including non-style attributes that should always get applied whenever this variant style is applied.
     */
    fun addVariant(
        extraModifiers: Modifier = Modifier,
        init: ComponentModifiers.() -> Unit
    ): ComponentVariant<T> {
        return addVariant({ extraModifiers }, init)
    }

    /**
     * Create a new variant that builds on top of this style.
     *
     *  @param extraModifiers Additional modifiers that can be tacked onto this component style, convenient for
     *    including non-style attributes that should always get applied whenever this variant style is applied.
     */
    fun addVariant(
        extraModifiers: @Composable () -> Modifier,
        init: ComponentModifiers.() -> Unit
    ): ComponentVariant<T> {
        return SimpleComponentVariant(
            object : CssStyle(init, extraModifiers) {},
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
    extraModifiers: Modifier = Modifier,
    init: ComponentModifier.() -> Modifier
): ComponentStyle<T> {
    return base({ extraModifiers }, init)
}

fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifier.() -> Modifier
): ComponentStyle<T> {
    return object : ComponentStyle<T>(init = {
        base {
            ComponentBaseModifier(colorMode).let(init)
        }
    }, extraModifiers) {}
}

fun <T : ComponentKind> ComponentStyle(
    extraModifiers: Modifier = Modifier,
    init: ComponentModifiers.() -> Unit
) = ComponentStyle<T>({ extraModifiers }, init)

fun <T : ComponentKind> ComponentStyle(
    extraModifiers: @Composable () -> Modifier,
    init: ComponentModifiers.() -> Unit
) = object : ComponentStyle<T>(init, extraModifiers) {}

fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifiers: Modifier = Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = base<T>({ extraModifiers }, init)

fun <T : ComponentKind> ComponentStyle.Companion.base(
    extraModifiers: @Composable () -> Modifier,
    init: ComponentBaseModifier.() -> Modifier
) = ComponentStyle<T>(extraModifiers, init = { base { ComponentBaseModifier(colorMode).let(init) } })

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
