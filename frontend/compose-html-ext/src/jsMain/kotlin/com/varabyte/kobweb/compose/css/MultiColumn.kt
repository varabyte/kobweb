package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule
class ColumnRule private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {

        // Global values
        val Inherit get() = ColumnRule("inherit")
        val Initial get() = ColumnRule("initial")
        val Revert get() = ColumnRule("revert")
        val Unset get() = ColumnRule("unset")
    }
}

fun StyleScope.columnRule(columnRule: ColumnRule) {
    property("column-rule", columnRule)
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

fun StyleScope.columnRule(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    width?.let {
        columnRuleWidth(it)
    }
    style?.let {
        columnRuleStyle(it)
    }
    color?.let {
        columnRuleColor(it)
    }
}