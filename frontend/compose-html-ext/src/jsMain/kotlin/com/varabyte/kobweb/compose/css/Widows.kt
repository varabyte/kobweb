package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/widows
class Widows private constructor(private val value: String) : StylePropertyValue {

    companion object {

        /* <integer> values */
        fun of(number: Int) = Widows("$number")

        /* Global values */
        val Inherit get() = Widows("inherit")
        val Initial get() = Widows("initial")
        val Revert get() = Widows("revert")
        val RevertLayer get() = Widows("revert-layer")
        val Unset get() = Widows("unset")
    }
}

fun StyleScope.windows(widows: Widows) {
    property("widows", widows)
}