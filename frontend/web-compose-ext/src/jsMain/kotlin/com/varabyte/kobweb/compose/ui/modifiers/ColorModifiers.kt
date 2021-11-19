package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color

fun Modifier.color(color: Color) = styleModifier {
    color(color.toCssColor())
}

fun Modifier.color(color: CSSColorValue) = styleModifier {
    color(color)
}

fun Modifier.background(color: Color) = styleModifier {
    backgroundColor(color.toCssColor())
}

fun Modifier.background(color: CSSColorValue) = styleModifier {
    backgroundColor(color)
}