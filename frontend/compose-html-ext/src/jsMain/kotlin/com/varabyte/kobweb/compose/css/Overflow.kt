package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.Overflow.Mode
import org.jetbrains.compose.web.css.*

internal sealed interface CssOverflowModeValues<T: StylePropertyValue> {
    val Visible get() = "visible".unsafeCast<T>()
    val Hidden get() = "hidden".unsafeCast<T>()
    val Clip get() = "clip".unsafeCast<T>()
    val Scroll get() = "scroll".unsafeCast<T>()
    val Auto get() = "auto".unsafeCast<T>()
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow
sealed interface Overflow : StylePropertyValue {
    sealed interface SingleValue : Overflow
    sealed interface Mode : SingleValue

    companion object : CssOverflowModeValues<Mode>, CssGlobalValues<SingleValue> {
        fun of(x: Mode, y: Mode) = "$x $y".unsafeCast<Overflow>()
    }
}

fun StyleScope.overflow(overflow: Overflow) {
    property("overflow", overflow)
}

fun StyleScope.overflow(overflowX: Overflow, overflowY: Overflow) {
    property("overflow", "$overflowX $overflowY")
}

fun StyleScope.overflowX(overflowX: Overflow) {
    property("overflow-x", overflowX)
}

fun StyleScope.overflowY(overflowY: Overflow) {
    property("overflow-y", overflowY)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-block
sealed interface OverflowBlock : StylePropertyValue {
    companion object : CssOverflowModeValues<OverflowBlock>, CssGlobalValues<OverflowBlock>
}

fun StyleScope.overflowBlock(overflowBlock: OverflowBlock) {
    property("overflow-block", overflowBlock)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-inline
sealed interface OverflowInline : StylePropertyValue {
    companion object : CssOverflowModeValues<OverflowInline>, CssGlobalValues<OverflowInline>
}

fun StyleScope.overflowInline(overflowInline: OverflowInline) {
    property("overflow-inline", overflowInline)
}

sealed interface OverflowScrollBehavior : StylePropertyValue {

    sealed interface Listable : OverflowScrollBehavior
    companion object : CssGlobalValues<OverflowScrollBehavior> {

        /* Keyword values */
        val Auto: OverflowScrollBehavior get() = "auto".unsafeCast<OverflowScrollBehavior>()
        val Contain: OverflowScrollBehavior get() = "contain".unsafeCast<OverflowScrollBehavior>()
        val None: OverflowScrollBehavior get() = "none".unsafeCast<OverflowScrollBehavior>()

        /* Two values */
        fun list(vararg values: Listable) = values.joinToString(" ").unsafeCast<OverflowScrollBehavior>()
    }
}

fun StyleScope.overflowScrollBehavior(overflowScrollBehavior: OverflowScrollBehavior) {
    property("overscroll-behavior", overflowScrollBehavior)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-wrap
sealed interface OverflowWrap : StylePropertyValue {
    companion object : CssGlobalValues<OverflowWrap> {
        // General
        val Normal get() = "normal".unsafeCast<OverflowWrap>()
        val BreakWord get() = "break-word".unsafeCast<OverflowWrap>()
        val Anywhere get() = "anywhere".unsafeCast<OverflowWrap>()
    }
}

fun StyleScope.overflowWrap(overflowWrap: OverflowWrap) {
    property("overflow-wrap", overflowWrap)
}
