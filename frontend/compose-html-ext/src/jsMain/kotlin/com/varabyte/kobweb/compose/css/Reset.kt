package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope

// https://developer.mozilla.org/en-US/docs/Web/CSS/all
class All private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Global values
        val Inherit get() = All("inherit")
        val Initial get() = All("initial")
        val Revert get() = All("revert")
        val Unset get() = All("unset")
    }
}

fun StyleScope.all(all: All) {
    property("all", all)
}