package com.varabyte.kobweb.silk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.forms.BaseButtonStyle
import com.varabyte.kobweb.silk.components.forms.ButtonKey

/**
 * Marker interface for a key used to fetch a configuration for a component.
 *
 * For example,
 *
 * ```
 * object ButtonKey = object : ComponentKey<ButtonStyle>
 * ```
 */
interface ComponentKey<S: ComponentStyle<*>>

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
interface ComponentStyle<T: ComponentState> {
    @Composable
    @ReadOnlyComposable
    fun toModifier(state: T): Modifier
}

/**
 * Extension function to create a modifier that merges the base style with an (optional) target variant.
 */
@Composable
@ReadOnlyComposable
fun <T: ComponentState, S: ComponentStyle<T>> S.toModifier(state: T, variant: ComponentVariant<T, S>? = null): Modifier {
    return this.toModifier(state) then (variant?.style?.toModifier(state) ?: Modifier)
}

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
interface ComponentVariant<T: ComponentState, S: ComponentStyle<T>> {
    val style: S
}

class ComponentStyles {
    private val baseStyles = mutableMapOf<ComponentKey<*>, ComponentStyle<*>>()

    fun <T: ComponentState, S: ComponentStyle<T>> register(key: ComponentKey<S>, baseStyle: S) {
        baseStyles[key] = baseStyle
    }

    /**
     * Fetch a style registered by [register].
     *
     * This method will throw an exception if no style was previously registered with the target key.
     */
    @Suppress("UNCHECKED_CAST") // This should always be a valid cast thanks to register
    operator fun <T: ComponentState, S: ComponentStyle<T>> get(key: ComponentKey<S>): S = (baseStyles[key] as? S) ?:
        error("No style registered with key $key")

    fun copy(): ComponentStyles {
        val clone = ComponentStyles()
        clone.baseStyles.putAll(baseStyles)
        return clone
    }
}

val SilkComponentStyles = compositionLocalOf {
    ComponentStyles().apply {
        register(ButtonKey, BaseButtonStyle())
    }
}
