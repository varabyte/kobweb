package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AccentColor
import com.varabyte.kobweb.compose.css.accentColor
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.accentColor(accentColor: AccentColor): Modifier = styleModifier {
    accentColor(accentColor)
}

fun Modifier.accentColor(color: CSSColorValue): Modifier = styleModifier {
    accentColor(AccentColor.of(color))
}

fun Modifier.color(color: CSSColorValue) = styleModifier {
    color(color)
}

fun Modifier.color(color: CSSColor) = styleModifier {
    color(color)
}

fun Modifier.colorScheme(colorScheme: ColorScheme) = styleModifier {
    colorScheme(colorScheme)
}

fun Modifier.opacity(value: Number) = styleModifier {
    opacity(value)
}

fun Modifier.opacity(value: CSSPercentageNumericValue) = styleModifier {
    opacity(value.unsafeCast<CSSSizeValue<CSSUnit.percent>>())
}
