// Sealed class private constructors are useful, actually!
@file:Suppress("RedundantVisibilityModifier")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

internal sealed interface CssOverscrollModeValues<T: StylePropertyValue> {
    val Auto get() = "auto".unsafeCast<T>()
    val Contain get() = "contain".unsafeCast<T>()
    val None get() = "none".unsafeCast<T>()
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overscroll-behavior
sealed interface OverscrollBehavior : StylePropertyValue {
    sealed interface SingleValue : OverscrollBehavior
    sealed interface Mode : SingleValue

    companion object : CssOverscrollModeValues<Mode>, CssGlobalValues<SingleValue> {
        fun of(x: Mode, y: Mode): OverscrollBehavior = "$x $y".unsafeCast<OverscrollBehavior>()
    }
}

fun StyleScope.overscrollBehavior(overscrollBehavior: OverscrollBehavior) {
    property("overscroll-behavior", overscrollBehavior)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overscroll-behavior-x
fun StyleScope.overscrollBehaviorX(overscrollBehavior: OverscrollBehavior.SingleValue) {
    property("overscroll-behavior-x", overscrollBehavior)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overscroll-behavior-y
fun StyleScope.overscrollBehaviorY(overscrollBehavior: OverscrollBehavior.SingleValue) {
    property("overscroll-behavior-y", overscrollBehavior)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overscroll-behavior-block
sealed interface OverscrollBehaviorBlock : StylePropertyValue {
    companion object : CssOverscrollModeValues<OverscrollBehaviorBlock>, CssGlobalValues<OverscrollBehaviorBlock>
}

fun StyleScope.overscrollBehaviorBlock(overscrollBehaviorBlock: OverscrollBehaviorBlock) {
    property("overscroll-behavior-block", overscrollBehaviorBlock)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overscroll-behavior-inline
sealed interface OverscrollBehaviorInline : StylePropertyValue {
    companion object : CssOverscrollModeValues<OverscrollBehaviorInline>, CssGlobalValues<OverscrollBehaviorInline>
}

// NOTE: Can't use OverscrollBehaviorInline.SingleValue here. Nested classes under typealiases are unsupported.
// See also: https://youtrack.jetbrains.com/issue/KT-34281
fun StyleScope.overscrollBehaviorInline(overscrollBehaviorInline: OverscrollBehaviorInline) {
    property("overscroll-behavior-inline", overscrollBehaviorInline)
}

sealed interface ScrollBehavior : StylePropertyValue {
    companion object : CssGlobalValues<ScrollBehavior> {
        // Keyword
        val Auto get() = "auto".unsafeCast<ScrollBehavior>()
        val Smooth get() = "smooth".unsafeCast<ScrollBehavior>()
    }
}

fun StyleScope.scrollBehavior(scrollBehavior: ScrollBehavior) {
    property("scroll-behavior", scrollBehavior)
}

// region Scroll snap
// See https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-type
sealed interface ScrollSnapType : StylePropertyValue {
    sealed interface Axis : ScrollSnapType

    enum class Strictness : StylePropertyValue {
        Mandatory, Proximity;

        override fun toString() = name.lowercase()
    }

    companion object : CssGlobalValues<ScrollSnapType> {
        // Keyword
        val None: ScrollSnapType get() = "none".unsafeCast<ScrollSnapType>()

        // Axes
        val X get() = "x".unsafeCast<Axis>()
        val Y get() = "y".unsafeCast<Axis>()
        val Block get() = "block".unsafeCast<Axis>()
        val Inline get() = "inline".unsafeCast<Axis>()
        val Both get() = "both".unsafeCast<Axis>()

        fun of(axis: Axis, strictness: Strictness): ScrollSnapType = "$axis $strictness".unsafeCast<ScrollSnapType>()
    }
}

fun StyleScope.scrollSnapType(scrollSnapType: ScrollSnapType) {
    property("scroll-snap-type", scrollSnapType)
}

// endregion

// region Scroll padding

fun StyleScope.scrollPadding(all: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding", "$all")
}

// Use `autoLength` as default value to match CSS spec

fun StyleScope.scrollPadding(
    topBottom: CSSLengthOrPercentageNumericValue = autoLength,
    leftRight: CSSLengthOrPercentageNumericValue = autoLength,
) {
    property("scroll-padding", "$topBottom $leftRight")
}

fun StyleScope.scrollPadding(
    top: CSSLengthOrPercentageNumericValue = autoLength,
    leftRight: CSSLengthOrPercentageNumericValue = autoLength,
    bottom: CSSLengthOrPercentageNumericValue = autoLength,
) {
    property("scroll-padding", "$top $leftRight $bottom")
}

fun StyleScope.scrollPadding(
    top: CSSLengthOrPercentageNumericValue = autoLength,
    right: CSSLengthOrPercentageNumericValue = autoLength,
    bottom: CSSLengthOrPercentageNumericValue = autoLength,
    left: CSSLengthOrPercentageNumericValue = autoLength,
) {
    property("scroll-padding", "$top $right $bottom $left")
}

fun StyleScope.scrollPaddingTop(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-top", value)
}

fun StyleScope.scrollPaddingRight(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-right", value)
}

fun StyleScope.scrollPaddingBottom(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-bottom", value)
}

fun StyleScope.scrollPaddingLeft(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-left", value)
}

fun StyleScope.scrollPaddingInline(both: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-inline", both)
}

fun StyleScope.scrollPaddingInline(
    start: CSSLengthOrPercentageNumericValue = autoLength,
    end: CSSLengthOrPercentageNumericValue = autoLength,
) {
    property("scroll-padding-inline", "$start $end")
}

fun StyleScope.scrollPaddingInlineStart(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-inline-start", value)
}

fun StyleScope.scrollPaddingInlineEnd(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-inline-end", value)
}

fun StyleScope.scrollPaddingBlock(both: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-block", both)
}

fun StyleScope.scrollPaddingBlock(
    start: CSSLengthOrPercentageNumericValue = autoLength,
    end: CSSLengthOrPercentageNumericValue = autoLength,
) {
    property("scroll-padding-block", "$start $end")
}

fun StyleScope.scrollPaddingBlockStart(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-block-start", value)
}

fun StyleScope.scrollPaddingBlockEnd(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-block-end", value)
}

// endregion

// region Scroll snap align

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-align
sealed interface ScrollSnapAlign : StylePropertyValue {
    sealed interface Alignment : ScrollSnapAlign

    companion object : CssGlobalValues<ScrollSnapAlign> {
        // Keyword
        val None get() = "none".unsafeCast<Alignment>()
        val Start get() = "start".unsafeCast<Alignment>()
        val End get() = "end".unsafeCast<Alignment>()
        val Center get() = "center".unsafeCast<Alignment>()

        fun of(blockAxis: Alignment, inlineAxis: Alignment) = "$blockAxis $inlineAxis".unsafeCast<ScrollSnapAlign>()
    }
}

fun StyleScope.scrollSnapAlign(scrollSnapAlign: ScrollSnapAlign) {
    property("scroll-snap-align", scrollSnapAlign)
}

// endregion

// region Scroll snap stop

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-stop
sealed interface ScrollSnapStop : StylePropertyValue {
    companion object : CssGlobalValues<ScrollSnapStop> {
        // Keyword
        val Normal get() = "normal".unsafeCast<ScrollSnapStop>()
        val Always get() = "always".unsafeCast<ScrollSnapStop>()
    }
}

fun StyleScope.scrollSnapStop(scrollSnapStop: ScrollSnapStop) {
    property("scroll-snap-stop", scrollSnapStop)
}

// endregion

// region Scroll margin

fun StyleScope.scrollMargin(all: CSSLengthNumericValue) {
    property("scroll-margin", all)
}

fun StyleScope.scrollMargin(
    topBottom: CSSLengthNumericValue = 0.px,
    leftRight: CSSLengthNumericValue = 0.px,
) {
    property("scroll-margin", "$topBottom $leftRight")
}

fun StyleScope.scrollMargin(
    top: CSSLengthNumericValue = 0.px,
    leftRight: CSSLengthNumericValue = 0.px,
    bottom: CSSLengthNumericValue = 0.px,
) {
    property("scroll-margin", "$top $leftRight $bottom")
}

fun StyleScope.scrollMargin(
    top: CSSLengthNumericValue = 0.px,
    right: CSSLengthNumericValue = 0.px,
    bottom: CSSLengthNumericValue = 0.px,
    left: CSSLengthNumericValue = 0.px,
) {
    property("scroll-margin", "$top $right $bottom $left")
}

fun StyleScope.scrollMarginTop(value: CSSLengthNumericValue) {
    property("scroll-margin-top", value)
}

fun StyleScope.scrollMarginRight(value: CSSLengthNumericValue) {
    property("scroll-margin-right", value)
}

fun StyleScope.scrollMarginBottom(value: CSSLengthNumericValue) {
    property("scroll-margin-bottom", value)
}

fun StyleScope.scrollMarginLeft(value: CSSLengthNumericValue) {
    property("scroll-margin-left", value)
}

fun StyleScope.scrollMarginInline(both: CSSLengthNumericValue) {
    property("scroll-margin-inline", both)
}

fun StyleScope.scrollMarginInline(start: CSSLengthNumericValue = 0.px, end: CSSLengthNumericValue = 0.px) {
    property("scroll-margin-inline", "$start $end")
}

fun StyleScope.scrollMarginInlineStart(value: CSSLengthNumericValue) {
    property("scroll-margin-inline-start", value)
}

fun StyleScope.scrollMarginInlineEnd(value: CSSLengthNumericValue) {
    property("scroll-margin-inline-end", value)
}

fun StyleScope.scrollMarginBlock(both: CSSLengthNumericValue) {
    property("scroll-margin-block", both)
}

fun StyleScope.scrollMarginBlock(start: CSSLengthNumericValue = 0.px, end: CSSLengthNumericValue = 0.px) {
    property("scroll-margin-block", "$start $end")
}

fun StyleScope.scrollMarginBlockStart(value: CSSLengthNumericValue) {
    property("scroll-margin-block-start", value)
}

fun StyleScope.scrollMarginBlockEnd(value: CSSLengthNumericValue) {
    property("scroll-margin-block-end", value)
}

// endregion

// region ScrollbarWidth

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/scrollbar-width
sealed interface ScrollbarWidth : StylePropertyValue {
    companion object : CssGlobalValues<ScrollbarWidth> {
        // Keyword
        val Auto get() = "auto".unsafeCast<ScrollbarWidth>()
        val Thin get() = "thin".unsafeCast<ScrollbarWidth>()
        val None get() = "none".unsafeCast<ScrollbarWidth>()
    }
}

fun StyleScope.scrollbarWidth(scrollbarWidth: ScrollbarWidth) {
    property("scrollbar-width", scrollbarWidth)
}

// endregion
