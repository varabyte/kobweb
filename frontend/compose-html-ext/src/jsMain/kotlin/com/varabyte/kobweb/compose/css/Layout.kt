package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/aspect-ratio
class AspectRatio private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Auto get() = AspectRatio("auto")

        // Global values
        val Inherit get() = AspectRatio("inherit")
        val Initial get() = AspectRatio("initial")
        val Revert get() = AspectRatio("revert")
        val Unset get() = AspectRatio("unset")
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

    companion object {
        // Keyword values
        val None get() = Clear("none")
        val Left get() = Clear("left")
        val Right get() = Clear("right")
        val Both get() = Clear("both")
        val InlineStart get() = Clear("inline-start")
        val InlineEnd get() = Clear("inline-end")

        // Global values
        val Inherit get() = Clear("inherit")
        val Initial get() = Clear("initial")
        val Revert get() = Clear("revert")
        val Unset get() = Clear("unset")
    }
}

fun StyleScope.clear(clear: Clear) {
    property("clear", clear)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/line-height
class LineHeight private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Normal get() = LineHeight("normal")

        // Global values
        val Inherit get() = LineHeight("inherit")
        val Initial get() = LineHeight("initial")
        val Revert get() = LineHeight("revert")
        val Unset get() = LineHeight("unset")
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

    companion object {
        // Keyword
        val None get() = Resize("none")
        val Both get() = Resize("both")
        val Horizontal get() = Resize("horizontal")
        val Vertical get() = Resize("vertical")
        val Block get() = Resize("block")
        val Inline get() = Resize("inline")

        // Global
        val Inherit get() = Resize("inherit")
        val Initial get() = Resize("initial")
        val Revert get() = Resize("revert")
        val Unset get() = Resize("unset")
    }
}

fun StyleScope.resize(resize: Resize) {
    property("resize", resize)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align
class VerticalAlign private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Baseline get() = VerticalAlign("baseline")
        val Sub get() = VerticalAlign("sub")
        val Super get() = VerticalAlign("super")
        val TextTop get() = VerticalAlign("text-top")
        val TextBottom get() = VerticalAlign("text-bottom")
        val Middle get() = VerticalAlign("middle")
        val Top get() = VerticalAlign("top")
        val Bottom get() = VerticalAlign("bottom")

        // Global
        val Inherit get() = VerticalAlign("inherit")
        val Initial get() = VerticalAlign("initial")
        val Revert get() = VerticalAlign("revert")
        val Unset get() = VerticalAlign("unset")
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
