package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// Read more about stacking contexts here:
// https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_positioned_layout/Understanding_z-index/Stacking_context

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/isolation
class Isolation private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Auto get() = Isolation("auto")
        val Isolate get() = Isolation("isolate")

        // Global values
        val Inherit get() = Isolation("inherit")
        val Initial get() = Isolation("initial")
        val Revert get() = Isolation("revert")
        val Unset get() = Isolation("unset")
    }
}

fun StyleScope.isolation(isolation: Isolation) {
    property("isolation", isolation)
}
