package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.border(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    border(width.unsafeCast<CSSLengthValue>(), style, color)
}

class BorderScope internal constructor(private val styleScope: StyleScope) {
    fun color(color: CSSColorValue) = styleScope.borderColor(color)
    fun style(lineStyle: LineStyle) = styleScope.borderStyle(lineStyle)
    fun width(width: CSSLengthNumericValue) = styleScope.borderWidth(width)
    fun width(topAndBottom: CSSLengthNumericValue, leftAndRight: CSSLengthNumericValue) =
        styleScope.borderWidth(topAndBottom, leftAndRight)

    fun width(top: CSSLengthNumericValue, leftAndRight: CSSLengthNumericValue, bottom: CSSLengthNumericValue) =
        styleScope.borderWidth(top, leftAndRight, bottom)

    fun width(
        top: CSSLengthNumericValue,
        right: CSSLengthNumericValue,
        bottom: CSSLengthNumericValue,
        left: CSSLengthNumericValue
    ) =
        styleScope.borderWidth(top, right, bottom, left)
}

fun Modifier.border(scope: BorderScope.() -> Unit) = styleModifier {
    BorderScope(this).apply(scope)
}

fun Modifier.borderCollapse(borderCollapse: BorderCollapse) = styleModifier {
    borderCollapse(borderCollapse)
}

@Deprecated("Use border { color(...) } instead.", ReplaceWith("border { color(color) }"))
fun Modifier.borderColor(color: CSSColorValue) = border { color(color) }

class BorderSideScope internal constructor(private val styleScope: StyleScope, private val side: String) {
    fun color(color: CSSColorValue) = styleScope.property("border$side-color", color)
    fun style(lineStyle: LineStyle) = styleScope.property("border$side-style", lineStyle)
    fun width(width: CSSLengthOrPercentageNumericValue) = styleScope.property("border$side-width", width)
}

fun Modifier.borderTop(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderTop(width, style, color)
}

fun Modifier.borderTop(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-top").apply(scope)
}

fun Modifier.borderBottom(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderBottom(width, style, color)
}

fun Modifier.borderBottom(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-bottom").apply(scope)
}

fun Modifier.borderLeft(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderLeft(width, style, color)
}

fun Modifier.borderLeft(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-left").apply(scope)
}

fun Modifier.borderRight(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderRight(width, style, color)
}

fun Modifier.borderRight(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-right").apply(scope)
}

fun Modifier.borderRadius(r: CSSLengthOrPercentageNumericValue) = styleModifier {
    borderRadius(r)
}

fun Modifier.borderRadius(
    topLeftAndBottomRight: CSSLengthOrPercentageNumericValue = 0.px,
    topRightAndBottomLeft: CSSLengthOrPercentageNumericValue = 0.px
) = styleModifier {
    borderRadius(topLeftAndBottomRight, topRightAndBottomLeft)
}

