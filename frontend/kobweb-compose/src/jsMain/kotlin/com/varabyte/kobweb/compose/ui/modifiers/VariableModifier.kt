package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

@Deprecated("StyleVariable has moved. Use com.varabyte.kobweb.compose.css.StyleVariable instead.")
typealias StyleVariable<T> = com.varabyte.kobweb.compose.css.StyleVariable<T>
@Deprecated("StyleVariableProvider has moved. Use com.varabyte.kobweb.compose.css.StyleVariableProvider instead.")
typealias StyleVariableProvider<T> = com.varabyte.kobweb.compose.css.StyleVariableProvider<T>

@Deprecated(
    "StyleVariable has moved. Use com.varabyte.kobweb.compose.css.StyleVariable instead.",
    ReplaceWith("com.varabyte.kobweb.compose.css.StyleVariable(defaultFallback, prefix)")
)
fun <T : StylePropertyValue> StyleVariable(defaultFallback: T? = null, prefix: String? = null) =
    com.varabyte.kobweb.compose.css.StyleVariable(defaultFallback, prefix)

fun <T : StylePropertyValue> Modifier.setVariable(
    variable: com.varabyte.kobweb.compose.css.StyleVariable<T>,
    value: T
) = styleModifier {
    // NOTE: This should just be `variable.invoke(value)`, but it seems broken for inline styles.
    // See also: https://github.com/JetBrains/compose-jb/issues/2702
    property("--${variable.name}", value)
}
