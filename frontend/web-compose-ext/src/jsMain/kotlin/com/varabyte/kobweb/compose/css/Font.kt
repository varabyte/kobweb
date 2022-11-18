package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleScope.fontFamily(value: String) {
    property("font-family", value)
}

class FontStyle private constructor(val value: String) {
    companion object {
        // Keyword
        val Normal get() = FontStyle("normal")
        val Italic get() = FontStyle("italic")
        val Oblique get() = FontStyle("oblique")

        fun Oblique(angle: CSSAngleValue) = FontStyle("oblique $angle")

        // Global
        val Inherit get() = FontStyle("inherit")
        val Initial get() = FontStyle("initial")
        val Revert get() = FontStyle("revert")
        val RevertLayer get() = FontStyle("revert-layer")
        val Unset get() = FontStyle("unset")    }
}

fun StyleScope.fontStyle(style: FontStyle) {
    property("font-style", style.value)
}

class FontWeight private constructor(val value: String) {
    companion object {
        // Keyword
        val Normal get() = FontWeight("normal")
        val Bold get() = FontWeight("bold")

        // Relative
        val Lighter get() = FontWeight("lighter")
        val Bolder get() = FontWeight("bolder")

        // Global
        val Inherit get() = FontWeight("inherit")
        val Initial get() = FontWeight("initial")
        val Revert get() = FontWeight("revert")
        val RevertLayer get() = FontWeight("revert-layer")
        val Unset get() = FontWeight("unset")
    }
}

fun StyleScope.fontWeight(weight: FontWeight) {
    property("font-weight", weight.value)
}
