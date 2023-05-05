package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.css.*
import kotlin.reflect.KProperty


// A reimplementation of org.jetbrains.compose.web.css.CSSStyleVariable, since that version uses `out` variance which
// actually allowed invalid assignment to work.
// See https://github.com/JetBrains/compose-jb/issues/2763 for more detail.
/**
 * A class for declaring a CSS style property (i.e. a variable value that can be used inside CSS styles).
 *
 * You can declare one, set it, and use it in your styles like this:
 *
 * ```
 * val bgColor by StyleVariable()
 * val MainAreaStyle by ComponentStyle.base {
 *   Modifier.setValue(bgColor, Colors.Black)
 * }
 *
 * val InvertedAreaStyle by ComponentStyle.base {
 *   Modifier.setValue(bgColor, Colors.White)
 * }
 *
 * // Box style is expected to be applied underneath a root element using either MainAreaStyle or InvertedAreaStyle
 * val BoxStyle by ComponentStyle.base {
 *   // Will be black if rendered in the main area or white in the inverted area.
 *   Modifier.backgroundColor(bgColor.value())
 * }
 * ```
 *
 * @param name The (globally unique) name for this variable.
 *
 * @param defaultFallback When you query a variable, you can specify a fallback at that time. However, if not specified,
 *   then you can provide this default fallback to be used instead. See also: [value].
 */
class StyleVariable<T : StylePropertyValue>(override val name: String, private val defaultFallback: T? = null) : CSSVariable {
    /**
     * Query this variable's current value.
     *
     * It is preferred that `Modifier.setVariable` was called in a parent scope prior to using this variable, but you
     * can provide an optional fallback value in case one was not. If this fallback and [defaultFallback] are both set,
     * then the fallback passed to this method will take precedence.
     */
    fun value(fallback: T? = null) =
        CSSVariableValue<T>(
            "var(--$name${(fallback ?: defaultFallback)?.let<StylePropertyValue, String> { ", $it" } ?: ""})"
        )
}

/**
 * A delegate provider class which allows you to create a [StyleVariable] instance via the `by` keyword.
 */
class StyleVariableProvider<T: StylePropertyValue> internal constructor(private val defaultFallback: T?) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): StyleVariable<T> {
        val name = property.name.titleCamelCaseToKebabCase()
        return StyleVariable(name, defaultFallback)
    }
}

/** Helper method for declaring a [StyleVariable] instance via the `by` keyword. */
fun <T : StylePropertyValue> StyleVariable(defaultFallback: T? = null) = StyleVariableProvider(defaultFallback)

fun <T : StylePropertyValue> Modifier.setVariable(variable: StyleVariable<T>, value: T) = styleModifier {
    // NOTE: This should just be `variable.invoke(value)`, but it seems broken for inline styles.
    // See also: https://github.com/JetBrains/compose-jb/issues/2702
    property("--${variable.name}", value)
}