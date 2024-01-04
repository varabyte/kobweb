package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.columnRule(columnRule: ColumnRule) = styleModifier {
    columnRule(columnRule)
}

fun Modifier.columnRuleColor(color: CSSColorValue) = styleModifier {
    columnRuleColor(color)
}

fun Modifier.columnRuleStyle(style: LineStyle) = styleModifier {
    columnRuleStyle(style)
}

fun Modifier.columnRuleWidth(width: CSSLengthNumericValue) = styleModifier {
    columnRuleWidth(width)
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