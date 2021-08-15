package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StyleBuilder

fun StyleBuilder.fontFamily(value: String) {
    property("font-family", value)
}

class FontStyle(val value: String) {
    companion object {
        val Normal get() = FontStyle("normal")
        val Italic get() = FontStyle("italic")
    }
}

fun StyleBuilder.fontStyle(style: FontStyle) {
    property("font-style", style.value)
}

sealed interface FontWeight {
    companion object {
        val Normal get() = StringFontWeight("normal")
        val Bold get() = StringFontWeight("bold")
        val Lighter get() = StringFontWeight("lighter")
        val Bolder get() = StringFontWeight("bolder")
    }
}

class StringFontWeight(val value: String) : FontWeight
class IntFontWeight(val value: Int) : FontWeight

fun StyleBuilder.fontWeight(weight: FontWeight) {
    when (weight) {
        is StringFontWeight -> property("font-weight", weight.value)
        is IntFontWeight -> {
            require(weight.value in 1..1000) { "Font weight must be between 1 and 1000. Got: ${weight.value}" }
            property("font-weight", weight.value)
        }
    }
}
