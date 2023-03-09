package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleScope.fontFamily(value: String) {
    property("font-family", value)
}

class FontStyle private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

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
    property("font-style", style)
}

class FontWeight private constructor(private val value: String): CSSStyleValue {
    override fun toString() = value

    companion object {
        // Common value constants
        // https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight#common_weight_name_mapping
        val Thin get() = FontWeight("100")
        val ExtraLight get() = FontWeight("200")
        val Light get() = FontWeight("300")
        // val Normal get() = FontWeight("400") // Same as "Normal" keyword"
        val Medium get() = FontWeight("500")
        val SemiBold get() = FontWeight("600")
        // val Bold get() = FontWeight("700") // Same as "Bold" keyword"
        val ExtraBold get() = FontWeight("800")
        val Black get() = FontWeight("900")
        val ExtraBlack get() = FontWeight("950")

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
    property("font-weight", weight)
}
