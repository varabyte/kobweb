package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun <T : StylePropertyValue> Modifier.setVariable(variable: StyleVariable.PropertyValue<T>, value: T) = styleModifier {
    setVariable(variable, value)
}

fun <T : Number> Modifier.setVariable(variable: StyleVariable.NumberValue<T>, value: T) = styleModifier {
    setVariable(variable, value)
}

fun Modifier.setVariable(variable: StyleVariable.StringValue, value: String) = styleModifier {
    setVariable(variable, value)
}
