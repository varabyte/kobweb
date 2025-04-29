package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-count
class ColumnCount private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ColumnCount> {
        // Keyword value
        val Auto get() = ColumnCount("auto")

        // <integer> value
        fun of(count: Int) = ColumnCount("$count")

    }
}

fun StyleScope.columnCount(columnCount: ColumnCount) {
    property("column-count", columnCount)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-fill
class ColumnFill private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ColumnFill> {
        // Keyword value
        val Auto get() = ColumnFill("auto")
        val Balance get() = ColumnFill("balance")
    }
}

fun StyleScope.columnFill(columnFill: ColumnFill) {
    property("column-fill", columnFill)
}

fun StyleScope.columnRuleColor(color: CSSColorValue) {
    property("column-rule-color", color)
}

fun StyleScope.columnRuleStyle(style: LineStyle) {
    property("column-rule-style", style)
}

fun StyleScope.columnRuleWidth(width: CSSLengthNumericValue) {
    property("column-rule-width", width)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule
fun StyleScope.columnRule(
    width: CSSLengthNumericValue? = null, style: LineStyle? = null, color: CSSColorValue? = null
) {
    property("column-rule", listOfNotNull(width, style, color).joinToString(" "))
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-span
class ColumnSpan private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<ColumnSpan> {
        // Keyword value
        val None get() = ColumnSpan("none")
        val All get() = ColumnSpan("all")
    }
}

fun StyleScope.columnSpan(columnSpan: ColumnSpan) {
    property("column-span", columnSpan)
}
