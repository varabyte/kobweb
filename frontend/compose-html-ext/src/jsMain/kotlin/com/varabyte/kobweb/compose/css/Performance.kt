package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/will-change
class WillChange private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<WillChange> {
        // Keyword values
        val Auto get() = WillChange("auto")
        val ScrollPosition get() = WillChange("scroll-position")
        val Contents get() = WillChange("contents")

        // Custom ident values
        fun of(vararg values: String) = WillChange(values.joinToString())
    }
}

fun StyleScope.willChange(willChange: WillChange) {
    property("will-change", willChange)
}