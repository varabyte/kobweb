package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

@Deprecated("StyleVariable has moved. Use com.varabyte.kobweb.compose.css.StyleVariable.PropertyValue instead.")
typealias StyleVariable<T> = com.varabyte.kobweb.compose.css.StyleVariable.PropertyValue<T>
@Deprecated("StyleVariableProvider has moved. Use com.varabyte.kobweb.compose.css.StyleVariablePropertyProvider instead.")
typealias StyleVariableProvider<T> = StyleVariablePropertyProvider<T>

@Deprecated(
    "StyleVariable has moved. Use com.varabyte.kobweb.compose.css.StyleVariable instead.",
    ReplaceWith("com.varabyte.kobweb.compose.css.StyleVariable(defaultFallback, prefix)")
)
fun <T : StylePropertyValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    com.varabyte.kobweb.compose.css.StyleVariable(defaultFallback, prefix)

fun <T : StylePropertyValue> Modifier.setVariable(
    variable: com.varabyte.kobweb.compose.css.StyleVariable.PropertyValue<T>,
    value: T
) = styleModifier {
    setVariable(variable, value)
}

fun <T : Number> Modifier.setVariable(
    variable: com.varabyte.kobweb.compose.css.StyleVariable.NumberValue<T>,
    value: T
) = styleModifier {
    setVariable(variable, value)
}

fun Modifier.setVariable(
    variable: com.varabyte.kobweb.compose.css.StyleVariable.StringValue,
    value: String
) = styleModifier {
    setVariable(variable, value)
}
