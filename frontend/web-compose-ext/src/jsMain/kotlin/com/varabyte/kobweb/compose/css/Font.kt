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

class FontWeight private constructor(val value: StylePropertyValue) {
    private constructor(value: String) : this(StylePropertyValue(value))
    constructor(value: Int) : this(value.let {
        require(it in 1..1000) { "Font weight must be between 1 and 1000. Got: $it" }
        StylePropertyValue(it)
    })

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

@Deprecated("This factory method is no longer required. Construct a FontWeight directly instead.",
    ReplaceWith("FontWeight(value)", "com.varabyte.kobweb.compose.css.FontWeight"),
)
fun IntFontWeight(value: Int) = FontWeight(value)

fun StyleScope.fontWeight(weight: FontWeight) {
    property("font-weight", weight.value)
}