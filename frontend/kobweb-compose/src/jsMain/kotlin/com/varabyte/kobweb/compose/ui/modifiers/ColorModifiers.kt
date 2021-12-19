package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.background
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.opacity

fun Modifier.background(value: String) = styleModifier {
    background(value)
}

fun Modifier.backgroundColor(color: Color) = styleModifier {
    backgroundColor(color.toCssColor())
}

fun Modifier.backgroundColor(value: String) = styleModifier {
    property("background-color", value)
}

fun Modifier.backgroundColor(color: CSSColorValue) = styleModifier {
    backgroundColor(color)
}

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