fun Modifier.borderRadius(
    topLeft: CSSLengthOrPercentageNumericValue = 0.px,
    topRightAndBottomLeft: CSSLengthOrPercentageNumericValue = 0.px,
    bottomRight: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    borderRadius(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderRadius(
    topLeft: CSSLengthOrPercentageNumericValue = 0.px,
    topRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottomRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottomLeft: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    borderRadius(topLeft, topRight, bottomRight, bottomLeft)
}

class BorderRadiusScope internal constructor(private val styleScope: StyleScope) {
    fun topLeft(r: CSSLengthOrPercentageNumericValue) = styleScope.borderTopLeftRadius(r)
    fun topLeft(horizontal: CSSLengthOrPercentageNumericValue, vertical: CSSLengthOrPercentageNumericValue) =
        styleScope.borderTopLeftRadius(horizontal, vertical)

    fun topRight(r: CSSLengthOrPercentageNumericValue) = styleScope.borderTopRightRadius(r)
    fun topRight(horizontal: CSSLengthOrPercentageNumericValue, vertical: CSSLengthOrPercentageNumericValue) =
        styleScope.borderTopRightRadius(horizontal, vertical)

    fun bottomRight(r: CSSLengthOrPercentageNumericValue) = styleScope.borderBottomRightRadius(r)
    fun bottomRight(horizontal: CSSLengthOrPercentageNumericValue, vertical: CSSLengthOrPercentageNumericValue) =
        styleScope.borderBottomRightRadius(horizontal, vertical)

    fun bottomLeft(r: CSSLengthOrPercentageNumericValue) = styleScope.borderBottomLeftRadius(r)
    fun bottomLeft(horizontal: CSSLengthOrPercentageNumericValue, vertical: CSSLengthOrPercentageNumericValue) =
        styleScope.borderBottomLeftRadius(horizontal, vertical)

    // store values of each axis since they are set independently but are applied together as a single property
    private var currentHorizontal = 0.px.toString()
    private var currentVertical = 0.px.toString()

    private fun updateHorizontal(value: String) {
        currentHorizontal = value
        styleScope.property("border-radius", "$currentHorizontal / $currentVertical")
    }

    private fun updateVertical(value: String) {
        currentVertical = value
        styleScope.property("border-radius", "$currentHorizontal / $currentVertical")
    }

    fun horizontal(all: CSSLengthOrPercentageNumericValue) = updateHorizontal(all.toString())
    fun horizontal(topAndBottom: CSSLengthOrPercentageNumericValue, leftAndRight: CSSLengthOrPercentageNumericValue) =
        updateHorizontal("$topAndBottom $leftAndRight")

    fun horizontal(
        top: CSSLengthOrPercentageNumericValue,
        leftAndRight: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue
    ) = updateHorizontal("$top $leftAndRight $bottom")

    fun horizontal(
        top: CSSLengthOrPercentageNumericValue,
        right: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue,
        left: CSSLengthOrPercentageNumericValue
    ) = updateHorizontal("$top $right $bottom $left")

    fun vertical(all: CSSLengthOrPercentageNumericValue) = updateVertical(all.toString())
    fun vertical(topAndBottom: CSSLengthOrPercentageNumericValue, leftAndRight: CSSLengthOrPercentageNumericValue) =
        updateVertical("$topAndBottom $leftAndRight")

    fun vertical(
        top: CSSLengthOrPercentageNumericValue,
        leftAndRight: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue
    ) = updateVertical("$top $leftAndRight $bottom")

    fun vertical(
        top: CSSLengthOrPercentageNumericValue,
        right: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue,
        left: CSSLengthOrPercentageNumericValue
    ) = updateVertical("$top $right $bottom $left")
}

fun Modifier.borderRadius(scope: BorderRadiusScope.() -> Unit) = styleModifier {
    BorderRadiusScope(this).apply(scope)
}

@Deprecated("Use border { style(...) } instead.", ReplaceWith("border { style(lineStyle) }"))
fun Modifier.borderStyle(lineStyle: LineStyle) = border { style(lineStyle) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(width) }"))
fun Modifier.borderWidth(width: CSSLengthNumericValue) = border { width(width) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(topAndBottom, leftAndRight) }"))
fun Modifier.borderWidth(topAndBottom: CSSLengthNumericValue, leftAndRight: CSSLengthNumericValue) = border {
    width(topAndBottom, leftAndRight)
}

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(top, leftAndRight, bottom) }"))
fun Modifier.borderWidth(
    top: CSSLengthNumericValue,
    leftAndRight: CSSLengthNumericValue,
    bottom: CSSLengthNumericValue
) = border { width(top, leftAndRight, bottom) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(top, right, bottom, left) }"))
fun Modifier.borderWidth(
    top: CSSLengthNumericValue,
    right: CSSLengthNumericValue,
    bottom: CSSLengthNumericValue,
    left: CSSLengthNumericValue
) = border { width(top, right, bottom, left) }
