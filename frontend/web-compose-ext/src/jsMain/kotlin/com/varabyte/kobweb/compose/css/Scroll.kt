package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.px

class ScrollBehavior(val value: String) {
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

fun StyleBuilder.scrollBehavior(scrollBehavior: ScrollBehavior) {
    property("scroll-behavior", scrollBehavior.value)
}

// region Scroll snap
// See https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-type
class ScrollSnapType(val value: String) {
    companion object {
        // Keyword
        val None get() = ScrollSnapAxis("none")

        // Global
        val Inherit get() = ScrollSnapType("inherit")
        val Initial get() = ScrollSnapType("initial")
        val Unset get() = ScrollSnapType("unset")
    }
}

class ScrollSnapAxis(val value: String) {
    companion object {
        // Keyword
        val X get() = ScrollSnapAxis("x")
        val Y get() = ScrollSnapAxis("y")
        val Block get() = ScrollSnapAxis("block")
        val Inline get() = ScrollSnapAxis("inline")
        val Both get() = ScrollSnapAxis("both")
    }
}

class ScrollSnapMode(val value: String) {
    companion object {
        // Keyword
        val Mandatory get() = ScrollSnapAxis("mandatory")
        val Proximity get() = ScrollSnapAxis("proximity")
    }
}

fun StyleBuilder.scrollSnapType(type: ScrollSnapType) {
    property("scroll-snap-type", type.value)
}

fun StyleBuilder.scrollSnapType(axis: ScrollSnapAxis, mode: ScrollSnapMode? = null) {
    val value = if (mode == null) axis.value else "${axis.value} ${mode.value}"
    property("scroll-snap-type", value)
}
// endregion

// region Scroll padding

fun StyleBuilder.scrollPadding(
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
) {
    property("scroll-padding", "$top $right $bottom $left")
}

fun StyleBuilder.scrollPadding(value: CSSNumeric) {
    property("scroll-padding", "$value")
}

fun StyleBuilder.scrollPaddingTop(value: CSSNumeric) {
    property("scroll-padding-top", value)
}

fun StyleBuilder.scrollPaddingRight(value: CSSNumeric) {
    property("scroll-padding-right", value)
}

fun StyleBuilder.scrollPaddingBottom(value: CSSNumeric) {
    property("scroll-padding-bottom", value)
}

fun StyleBuilder.scrollPaddingLeft(value: CSSNumeric) {
    property("scroll-padding-left", value)
}

fun StyleBuilder.scrollPaddingInline(vararg value: CSSNumeric) {
    property("scroll-padding-inline", value.joinToString(" "))
}

fun StyleBuilder.scrollPaddingInlineStart(value: CSSNumeric) {
    property("scroll-padding-inline-start", value)
}

fun StyleBuilder.scrollPaddingInlineEnd(value: CSSNumeric) {
    property("scroll-padding-inline-end", value)
}

fun StyleBuilder.scrollPaddingBlock(vararg value: CSSNumeric) {
    property("scroll-padding-block", value.joinToString(" "))
}

fun StyleBuilder.scrollPaddingBlockStart(value: CSSNumeric) {
    property("scroll-padding-block-start", value)
}

fun StyleBuilder.scrollPaddingBlockEnd(value: CSSNumeric) {
    property("scroll-padding-block-end", value)
}

// endregion

// region Scroll snap align

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-align
class ScrollSnapAlign(val value: String) {
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

fun StyleBuilder.scrollSnapAlign(align: ScrollSnapAlign) {
    scrollSnapAlign(align, align)
}

fun StyleBuilder.scrollSnapAlign(blockAxis: ScrollSnapAlign, inlineAxis: ScrollSnapAlign? = null) {
    property("scroll-snap-align", "$blockAxis $inlineAxis")
}

// endregion

// region Scroll snap stop

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-snap-stop
class ScrollSnapStop(val value: String) {
    companion object {
        // Keyword
        val Normal get() = ScrollSnapStop("normal")
        val Always get() = ScrollSnapStop("always")

        // Global
        val Inherit get() = ScrollSnapStop("inherit")
        val Initial get() = ScrollSnapStop("initial")
        val Unset get() = ScrollSnapStop("unset")
    }
}

fun StyleBuilder.scrollSnapStop(scrollSnapStop: ScrollSnapStop) {
    property("scroll-snap-stop", scrollSnapStop.value)
}

// endregion

// region Scroll margin

fun StyleBuilder.scrollMargin(
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
) {
    property("scroll-margin", "$top $right $bottom $left")
}

fun StyleBuilder.scrollMargin(vararg value: CSSNumeric) {
    property("scroll-margin", value.joinToString(" "))
}

fun StyleBuilder.scrollMarginTop(value: CSSNumeric) {
    property("scroll-margin-top", value)
}

fun StyleBuilder.scrollMarginRight(value: CSSNumeric) {
    property("scroll-margin-right", value)
}

fun StyleBuilder.scrollMarginBottom(value: CSSNumeric) {
    property("scroll-margin-bottom", value)
}

fun StyleBuilder.scrollMarginLeft(value: CSSNumeric) {
    property("scroll-margin-left", value)
}

fun StyleBuilder.scrollMarginInline(vararg value: CSSNumeric) {
    property("scroll-margin-inline", value.joinToString(" "))
}

fun StyleBuilder.scrollMarginInlineStart(value: CSSNumeric) {
    property("scroll-margin-inline-start", value)
}

fun StyleBuilder.scrollMarginInlineEnd(value: CSSNumeric) {
    property("scroll-margin-inline-end", value)
}

fun StyleBuilder.scrollMarginBlock(vararg value: CSSNumeric) {
    property("scroll-margin-block", value.joinToString(" "))
}

fun StyleBuilder.scrollMarginBlockStart(value: CSSNumeric) {
    property("scroll-margin-block-start", value)
}

fun StyleBuilder.scrollMarginBlockEnd(value: CSSNumeric) {
    property("scroll-margin-block-end", value)
}

// endregion
