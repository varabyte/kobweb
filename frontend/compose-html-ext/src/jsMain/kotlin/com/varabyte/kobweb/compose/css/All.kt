package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/all
class All private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {

        /* Global values */
        val Inherit get() = All("inherit")
        val Initial get() = All("initial")
        val Revert get() = All("revert")
        val RevertLayer get() = All("revert-layer")
        val Unset get() = All("unset")
    }
}

fun StyleScope.all(all: All) {
    property("all", all)
}