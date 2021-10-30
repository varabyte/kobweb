package com.varabyte.kobweb.silk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.theme.SilkTheme

/**
 * Marker interface for a key used to fetch a configuration for a component.
 *
 * For example,
 *
 * ```
 * object ButtonKey = object : ComponentKey<ButtonStyle>
 * ```
 */
interface ComponentKey<S : ComponentStyle<*>>

interface ComponentState

/**
 * Convenience class when defining component styles that don't change depending on any state
 */
object EmptyState : ComponentState

/**
 * An interface for something that represents a collection of properties that makes up the full style for a component.
 *
 * By convention, all properties should be nullable, so that styles can be partially modified by variants.
 *
 * For example,
 *
 * ```
 * abstract class ButtonStyle : ComponentStyle {
 *   open val shape: Shape? = null
 *   open val color: Color? = null
 *
 *   override fun modify(modifier) { ... }
 * }
 *
 * class BaseButtonStyle : ButtonStyle {
 *   override val shape = Rect(8.dp)
 *   override val color = ...
 * }
 *
 * interface ButtonVariant : ComponentVariant<ButtonStyle>
 * class GhostButtonVariant : ButtonVariant {
 *    override val style = object : ButtonStyle {
 *       override val color = Color.Transparent
 *    }
 * }
 * ```
 */
interface ComponentStyle<T : ComponentState> {
    @Composable
    @ReadOnlyComposable
    fun toModifier(state: T): Modifier
}

/**
 * Extension function to create a modifier that merges the base style with an (optional) target variant.
 */
@Composable
@ReadOnlyComposable
fun <T : ComponentState, S : ComponentStyle<T>> S.toModifier(
    state: T,
    variant: ComponentVariant<T, S>? = null
): Modifier {
    return this.toModifier(state) then (variant?.style?.toModifier(state) ?: Modifier)
}

/**
 * Convenience shortcut for component styles that use the [EmptyState] (so the state that gets passed in will always be
 * the same.)
 */
@Composable
@ReadOnlyComposable
fun <S : ComponentStyle<EmptyState>> S.toModifier(variant: ComponentVariant<EmptyState, S>? = null) =
    toModifier(EmptyState, variant)

/**
 * An interface which can be used for modifying a base style based on variants.
 *
 * For example, links might do something like:
 *
 * ```
 * interface LinkVariant : ComponentVariant<LinkStyle>
 * interface UnderlineLinkVariant : LinkVariant
 * interface UndecoratedLinkVariant : LinkVariant
 * ```
 *
 * At this point, users can add their own variants in their own code and register them.
 */
interface ComponentVariant<T : ComponentState, S : ComponentStyle<T>> {
    val style: S
}

interface ComponentStyles {
    /**
     * Fetch a style by its [ComponentKey].
     *
     * Styles can be overridden and additional styles can be registered using the [SilkTheme] composable.
     *
     * This method will throw an exception if no style was previously registered with the target key.
     */
    operator fun <T : ComponentState, S : ComponentStyle<T>> get(key: ComponentKey<S>): S
}

class KeyedStyle<T : ComponentState, S : ComponentStyle<T>>(val key: ComponentKey<S>, val style: S) {
    internal fun setOn(styles: MutableComponentStyles) {
        styles[key] = style
    }
}
infix fun <T : ComponentState, S : ComponentStyle<T>> ComponentKey<S>.to(style: S) = KeyedStyle(this, style)

internal class MutableComponentStyles(private val parent: ComponentStyles? = null) : ComponentStyles {
    private val baseStyles = mutableMapOf<ComponentKey<*>, ComponentStyle<*>>()

    operator fun <T : ComponentState, S : ComponentStyle<T>> set(key: ComponentKey<S>, baseStyle: S) {
        baseStyles[key] = baseStyle
    }

    @Suppress("UNCHECKED_CAST") // This should always be a valid cast thanks to register
    override operator fun <T : ComponentState, S : ComponentStyle<T>> get(key: ComponentKey<S>): S =
        (baseStyles[key] as? S)
            ?: if (parent != null) parent[key] else { error("No style registered with key $key") }
}