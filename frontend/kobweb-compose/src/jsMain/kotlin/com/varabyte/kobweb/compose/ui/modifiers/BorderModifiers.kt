package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.Gradient
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*


class BorderScope internal constructor(private val styleScope: StyleScope) {
    fun color(color: CSSColorValue) = styleScope.borderColor(color)
    fun color(topBottom: CSSColorValue = Color.currentColor, leftRight: CSSColorValue = Color.currentColor) =
        styleScope.borderColor(topBottom, leftRight)

    fun color(
        top: CSSColorValue = Color.currentColor,
        leftRight: CSSColorValue = Color.currentColor,
        bottom: CSSColorValue = Color.currentColor
    ) = styleScope.borderColor(top, leftRight, bottom)

    fun color(
        top: CSSColorValue = Color.currentColor,
        right: CSSColorValue = Color.currentColor,
        bottom: CSSColorValue = Color.currentColor,
        left: CSSColorValue = Color.currentColor
    ) = styleScope.borderColor(top, right, bottom, left)

    fun style(lineStyle: LineStyle) = styleScope.borderStyle(lineStyle)
    fun style(topBottom: LineStyle = LineStyle.None, leftRight: LineStyle = LineStyle.None) =
        styleScope.borderStyle(topBottom, leftRight)

    fun style(
        top: LineStyle = LineStyle.None,
        leftRight: LineStyle = LineStyle.None,
        bottom: LineStyle = LineStyle.None
    ) = styleScope.borderStyle(top, leftRight, bottom)

    fun style(
        top: LineStyle = LineStyle.None,
        right: LineStyle = LineStyle.None,
        bottom: LineStyle = LineStyle.None,
        left: LineStyle = LineStyle.None
    ) = styleScope.borderStyle(top, right, bottom, left)


    fun width(width: CSSLengthNumericValue) = styleScope.borderWidth(width)
    fun width(topBottom: CSSLengthNumericValue, leftRight: CSSLengthNumericValue) =
        styleScope.borderWidth(topBottom, leftRight)

    fun width(top: CSSLengthNumericValue, leftRight: CSSLengthNumericValue, bottom: CSSLengthNumericValue) =
        styleScope.borderWidth(top, leftRight, bottom)

    fun width(
        top: CSSLengthNumericValue,
        right: CSSLengthNumericValue,
        bottom: CSSLengthNumericValue,
        left: CSSLengthNumericValue
    ) = styleScope.borderWidth(top, right, bottom, left)
}

class BorderImageScope internal constructor(private val styleScope: StyleScope) {
    fun source(source: BorderImageSource) = styleScope.borderImageSource(source)
    fun slice(slice: BorderImageSlice) = styleScope.borderImageSlice(slice)
    fun width(width: BorderImageWidth) = styleScope.borderImageWidth(width)
    fun outset(outset: BorderImageOutset) = styleScope.borderImageOutset(outset)
    fun repeat(repeat: BorderImageRepeat) = styleScope.borderImageRepeat(repeat)

    // Helper functions
    fun source(url: CSSUrl) = source(BorderImageSource.of(url))
    fun source(gradient: Gradient) = source(BorderImageSource.of(gradient))
    fun slice(all: CSSPercentageValue) = slice(BorderImageSlice.of(all))
    fun slice(all: Number) = slice(BorderImageSlice.of(all))
    fun slice(block: BorderImageSlice.Builder.() -> Unit) = slice(BorderImageSlice.of(block))
    fun width(all: CSSLengthOrPercentageNumericValue) = width(BorderImageWidth.of(all))
    fun width(all: Number) = width(BorderImageWidth.of(all))
    fun width(block: BorderImageWidth.Builder.() -> Unit) = width(BorderImageWidth.of(block))
    fun outset(all: CSSLengthNumericValue) = outset(BorderImageOutset.of(all))
    fun outset(all: Number) = outset(BorderImageOutset.of(all))
    fun outset(block: BorderImageOutset.Builder.() -> Unit) = outset(BorderImageOutset.of(block))
    fun repeat(topBottom: BorderImageRepeat.Mode, leftRight: BorderImageRepeat.Mode) =
        repeat(BorderImageRepeat.of(topBottom, leftRight))
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
    fun horizontal(topBottom: CSSLengthOrPercentageNumericValue, leftRight: CSSLengthOrPercentageNumericValue) =
        updateHorizontal("$topBottom $leftRight")

    fun horizontal(
        top: CSSLengthOrPercentageNumericValue,
        leftRight: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue
    ) = updateHorizontal("$top $leftRight $bottom")

    fun horizontal(
        top: CSSLengthOrPercentageNumericValue,
        right: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue,
        left: CSSLengthOrPercentageNumericValue
    ) = updateHorizontal("$top $right $bottom $left")

