package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.util.titleCamelCaseToKebabCase
import org.jetbrains.compose.web.css.*
import kotlin.reflect.KProperty


// A reimplementation of org.jetbrains.compose.web.css.CSSStyleVariable, since that version uses `out` variance which
// actually allowed invalid assignment to work.
// See https://github.com/JetBrains/compose-jb/issues/2763 for more detail.
class StyleVariable<T : StylePropertyValue>(override val name: String) : CSSVariable {
    fun value(fallback: T? = null) =
        CSSVariableValue<T>(
            "var(--$name${fallback?.let<StylePropertyValue, String> { ", $it" } ?: ""})"
        )
}

/**
 * A delegate provider class which allows you to create a [StyleVariable] instance via the `by` keyword.
 */
class StyleVariableProvider<T: StylePropertyValue> internal constructor() {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): StyleVariable<T> {
        val name = property.name.titleCamelCaseToKebabCase()
        return StyleVariable(name)
    }
}

fun <T : StylePropertyValue> StyleVariable() = StyleVariableProvider<T>()

fun <T : StylePropertyValue> Modifier.setVariable(variable: StyleVariable<T>, value: T) = styleModifier {
    // NOTE: This should just be `variable.invoke(value)`, but it seems broken for inline styles.
    // See also: https://github.com/JetBrains/compose-jb/issues/2702
    property("--${variable.name}", value)
}