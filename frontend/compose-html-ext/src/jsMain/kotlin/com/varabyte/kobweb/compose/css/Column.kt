package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-count
sealed interface ColumnCount : StylePropertyValue {
    companion object : CssGlobalValues<ColumnCount> {
        // Keyword value
        val Auto get() = "auto".unsafeCast<ColumnCount>()

        // <integer> value
        fun of(count: Int) = "$count".unsafeCast<ColumnCount>()

    }
}

fun StyleScope.columnCount(columnCount: ColumnCount) {
    property("column-count", columnCount)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-fill
sealed interface ColumnFill : StylePropertyValue {
    companion object : CssGlobalValues<ColumnFill> {
        // Keyword value
        val Auto get() = "auto".unsafeCast<ColumnFill>()
        val Balance get() = "balance".unsafeCast<ColumnFill>()
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
sealed interface ColumnSpan : StylePropertyValue {
    companion object : CssGlobalValues<ColumnSpan> {
        // Keyword value
        val None get() = "none".unsafeCast<ColumnSpan>()
        val All get() = "all".unsafeCast<ColumnSpan>()
    }
}

fun StyleScope.columnSpan(columnSpan: ColumnSpan) {
    property("column-span", columnSpan)
}
