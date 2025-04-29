package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/widows
class Widows private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // <integer> values
        fun of(numLines: Int) = Widows("$numLines")

        // Global values
        val Inherit get() = Widows("inherit")
        val Initial get() = Widows("initial")
        val Revert get() = Widows("revert")
        val Unset get() = Widows("unset")
    }
}

fun StyleScope.widows(widows: Widows) {
    property("widows", widows)
}