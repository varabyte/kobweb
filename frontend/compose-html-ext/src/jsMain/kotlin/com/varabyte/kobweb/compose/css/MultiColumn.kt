package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleScope.columnRuleColor(color: CSSColorValue) {
    property("column-rule-color", color)
}

fun StyleScope.columnRuleStyle(style: LineStyle) {
    property("column-rule-style", style)
}

fun StyleScope.columnRuleWidth(width: CSSLengthNumericValue) {
    property("column-rule-width", width)
}

fun StyleScope.columnRule(
    width: CSSLengthNumericValue? = null, style: LineStyle? = null, color: CSSColorValue? = null
) {
    width?.let { columnRuleWidth(it) }
    style?.let { columnRuleStyle(it) }
    color?.let { columnRuleColor(it) }
}
