package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/aspect-ratio
class AspectRatio private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<AspectRatio> {
        // Keywords
        val Auto get() = AspectRatio("auto")
    }
}

fun StyleScope.aspectRatio(ratio: Number) {
    property("aspect-ratio", ratio)
}

fun StyleScope.aspectRatio(width: Number, height: Number) {
    property("aspect-ratio", "$width / $height")
}

fun StyleScope.aspectRatio(aspectRatio: AspectRatio) {
    property("aspect-ratio", aspectRatio)
}

// See https://developer.mozilla.org/en-US/docs/Web/CSS/clear
class Clear private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Clear> {
        // Keyword values
        val None get() = Clear("none")
        val Left get() = Clear("left")
        val Right get() = Clear("right")
        val Both get() = Clear("both")
        val InlineStart get() = Clear("inline-start")
        val InlineEnd get() = Clear("inline-end")
    }
}

fun StyleScope.clear(clear: Clear) {
    property("clear", clear)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/line-height
class LineHeight private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<LineHeight> {
        // Keywords
        val Normal get() = LineHeight("normal")
    }
}

fun StyleScope.lineHeight(lineHeight: LineHeight) {
    property("line-height", lineHeight)
}

// region margin-block See: https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block

fun StyleScope.marginBlock(both: CSSLengthOrPercentageNumericValue) {
    property("margin-block", both)
}

fun StyleScope.marginBlock(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) {
    property("margin-block", "$start $end")
}

fun StyleScope.marginBlockStart(value: CSSLengthOrPercentageNumericValue) {
    property("margin-block-start", value)
}

fun StyleScope.marginBlockEnd(value: CSSLengthOrPercentageNumericValue) {
    property("margin-block-end", value)
}

// endregion

// region margin-inline See https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline

fun StyleScope.marginInline(both: CSSLengthOrPercentageNumericValue) {
    property("margin-inline", both)
}

fun StyleScope.marginInline(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) {
    property("margin-inline", "$start $end")
}

fun StyleScope.marginInlineStart(value: CSSLengthOrPercentageNumericValue) {
    property("margin-inline-start", value)
}

fun StyleScope.marginInlineEnd(value: CSSLengthOrPercentageNumericValue) {
    property("margin-inline-end", value)
}

// endregion


// region padding-inline See: https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline

fun StyleScope.paddingInline(both: CSSLengthOrPercentageNumericValue) {
    property("padding-inline", both)
}

fun StyleScope.paddingInline(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) {
    property("padding-inline", "$start $end")
}

fun StyleScope.paddingInlineStart(value: CSSLengthOrPercentageNumericValue) {
    property("padding-inline-start", value)
}

fun StyleScope.paddingInlineEnd(value: CSSLengthOrPercentageNumericValue) {
    property("padding-inline-end", value)
}

// endregion

// region padding-block See: https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block

fun StyleScope.paddingBlock(both: CSSLengthOrPercentageNumericValue) {
    property("padding-block", both)
}

fun StyleScope.paddingBlock(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px
) {
    property("padding-block", "$start $end")
}

fun StyleScope.paddingBlockStart(value: CSSLengthOrPercentageNumericValue) {
    property("padding-block-start", value)
}

fun StyleScope.paddingBlockEnd(value: CSSLengthOrPercentageNumericValue) {
    property("padding-block-end", value)
}

// endregion

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/resize
class Resize private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Resize> {
        // Keyword
        val None get() = Resize("none")
        val Both get() = Resize("both")
        val Horizontal get() = Resize("horizontal")
        val Vertical get() = Resize("vertical")
        val Block get() = Resize("block")
        val Inline get() = Resize("inline")
    }
}

fun StyleScope.resize(resize: Resize) {
    property("resize", resize)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align
class VerticalAlign private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<VerticalAlign> {
        // Keyword
        val Baseline get() = VerticalAlign("baseline")
        val Sub get() = VerticalAlign("sub")
        val Super get() = VerticalAlign("super")
        val TextTop get() = VerticalAlign("text-top")
        val TextBottom get() = VerticalAlign("text-bottom")
        val Middle get() = VerticalAlign("middle")
        val Top get() = VerticalAlign("top")
        val Bottom get() = VerticalAlign("bottom")
    }
}

fun StyleScope.verticalAlign(verticalAlign: VerticalAlign) {
    property("vertical-align", verticalAlign)
}

fun StyleScope.verticalAlign(value: CSSLengthOrPercentageNumericValue) {
    property("vertical-align", value)
}

// region z-index See: https://developer.mozilla.org/en-US/docs/Web/CSS/z-index

fun StyleScope.zIndex(value: Number) {
    property("z-index", value)
}

// endregion
