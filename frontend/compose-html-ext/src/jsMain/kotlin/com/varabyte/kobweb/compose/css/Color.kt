package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*


// https://developer.mozilla.org/en-US/docs/Web/CSS/accent-color
class AccentColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword values
        val Auto get() = AccentColor("auto")

        // <color> values
        fun of(color: CSSColorValue) = AccentColor(color.toString())

        // Global values
        val Inherit get() = AccentColor("inherit");
        val Initial get() = AccentColor("initial")
        val Revert get() = AccentColor("revert")

        //        val RevertLayer get() = AccentColor("revert-layer")
        val Unset get() = AccentColor("unset")
    }
}

fun StyleScope.accentColor(accentColor: AccentColor) {
    property("accent-color", accentColor)
}

fun StyleScope.accentColor(color: CSSColorValue) {
    property("accent-color", AccentColor.of(color))
}

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
