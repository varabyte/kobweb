package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/aspect-ratio
sealed interface AspectRatio : StylePropertyValue {
    companion object : CssGlobalValues<AspectRatio> {
        fun of(ratio: Number) = "$ratio".unsafeCast<AspectRatio>()
        fun of(width: Number, height: Number) = "$width / $height".unsafeCast<AspectRatio>()

        // Keywords
        val Auto get() = "auto".unsafeCast<AspectRatio>()
    }
}

@Deprecated("Use `aspectRatio(AspectRatio.of(ratio))` instead", ReplaceWith("aspectRatio(AspectRatio.of(ratio))"))
fun StyleScope.aspectRatio(ratio: Number) {
    aspectRatio(AspectRatio.of(ratio))
}

@Deprecated("Use `aspectRation(AspectRatio.of(width, height))` instead", ReplaceWith("aspectRatio(AspectRatio.of(width, height))"))
fun StyleScope.aspectRatio(width: Number, height: Number) {
    aspectRatio(AspectRatio.of(width, height))
}

fun StyleScope.aspectRatio(aspectRatio: AspectRatio) {
    property("aspect-ratio", aspectRatio)
}

// See https://developer.mozilla.org/en-US/docs/Web/CSS/clear
sealed interface Clear : StylePropertyValue {
    companion object : CssGlobalValues<Clear> {
        // Keyword values
        val None get() = "none".unsafeCast<Clear>()
        val Left get() = "left".unsafeCast<Clear>()
        val Right get() = "right".unsafeCast<Clear>()
        val Both get() = "both".unsafeCast<Clear>()
        val InlineStart get() = "inline-start".unsafeCast<Clear>()
        val InlineEnd get() = "inline-end".unsafeCast<Clear>()
    }
}

fun StyleScope.clear(clear: Clear) {
    property("clear", clear)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/line-height
sealed interface LineHeight : StylePropertyValue {
    companion object : CssGlobalValues<LineHeight> {
        // Keywords
        val Normal get() = "normal".unsafeCast<LineHeight>()
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
sealed interface Resize : StylePropertyValue {
    companion object : CssGlobalValues<Resize> {
        // Keyword
        val None get() = "none".unsafeCast<Resize>()
        val Both get() = "both".unsafeCast<Resize>()
        val Horizontal get() = "horizontal".unsafeCast<Resize>()
        val Vertical get() = "vertical".unsafeCast<Resize>()
        val Block get() = "block".unsafeCast<Resize>()
        val Inline get() = "inline".unsafeCast<Resize>()
    }
}

fun StyleScope.resize(resize: Resize) {
    property("resize", resize)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align
sealed interface VerticalAlign : StylePropertyValue {
    companion object : CssGlobalValues<VerticalAlign> {
        // Keyword
        val Baseline get() = "baseline".unsafeCast<VerticalAlign>()
        val Sub get() = "sub".unsafeCast<VerticalAlign>()
        val Super get() = "super".unsafeCast<VerticalAlign>()
        val TextTop get() = "text-top".unsafeCast<VerticalAlign>()
        val TextBottom get() = "text-bottom".unsafeCast<VerticalAlign>()
        val Middle get() = "middle".unsafeCast<VerticalAlign>()
        val Top get() = "top".unsafeCast<VerticalAlign>()
        val Bottom get() = "bottom".unsafeCast<VerticalAlign>()
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
