package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleScope.borderStyle(lineStyle: LineStyle) {
    property("border-style", lineStyle.value)
}

fun StyleScope.borderWidth(width: CSSLengthNumericValue) {
    property("border-width", width)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-collapse
class BorderCollapse private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Separate get() = BorderCollapse("separate")
        val Collapse get() = BorderCollapse("collapse")

        // Global
        val Inherit get() = BorderCollapse("inherit")
        val Initial get() = BorderCollapse("initial")
        val Revert get() = BorderCollapse("revert")
        val Unset get() = BorderCollapse("unset")
    }
}

fun StyleScope.borderCollapse(borderCollapse: BorderCollapse) {
    property("border-collapse", borderCollapse)
}

fun StyleScope.borderColor(color: CSSColorValue) {
    property("border-color", color)
}

fun StyleScope.borderTop(borderBuild: CSSBorder.() -> Unit) {
    property("border-top", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderTop(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderTop {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

fun StyleScope.borderBottom(borderBuild: CSSBorder.() -> Unit) {
    property("border-bottom", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderBottom(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderBottom {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

fun StyleScope.borderLeft(borderBuild: CSSBorder.() -> Unit) {
    property("border-left", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderLeft(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderLeft {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

fun StyleScope.borderRight(borderBuild: CSSBorder.() -> Unit) {
    property("border-right", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderRight(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderRight {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

fun StyleScope.borderTopLeftRadius(radius: CSSLengthOrPercentageNumericValue) {
    property("border-top-left-radius", radius)
}

fun StyleScope.borderTopLeftRadius(
    horizontal: CSSLengthOrPercentageNumericValue = 0.px,
    vertical: CSSLengthOrPercentageNumericValue = 0.px
) {
    property("border-top-left-radius", "$horizontal $vertical")
}

fun StyleScope.borderTopRightRadius(radius: CSSLengthOrPercentageNumericValue) {
    property("border-top-right-radius", radius)
}

fun StyleScope.borderTopRightRadius(
    horizontal: CSSLengthOrPercentageNumericValue = 0.px,
    vertical: CSSLengthOrPercentageNumericValue = 0.px
) {
    property("border-top-right-radius", "$horizontal $vertical")
}

fun StyleScope.borderBottomLeftRadius(radius: CSSLengthOrPercentageNumericValue) {
    property("border-bottom-left-radius", radius)
}

fun StyleScope.borderBottomLeftRadius(
    horizontal: CSSLengthOrPercentageNumericValue = 0.px,
    vertical: CSSLengthOrPercentageNumericValue = 0.px
) {
    property("border-bottom-left-radius", "$horizontal $vertical")
}

fun StyleScope.borderBottomRightRadius(radius: CSSLengthOrPercentageNumericValue) {
    property("border-bottom-right-radius", radius)
}

fun StyleScope.borderBottomRightRadius(
    horizontal: CSSLengthOrPercentageNumericValue = 0.px,
    vertical: CSSLengthOrPercentageNumericValue = 0.px
) {
    property("border-bottom-right-radius", "$horizontal $vertical")
}
