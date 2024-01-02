package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

class ScrollBehavior private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Auto get() = ScrollBehavior("auto")
        val Smooth get() = ScrollBehavior("smooth")

        // Global
        val Inherit get() = ScrollBehavior("inherit")
        val Initial get() = ScrollBehavior("initial")
        val Revert get() = ScrollBehavior("revert")
        val Unset get() = ScrollBehavior("unset")
    }
}

fun StyleScope.scrollBehavior(scrollBehavior: ScrollBehavior) {
    property("scroll-behavior", scrollBehavior)
}

// region Scroll snap
// See https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-type
class ScrollSnapType private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val None get() = ScrollSnapType("none")

        // Global
        val Inherit get() = ScrollSnapType("inherit")
        val Initial get() = ScrollSnapType("initial")
        val Revert get() = ScrollSnapType("revert")
        val Unset get() = ScrollSnapType("unset")
    }
}

class ScrollSnapAxis private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val X get() = ScrollSnapAxis("x")
        val Y get() = ScrollSnapAxis("y")
        val Block get() = ScrollSnapAxis("block")
        val Inline get() = ScrollSnapAxis("inline")
        val Both get() = ScrollSnapAxis("both")
    }
}

class ScrollSnapMode private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Mandatory get() = ScrollSnapMode("mandatory")
        val Proximity get() = ScrollSnapMode("proximity")
    }
}

fun StyleScope.scrollSnapType(type: ScrollSnapType) {
    property("scroll-snap-type", type)
}

fun StyleScope.scrollSnapType(axis: ScrollSnapAxis, mode: ScrollSnapMode? = null) {
    val value = buildString {
        append(axis.toString())
        if (mode != null) {
            append(' ')
            append(mode.toString())
        }
    }
    property("scroll-snap-type", value)
}
// endregion

// region Scroll padding

fun StyleScope.scrollPadding(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    right: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
    left: CSSLengthOrPercentageNumericValue = 0.px
) {
    property("scroll-padding", "$top $right $bottom $left")
}

fun StyleScope.scrollPadding(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding", "$value")
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

fun StyleScope.scrollPaddingInline(vararg value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-inline", value.joinToString(" "))
}

fun StyleScope.scrollPaddingInlineStart(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-inline-start", value)
}

fun StyleScope.scrollPaddingInlineEnd(value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-inline-end", value)
}

fun StyleScope.scrollPaddingBlock(vararg value: CSSLengthOrPercentageNumericValue) {
    property("scroll-padding-block", value.joinToString(" "))
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
class ScrollSnapAlign private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val None get() = ScrollSnapAlign("none")
        val Start get() = ScrollSnapAlign("start")
        val End get() = ScrollSnapAlign("end")
        val Center get() = ScrollSnapAlign("center")

        // Global
        val Inherit get() = ScrollSnapAlign("inherit")
        val Initial get() = ScrollSnapAlign("initial")
        val Revert get() = ScrollSnapAlign("revert")
        val Unset get() = ScrollSnapAlign("unset")
    }
}

fun StyleScope.scrollSnapAlign(align: ScrollSnapAlign) {
    scrollSnapAlign(align, align)
}

fun StyleScope.scrollSnapAlign(blockAxis: ScrollSnapAlign, inlineAxis: ScrollSnapAlign? = null) {
    property("scroll-snap-align", "$blockAxis $inlineAxis")
}

// endregion

// region Scroll snap stop

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-stop
class ScrollSnapStop private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Normal get() = ScrollSnapStop("normal")
        val Always get() = ScrollSnapStop("always")

        // Global
        val Inherit get() = ScrollSnapStop("inherit")
        val Initial get() = ScrollSnapStop("initial")
        val Revert get() = ScrollSnapStop("revert")
        val Unset get() = ScrollSnapStop("unset")
    }
}

fun StyleScope.scrollSnapStop(scrollSnapStop: ScrollSnapStop) {
    property("scroll-snap-stop", scrollSnapStop)
}

// endregion

// region Scroll margin

fun StyleScope.scrollMargin(
    top: CSSLengthNumericValue = 0.px,
    right: CSSLengthNumericValue = 0.px,
    bottom: CSSLengthNumericValue = 0.px,
    left: CSSLengthNumericValue = 0.px
) {
    property("scroll-margin", "$top $right $bottom $left")
}

fun StyleScope.scrollMargin(vararg value: CSSLengthNumericValue) {
    property("scroll-margin", value.joinToString(" "))
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

fun StyleScope.scrollMarginInline(vararg value: CSSLengthNumericValue) {
    property("scroll-margin-inline", value.joinToString(" "))
}

fun StyleScope.scrollMarginInlineStart(value: CSSLengthNumericValue) {
    property("scroll-margin-inline-start", value)
}

fun StyleScope.scrollMarginInlineEnd(value: CSSLengthNumericValue) {
    property("scroll-margin-inline-end", value)
}

fun StyleScope.scrollMarginBlock(vararg value: CSSLengthNumericValue) {
    property("scroll-margin-block", value.joinToString(" "))
}

fun StyleScope.scrollMarginBlockStart(value: CSSLengthNumericValue) {
    property("scroll-margin-block-start", value)
}

fun StyleScope.scrollMarginBlockEnd(value: CSSLengthNumericValue) {
    property("scroll-margin-block-end", value)
}

// endregion
