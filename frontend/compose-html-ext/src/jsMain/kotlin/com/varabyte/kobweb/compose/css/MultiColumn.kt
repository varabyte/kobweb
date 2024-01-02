package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule
class MultiColumn private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {

        // Global values
        val Inherit get() = MultiColumn("inherit")
        val Initial get() = MultiColumn("initial")
        val Revert get() = MultiColumn("revert")

        //        val RevertLayer get() = MultiColumn("revert-layer")
        val Unset get() = MultiColumn("unset")
    }
}

fun StyleScope.multiColumn(multiColumn: MultiColumn) {
    property("column-rule", multiColumn)
}

fun StyleScope.multiColumn(color: CSSColorValue) {
    property("column-rule", color)
}

fun StyleScope.multiColumn(style: LineStyle) {
    property("column-rule", style)
}

fun StyleScope.multiColumn(width: CSSLengthNumericValue) {
    property("column-rule", width)
}

fun StyleScope.multiColumn(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    width?.let {
        multiColumn(it)
    }
    style?.let {
        multiColumn(it)
    }
    color?.let {
        multiColumn(it)
    }
}