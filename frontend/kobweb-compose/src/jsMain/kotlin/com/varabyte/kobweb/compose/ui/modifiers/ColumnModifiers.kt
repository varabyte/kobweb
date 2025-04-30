package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.columnCount(columnCount: ColumnCount) = styleModifier {
    columnCount(columnCount)
}

fun Modifier.columnCount(count: Int) = styleModifier {
    columnCount(ColumnCount.of(count))
}

fun Modifier.columnFill(columnFill: ColumnFill) = styleModifier {
    columnFill(columnFill)
}

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

fun Modifier.columnSpan(columnSpan: ColumnSpan) = styleModifier {
    columnSpan(columnSpan)
}
