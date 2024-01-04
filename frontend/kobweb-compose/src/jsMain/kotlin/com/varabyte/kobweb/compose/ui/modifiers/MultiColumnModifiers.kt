package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.columnRule(columnRule: ColumnRule) = styleModifier {
    columnRule(columnRule)
}

fun Modifier.columnRule(color: CSSColorValue) = styleModifier {
    columnRule(color)
}

fun Modifier.columnRule(style: LineStyle) = styleModifier {
    columnRule(style)
}

fun Modifier.columnRule(width: CSSLengthNumericValue) = styleModifier {
    columnRule(width)
}

fun Modifier.columnRule(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    columnRule(
        width, style, color
    )
}