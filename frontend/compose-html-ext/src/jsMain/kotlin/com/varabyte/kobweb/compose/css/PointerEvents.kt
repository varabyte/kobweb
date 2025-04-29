package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events
class PointerEvents private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<PointerEvents> {
        // Keyword
        val Auto get() = PointerEvents("auto")
        val None get() = PointerEvents("none")
    }
}

fun StyleScope.pointerEvents(pointerEvents: PointerEvents) {
    property("pointer-events", pointerEvents)
}
