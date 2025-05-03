package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events
sealed interface PointerEvents : StylePropertyValue {
    companion object : CssGlobalValues<PointerEvents> {
        // Keyword
        val Auto get() = "auto".unsafeCast<PointerEvents>()
        val None get() = "none".unsafeCast<PointerEvents>()
    }
}

fun StyleScope.pointerEvents(pointerEvents: PointerEvents) {
    property("pointer-events", pointerEvents)
}
