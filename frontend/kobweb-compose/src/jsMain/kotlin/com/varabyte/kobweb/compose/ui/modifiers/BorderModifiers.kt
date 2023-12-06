package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.border(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    border(width, style, color)
}

class BorderScope internal constructor(private val styleScope: StyleScope) {
    fun color(color: CSSColorValue) = styleScope.borderColor(color)
    fun style(lineStyle: LineStyle) = styleScope.borderStyle(lineStyle)
    fun width(width: CSSLengthValue) = styleScope.borderWidth(width)
    fun width(topAndBottom: CSSLengthValue, leftAndRight: CSSLengthValue) =
        styleScope.borderWidth(topAndBottom, leftAndRight)

    fun width(top: CSSLengthValue, leftAndRight: CSSLengthValue, bottom: CSSLengthValue) =
        styleScope.borderWidth(top, leftAndRight, bottom)

    fun width(top: CSSLengthValue, right: CSSLengthValue, bottom: CSSLengthValue, left: CSSLengthValue) =
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
    fun width(width: CSSLengthOrPercentageValue) = styleScope.property("border$side-width", width)
}

fun Modifier.borderTop(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderTop(width, style, color)
}

fun Modifier.borderTop(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-top").apply(scope)
}

fun Modifier.borderBottom(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderBottom(width, style, color)
}

fun Modifier.borderBottom(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-bottom").apply(scope)
}

fun Modifier.borderLeft(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderLeft(width, style, color)
}

fun Modifier.borderLeft(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-left").apply(scope)
}

fun Modifier.borderRight(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderRight(width, style, color)
}

fun Modifier.borderRight(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-right").apply(scope)
}

fun Modifier.borderRadius(r: CSSLengthOrPercentageValue) = styleModifier {
    borderRadius(r)
}

fun Modifier.borderRadius(
    topLeftAndBottomRight: CSSLengthOrPercentageValue = 0.px,
    topRightAndBottomLeft: CSSLengthOrPercentageValue = 0.px
) = styleModifier {
    borderRadius(topLeftAndBottomRight, topRightAndBottomLeft)
}

fun Modifier.borderRadius(
    topLeft: CSSLengthOrPercentageValue = 0.px,
    topRightAndBottomLeft: CSSLengthOrPercentageValue = 0.px,
    bottomRight: CSSLengthOrPercentageValue = 0.px,
) = styleModifier {
    borderRadius(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderRadius(
    topLeft: CSSLengthOrPercentageValue = 0.px,
    topRight: CSSLengthOrPercentageValue = 0.px,
    bottomRight: CSSLengthOrPercentageValue = 0.px,
    bottomLeft: CSSLengthOrPercentageValue = 0.px,
) = styleModifier {
    borderRadius(topLeft, topRight, bottomRight, bottomLeft)
}

@Deprecated("Use border { style(...) } instead.", ReplaceWith("border { style(lineStyle) }"))
fun Modifier.borderStyle(lineStyle: LineStyle) = border { style(lineStyle) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(width) }"))
fun Modifier.borderWidth(width: CSSLengthValue) = border { width(width) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(topAndBottom, leftAndRight) }"))
fun Modifier.borderWidth(topAndBottom: CSSLengthValue, leftAndRight: CSSLengthValue) = border {
    width(topAndBottom, leftAndRight)
}

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(top, leftAndRight, bottom) }"))
fun Modifier.borderWidth(
    top: CSSLengthValue,
    leftAndRight: CSSLengthValue,
    bottom: CSSLengthValue
) = border { width(top, leftAndRight, bottom) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(top, right, bottom, left) }"))
fun Modifier.borderWidth(
    top: CSSLengthValue,
    right: CSSLengthValue,
    bottom: CSSLengthValue,
    left: CSSLengthValue
) = border { width(top, right, bottom, left) }
