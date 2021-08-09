package kobweb.silk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import kobweb.silk.components.forms.BaseButtonStyle
import kobweb.silk.components.forms.ButtonKey
import org.jetbrains.compose.common.internal.ActualModifier

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
/** Conveneince class for components that don't depend on any state */
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
    fun modify(modifier: ActualModifier, state: T)
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

    @Composable
    fun <T: ComponentState, S: ComponentStyle<T>> modify(key: ComponentKey<S>, modifier: ActualModifier, state: T, variant: ComponentVariant<T, S>? = null) {
        @Suppress("UNCHECKED_CAST") // We control register, so we know the cast is good
        (baseStyles[key] as? ComponentStyle<T>)?.let { baseStyle  ->
            baseStyle.modify(modifier, state)
            variant?.style?.modify(modifier, state)
        }
    }

    fun copy(): ComponentStyles {
        val other = ComponentStyles()
        other.baseStyles.putAll(baseStyles)
        return other
    }
}

val SilkComponentStyles = compositionLocalOf {
    ComponentStyles().apply {
        register(ButtonKey, BaseButtonStyle())
    }
}
