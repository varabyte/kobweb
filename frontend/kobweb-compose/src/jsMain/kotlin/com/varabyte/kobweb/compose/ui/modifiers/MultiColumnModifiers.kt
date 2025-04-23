package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

class ColumnRuleScope internal constructor(private val styleScope: StyleScope) {
    fun color(color: CSSColorValue) = styleScope.columnRuleColor(color)
    fun style(lineStyle: LineStyle) = styleScope.columnRuleStyle(lineStyle)
    fun width(width: CSSLengthNumericValue) = styleScope.columnRuleWidth(width)
}

fun Modifier.columnRule(scope: ColumnRuleScope.() -> Unit) = styleModifier {
    ColumnRuleScope(this).apply(scope)
}

fun Modifier.columnRule(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    columnRule(width, style, color)
}
