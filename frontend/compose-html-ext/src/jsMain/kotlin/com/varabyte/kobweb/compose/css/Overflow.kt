package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow
sealed interface Overflow : StylePropertyValue {
    companion object : CssGlobalValues<Overflow> {
        // General
        val Visible get() = "visible".unsafeCast<Overflow>()
        val Hidden get() = "hidden".unsafeCast<Overflow>()
        val Clip get() = "clip".unsafeCast<Overflow>()
        val Scroll get() = "scroll".unsafeCast<Overflow>()
        val Auto get() = "auto".unsafeCast<Overflow>()
    }
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

fun StyleScope.overflowWrap(overflowWrap: OverflowWrap) {
    property("overflow-wrap", overflowWrap)
}
