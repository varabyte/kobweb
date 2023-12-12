package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.LineStyle

fun Modifier.outline(width: CSSLengthNumericValue? = null, style: LineStyle? = null, color: CSSColorValue? = null) =
    styleModifier {
        outline(width, style, color)
    }

fun Modifier.outlineColor(outlineColor: OutlineColor) = styleModifier {
    outlineColor(outlineColor)
}

fun Modifier.outlineColor(value: CSSColorValue) = styleModifier {
    outlineColor(value)
}

fun Modifier.outlineOffset(value: CSSLengthNumericValue) = styleModifier {
    outlineOffset(value)
}

fun Modifier.outlineStyle(value: LineStyle) = styleModifier {
    outlineStyle(value)
}

fun Modifier.outlineWidth(outlineWidth: OutlineWidth) = styleModifier {
    outlineWidth(outlineWidth)
}

fun Modifier.outlineWidth(value: CSSLengthNumericValue) = styleModifier {
    outlineWidth(value)
}
