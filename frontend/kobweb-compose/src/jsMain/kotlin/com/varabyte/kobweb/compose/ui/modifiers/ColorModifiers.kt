package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.color(color: Color) = styleModifier {
    color(color.toCssColor())
}

fun Modifier.color(color: CSSColorValue) = styleModifier {
    color(color)
}

fun Modifier.color(value: String) = styleModifier {
    property("color", value)
}

fun Modifier.opacity(value: Number) = styleModifier {
    opacity(value)
}

fun Modifier.opacity(value: CSSSizeValue<CSSUnit.percent>) = styleModifier {
    opacity(value)
}