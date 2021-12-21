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

fun StyleBuilder.overflow(vararg overflows: Overflow) {
    property("cursor", overflows.joinToString(" ") { it.value })
}