package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/widows
class Widows private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<Widows> {
        // <integer> values
        fun of(numLines: Int) = Widows("$numLines")
    }
}

fun StyleScope.widows(widows: Widows) {
    property("widows", widows)
}