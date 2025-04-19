package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow
class Overflow private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Overflow> {
        // General
        val Visible get() = Overflow("visible")
        val Hidden get() = Overflow("hidden")
        val Clip get() = Overflow("clip")
        val Scroll get() = Overflow("scroll")
        val Auto get() = Overflow("auto")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-wrap
class OverflowWrap private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<OverflowWrap> {
        // General
        val Normal get() = OverflowWrap("normal")
        val BreakWord get() = OverflowWrap("break-word")
        val Anywhere get() = OverflowWrap("anywhere")
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