    fun vertical(all: CSSLengthOrPercentageNumericValue) = updateVertical(all.toString())
    fun vertical(topBottom: CSSLengthOrPercentageNumericValue, leftRight: CSSLengthOrPercentageNumericValue) =
        updateVertical("$topBottom $leftRight")

    fun vertical(
        top: CSSLengthOrPercentageNumericValue,
        leftRight: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue
    ) = updateVertical("$top $leftRight $bottom")

    fun vertical(
        top: CSSLengthOrPercentageNumericValue,
        right: CSSLengthOrPercentageNumericValue,
        bottom: CSSLengthOrPercentageNumericValue,
        left: CSSLengthOrPercentageNumericValue
    ) = updateVertical("$top $right $bottom $left")
}

class BorderSideScope internal constructor(private val styleScope: StyleScope, private val side: String) {
    fun color(color: CSSColorValue) = styleScope.property("border$side-color", color)
    fun style(lineStyle: LineStyle) = styleScope.property("border$side-style", lineStyle)
    fun width(width: CSSLengthOrPercentageNumericValue) = styleScope.property("border$side-width", width)
}

fun Modifier.border(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    border(width.unsafeCast<CSSLengthValue>(), style, color)
}

fun Modifier.border(scope: BorderScope.() -> Unit) = styleModifier {
    BorderScope(this).apply(scope)
}

fun Modifier.borderBlockColor(borderBlockColor: BorderBlockColor) = styleModifier {
    borderBlockColor(borderBlockColor)
}

fun Modifier.borderBlockColor(color: CSSColorValue) = styleModifier {
    borderBlockColor(BorderBlockColor.of(color))
}

fun Modifier.borderBlockEndColor(borderBlockEndColor: BorderBlockEndColor) = styleModifier {
    borderBlockEndColor(borderBlockEndColor)
}

fun Modifier.borderBlockEndColor(color: CSSColorValue) = styleModifier {
    borderBlockEndColor(BorderBlockEndColor.of(color))
}

fun Modifier.borderBlockEndStyle(borderBlockEndStyle: BorderBlockEndStyle) = styleModifier {
    borderBlockEndStyle(borderBlockEndStyle)
}

fun Modifier.borderBlockStyle(borderBlockStyle: BorderBlockStyle) = styleModifier {
    borderBlockStyle(borderBlockStyle)
}

fun Modifier.borderBlockStartColor(borderBlockStartColor: BorderBlockStartColor) = styleModifier {
    borderBlockStartColor(borderBlockStartColor)
}

fun Modifier.borderBlockStartColor(color: CSSColorValue) = styleModifier {
    borderBlockStartColor(BorderBlockStartColor.of(color))
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

fun Modifier.borderCollapse(borderCollapse: BorderCollapse) = styleModifier {
    borderCollapse(borderCollapse)
}

fun Modifier.borderImage(image: BorderImage) = styleModifier {
    borderImage(image)
}

fun Modifier.borderImage(scope: BorderImageScope.() -> Unit) = styleModifier {
    BorderImageScope(this).apply(scope)
}

fun Modifier.borderInlineColor(borderInlineColor: BorderInlineColor) = styleModifier {
    borderInlineColor(borderInlineColor)
}

fun Modifier.borderInlineColor(color: CSSColorValue) = styleModifier {
    borderInlineColor(BorderInlineColor.of(color))
}

fun Modifier.borderInlineStyle(borderInlineStyle: BorderInlineStyle) = styleModifier {
    borderInlineStyle(borderInlineStyle)
}

fun Modifier.borderInlineEndColor(borderInlineEndColor: BorderInlineEndColor) = styleModifier {
    borderInlineEndColor(borderInlineEndColor)
}

fun Modifier.borderInlineEndColor(color: CSSColorValue) = styleModifier {
    borderInlineEndColor(BorderInlineEndColor.of(color))
}

fun Modifier.borderInlineEndStyle(borderInlineEndStyle: BorderInlineEndStyle) = styleModifier {
    borderInlineEndStyle(borderInlineEndStyle)
}

fun Modifier.borderInlineStartColor(borderInlineStartColor: BorderInlineStartColor) = styleModifier {
    borderInlineStartColor(borderInlineStartColor)
}

fun Modifier.borderInlineStartColor(color: CSSColorValue) = styleModifier {
    borderInlineStartColor(BorderInlineStartColor.of(color))
}

fun Modifier.borderInlineStartStyle(borderInlineStartStyle: BorderInlineStartStyle) = styleModifier {
    borderInlineStartStyle(borderInlineStartStyle)
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

fun Modifier.borderRadius(scope: BorderRadiusScope.() -> Unit) = styleModifier {
    BorderRadiusScope(this).apply(scope)
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

