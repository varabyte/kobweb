package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StyleBuilder

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow
class Overflow(val value: String) {
    companion object {
        // General
        val Visible get() = Overflow("visible")
        val Hidden get() = Overflow("hidden")
        val Clip get() = Overflow("clip")
        val Scroll get() = Overflow("scroll")
        val Auto get() = Overflow("auto")

        // Global
        val Inherit get() = Overflow("inherit")
        val Initial get() = Overflow("initial")
        val Revert get() = Overflow("revert")
        val Unset get() = Overflow("unset")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-wrap
class OverflowWrap(val value: String) {
    companion object {
        // General
        val Normal get() = OverflowWrap("normal")
        val BreakWord get() = OverflowWrap("break-word")
        val Anywhere get() = OverflowWrap("anywhere")

        // Global
        val Inherit get() = OverflowWrap("inherit")
        val Initial get() = OverflowWrap("initial")
        val Revert get() = OverflowWrap("revert")
        val Unset get() = OverflowWrap("unset")
    }
}

fun StyleBuilder.overflow(vararg overflows: Overflow) {
    property("overflow", overflows.joinToString(" ") { it.value })
}

fun StyleBuilder.overflowX(overflowX: Overflow) {
    property("overflow-x", overflowX.value)
}

fun StyleBuilder.overflowY(overflowY: Overflow) {
    property("overflow-y", overflowY.value)
}

fun StyleBuilder.overflowWrap(overflowWrap: OverflowWrap) {
    property("overflow-wrap", overflowWrap.value)
}
