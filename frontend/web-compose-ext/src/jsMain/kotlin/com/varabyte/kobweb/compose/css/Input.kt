package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope

// region Caret Color, see https://developer.mozilla.org/en-US/docs/Web/CSS/caret-color

class CaretColor private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Auto get() = CaretColor("auto")
        val Transparent get() = CaretColor("transparent")
        val CurrentColor get() = CaretColor("currentcolor")

        // Global
        val Inherit get() = CaretColor("inherit")
        val Initial get() = CaretColor("initial")
        val Revert get() = CaretColor("revert")
        val RevertLayer get() = CaretColor("revert-layer")
        val Unset get() = CaretColor("unset")
    }
}

fun StyleScope.caretColor(caretColor: CaretColor) {
    property("caret-color", caretColor)
}

fun StyleScope.caretColor(color: CSSColorValue) {
    property("caret-color", color)
}

// endregion