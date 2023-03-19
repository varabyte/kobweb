package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events
class PointerEvents private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Auto get() = PointerEvents("auto")
        val None get() = PointerEvents("none")

        // Global
        val Inherit get() = PointerEvents("inherit")
        val Initial get() = PointerEvents("initial")
        val Revert get() = PointerEvents("revert")
        val Unset get() = PointerEvents("unset")
    }
}

fun StyleScope.pointerEvents(pointerEvents: PointerEvents) {
    property("pointer-events", pointerEvents)
}