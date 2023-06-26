package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color
// Named CSSColor to avoid ambiguity with org.jetbrains.compose.web.css.Color
class CSSColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val CurrentColor get() = CSSColor("currentColor")

        // Global values
        val Inherit get() = Color("inherit")
        val Initial get() = Color("initial")
        val Revert get() = Color("revert")
        val Unset get() = Color("unset")
    }
}

fun StyleScope.color(color: CSSColor) {
    color(color.toString())
}

fun StyleScope.color(value: String) {
    property("color", value)
}
