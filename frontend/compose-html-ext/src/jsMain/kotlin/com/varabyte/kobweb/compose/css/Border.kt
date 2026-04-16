package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CSSImage
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-color
sealed interface BorderBlockColor : StylePropertyValue {
    companion object : CssGlobalValues<BorderBlockColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<BorderBlockColor>()
    }
}

fun StyleScope.borderBlockColor(borderBlockColor: BorderBlockColor) {
    property("border-block-color", borderBlockColor)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-color
sealed interface BorderBlockEndColor : StylePropertyValue {
    companion object : CssGlobalValues<BorderBlockEndColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<BorderBlockEndColor>()
    }
}

fun StyleScope.borderBlockEndColor(borderBlockEndColor: BorderBlockEndColor) {
    property("border-block-end-color", borderBlockEndColor)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-style
sealed interface BorderBlockEndStyle : StylePropertyValue {
    companion object : CssGlobalValues<BorderBlockEndStyle>, CommonBorderStyle<BorderBlockEndStyle>
}

fun StyleScope.borderBlockEndStyle(borderBlockStartStyle: BorderBlockEndStyle) {
    property("border-block-end-style", borderBlockStartStyle)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-color
sealed interface BorderBlockStartColor : StylePropertyValue {
    companion object : CssGlobalValues<BorderBlockStartColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<BorderBlockStartColor>()
    }
}

fun StyleScope.borderBlockStartColor(borderBlockEndColor: BorderBlockStartColor) {
    property("border-block-start-color", borderBlockEndColor)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-style
sealed interface BorderBlockStartStyle : StylePropertyValue {
    companion object : CssGlobalValues<BorderBlockStartStyle>, CommonBorderStyle<BorderBlockStartStyle>
}

fun StyleScope.borderBlockStartStyle(borderBlockStartStyle: BorderBlockStartStyle) {
    property("border-block-start-style", borderBlockStartStyle)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-style
sealed interface BorderBlockStyle : StylePropertyValue {
    companion object : CssGlobalValues<BorderBlockStyle>, CommonBorderStyle<BorderBlockStyle>
}

fun StyleScope.borderBlockStyle(borderBlockStyle: BorderBlockStyle) {
    property("border-block-style", borderBlockStyle)
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

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-collapse
sealed interface BorderCollapse : StylePropertyValue {
    companion object : CssGlobalValues<BorderCollapse> {
        // Keyword
        val Separate get() = "separate".unsafeCast<BorderCollapse>()
        val Collapse get() = "collapse".unsafeCast<BorderCollapse>()
    }
}

fun StyleScope.borderCollapse(borderCollapse: BorderCollapse) {
    property("border-collapse", borderCollapse)
}

fun StyleScope.borderColor(color: CSSColorValue) {
    property("border-color", color)
}

fun StyleScope.borderColor(
    topBottom: CSSColorValue = Color.currentColor,
    leftRight: CSSColorValue = Color.currentColor
) {
    property("border-color", "$topBottom $leftRight")
}

fun StyleScope.borderColor(
    top: CSSColorValue = Color.currentColor,
    leftRight: CSSColorValue = Color.currentColor,
    bottom: CSSColorValue = Color.currentColor
) {
    property("border-color", "$top $leftRight $bottom")
}

fun StyleScope.borderColor(
    top: CSSColorValue = Color.currentColor,
    right: CSSColorValue = Color.currentColor,
    bottom: CSSColorValue = Color.currentColor,
    left: CSSColorValue = Color.currentColor
) {
    property("border-color", "$top $right $bottom $left")
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

// Helper class to reduce duplication between BorderImageOutset, BorderImageSlice, & BorderImageWidth
// Cannot be made private as its API is exposed in the builders, but it is sealed to disallow external inheritance
sealed class BorderImageNumericBuilder<T : CSSNumericValue<*>> {
    protected var top = 0.toString()
    protected var right = 0.toString()
    protected var bottom = 0.toString()
    protected var left = 0.toString()

    fun top(value: T) {
        top = value.toString()
    }

    fun top(value: Number) {
        top = value.toString()
    }

    fun right(value: T) {
        right = value.toString()
    }

    fun right(value: Number) {
        right = value.toString()
    }

    fun bottom(value: T) {
        bottom = value.toString()
    }

    fun bottom(value: Number) {
        bottom = value.toString()
    }

    fun left(value: T) {
        left = value.toString()
    }

    fun left(value: Number) {
        left = value.toString()
    }

    fun topBottom(value: T) {
        top = value.toString()
        bottom = value.toString()
    }

    fun topBottom(value: Number) {
        top = value.toString()
        bottom = value.toString()
    }

    fun leftRight(value: T) {
        left = value.toString()
        right = value.toString()
    }

    fun leftRight(value: Number) {
        left = value.toString()
        right = value.toString()
    }

    fun all(value: T) {
        top = value.toString()
        right = value.toString()
        bottom = value.toString()
        left = value.toString()
    }

    fun all(value: Number) {
        top = value.toString()
        right = value.toString()
        bottom = value.toString()
        left = value.toString()
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-source
typealias BorderImageSource = CSSImage

fun StyleScope.borderImageSource(source: BorderImageSource) {
    property("border-image-source", source)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice
sealed interface BorderImageSlice : StylePropertyValue {
    class Builder internal constructor() : BorderImageNumericBuilder<CSSPercentageValue>() {
        private var fill = false
        fun fill() {
            fill = true
        }

        internal fun build(): BorderImageSlice =
            listOfNotNull(top, right, bottom, left, if (fill) "fill" else null)
                .joinToString(" ").unsafeCast<BorderImageSlice>()
    }

    companion object : CssGlobalValues<BorderImageSlice> {
        fun of(all: CSSPercentageValue) = of { all(all) }
        fun of(all: Number) = of { all(all) }
        fun of(block: Builder.() -> Unit): BorderImageSlice = Builder().apply(block).build()
    }
}

fun StyleScope.borderImageSlice(slice: BorderImageSlice) {
    property("border-image-slice", slice)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-width
sealed interface BorderImageWidth : StylePropertyValue {
    class Builder internal constructor() : BorderImageNumericBuilder<CSSLengthOrPercentageNumericValue>() {
        internal fun build(): BorderImageWidth = "$top $right $bottom $left".unsafeCast<BorderImageWidth>()
    }

    companion object : CssGlobalValues<BorderImageWidth> {
        fun of(all: CSSLengthOrPercentageNumericValue) = of { all(all) }
        fun of(all: Number) = of { all(all) }
        fun of(block: Builder.() -> Unit): BorderImageWidth = Builder().apply(block).build()
    }
}

fun StyleScope.borderImageWidth(width: BorderImageWidth) {
    property("border-image-width", width)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-outset
sealed interface BorderImageOutset : StylePropertyValue {
    class Builder internal constructor() : BorderImageNumericBuilder<CSSLengthNumericValue>() {
        internal fun build(): BorderImageOutset = "$top $right $bottom $left".unsafeCast<BorderImageOutset>()
    }

    companion object : CssGlobalValues<BorderImageOutset> {
        fun of(all: CSSLengthNumericValue) = of { all(all) }
        fun of(all: Number) = of { all(all) }
        fun of(block: Builder.() -> Unit): BorderImageOutset = Builder().apply(block).build()
    }
}

fun StyleScope.borderImageOutset(outset: BorderImageOutset) {
    property("border-image-outset", outset)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-repeat
sealed interface BorderImageRepeat : StylePropertyValue {
    sealed interface Mode : BorderImageRepeat

    companion object : CssGlobalValues<BorderImageRepeat> {
        // Keyword
        val Stretch get() = "stretch".unsafeCast<Mode>()
        val Repeat get() = "repeat".unsafeCast<Mode>()
        val Round get() = "round".unsafeCast<Mode>()
        val Space get() = "space".unsafeCast<Mode>()

        fun of(topBottom: Mode, leftRight: Mode) = "$topBottom $leftRight".unsafeCast<BorderImageRepeat>()
    }
}

fun StyleScope.borderImageRepeat(repeat: BorderImageRepeat) {
    property("border-image-repeat", repeat)
}

sealed interface BorderImage : StylePropertyValue {
    companion object : CssGlobalValues<BorderImage> {
        fun of(
            source: BorderImageSource? = null,
            slice: BorderImageSlice? = null,
            width: BorderImageWidth? = null,
            outset: BorderImageOutset? = null,
            repeat: BorderImageRepeat? = null,
        ) = buildString {
            source?.let { append("$it ") }
            if (slice != null || width != null || outset != null) {
                // width requires slice to be present, and outset requires width to be present
                append("${slice ?: BorderImageSlice.of(100.percent)} ")
                if (outset != null || width != null) {
                    append("/ ${width ?: BorderImageWidth.of(1)} ")
                    outset?.let { append("/ $it ") }
                }
            }
            repeat?.let { append(it) }
        }.unsafeCast<BorderImage>()
    }
}

fun StyleScope.borderImage(borderImage: BorderImage) {
    property("border-image", borderImage)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-color
sealed interface BorderInlineColor : StylePropertyValue {
    companion object : CssGlobalValues<BorderInlineColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<BorderInlineColor>()
    }
}

fun StyleScope.borderInlineColor(borderInlineColor: BorderInlineColor) {
    property("border-inline-color", borderInlineColor)
}

// See:https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-style
sealed interface BorderInlineStyle: StylePropertyValue {
    companion object: CssGlobalValues<BorderInlineStyle>, CommonBorderStyle<BorderInlineStyle>
}

fun StyleScope.borderInlineStyle(borderInlineStyle: BorderInlineStyle){
    property("border-inline-style", borderInlineStyle)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-color
sealed interface BorderInlineEndColor : StylePropertyValue {
    companion object : CssGlobalValues<BorderInlineEndColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<BorderInlineEndColor>()
    }
}

fun StyleScope.borderInlineEndColor(borderInlineEndColor: BorderInlineEndColor) {
    property("border-inline-end-color", borderInlineEndColor)
}

// See:https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-style
sealed interface BorderInlineEndStyle: StylePropertyValue {
    companion object: CssGlobalValues<BorderInlineEndStyle>, CommonBorderStyle<BorderInlineEndStyle>
}

fun StyleScope.borderInlineEndStyle(borderInlineEndStyle: BorderInlineEndStyle){
    property("border-inline-end-style", borderInlineEndStyle)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-color
sealed interface BorderInlineStartColor : StylePropertyValue {
    companion object : CssGlobalValues<BorderInlineStartColor> {
        fun of(color: CSSColorValue) = color.unsafeCast<BorderInlineStartColor>()
    }
}

fun StyleScope.borderInlineStartColor(borderInlineStartColor: BorderInlineStartColor) {
    property("border-inline-start-color", borderInlineStartColor)
}

// See:https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-style
sealed interface BorderInlineStartStyle: StylePropertyValue {
    companion object: CssGlobalValues<BorderInlineStartStyle>, CommonBorderStyle<BorderInlineStartStyle>
}

fun StyleScope.borderInlineStartStyle(borderInlineStartStyle: BorderInlineStartStyle){
    property("border-inline-start-style", borderInlineStartStyle)
}

fun StyleScope.borderStyle(lineStyle: LineStyle) {
    property("border-style", lineStyle.value)
}

fun StyleScope.borderStyle(topBottom: LineStyle = LineStyle.None, leftRight: LineStyle = LineStyle.None) {
    property("border-style", "$topBottom $leftRight")
}

fun StyleScope.borderStyle(
    top: LineStyle = LineStyle.None,
    leftRight: LineStyle = LineStyle.None,
    bottom: LineStyle = LineStyle.None
) {
    property("border-style", "$top $leftRight $bottom")
}

fun StyleScope.borderStyle(
    top: LineStyle = LineStyle.None,
    right: LineStyle = LineStyle.None,
    bottom: LineStyle = LineStyle.None,
    left: LineStyle = LineStyle.None
) {
    property("border-style", "$top $right $bottom $left")
}

fun StyleScope.borderWidth(width: CSSLengthNumericValue) {
    property("border-width", width)
}

internal sealed interface CommonBorderStyle<T : StylePropertyValue> {
    val None get() = "none".unsafeCast<T>()
    val Hidden get() = "hidden".unsafeCast<T>()
    val Dotted get() = "dotted".unsafeCast<T>()
    val Dashed get() = "dashed".unsafeCast<T>()
    val Solid get() = "solid".unsafeCast<T>()
    val Double get() = "double".unsafeCast<T>()
    val Groove get() = "groove".unsafeCast<T>()
    val Ridge get() = "ridge".unsafeCast<T>()
    val Inset get() = "inset".unsafeCast<T>()
    val Outset get() = "outset".unsafeCast<T>()
}