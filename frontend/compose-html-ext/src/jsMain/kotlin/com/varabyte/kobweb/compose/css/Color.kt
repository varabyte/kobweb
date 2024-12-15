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

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color-scheme
class ColorScheme private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword values
        val Normal get() = ColorScheme("normal")
        val Light get() = ColorScheme("light")
        val Dark get() = ColorScheme("dark")
        val LightDark get() = ColorScheme("light dark")
        val DarkLight get() = ColorScheme("dark light")
        val OnlyLight get() = ColorScheme("only light")
        val OnlyDark get() = ColorScheme("only dark")

        // Global values
        val Inherit get() = ColorScheme("inherit")
        val Initial get() = ColorScheme("initial")
        val Revert get() = ColorScheme("revert")
        val Unset get() = ColorScheme("unset")
    }
}

fun StyleScope.colorScheme(colorScheme: ColorScheme) {
    property("color-scheme", colorScheme)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/color
// Named CSSColor to avoid ambiguity with org.jetbrains.compose.web.css.Color
class CSSColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val CurrentColor get() = CSSColor("currentColor")

        // Global values
        val Inherit get() = CSSColor("inherit")
        val Initial get() = CSSColor("initial")
        val Revert get() = CSSColor("revert")
        val Unset get() = CSSColor("unset")
    }
}

fun StyleScope.color(color: CSSColor) {
    color(color.toString())
}

fun StyleScope.color(value: String) {
    property("color", value)
}
