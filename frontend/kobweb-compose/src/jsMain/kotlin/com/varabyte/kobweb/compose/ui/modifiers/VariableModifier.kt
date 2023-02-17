package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSStyleVariable
import org.jetbrains.compose.web.css.StylePropertyValue

fun <T : StylePropertyValue> Modifier.setVariable(variable: CSSStyleVariable<T>, value: T) = styleModifier {
    // NOTE: This should just be `variable.invoke(value)`, but it seems broken for inline styles.
    // See also: https://github.com/JetBrains/compose-jb/issues/2702
    property("--${variable.name}", value)
}