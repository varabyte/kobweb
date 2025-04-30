package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// Read more about stacking contexts here:
// https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_positioned_layout/Understanding_z-index/Stacking_context

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/isolation
sealed interface Isolation : StylePropertyValue {
    companion object : CssGlobalValues<Isolation> {
        // Keywords
        val Auto get() = "auto".unsafeCast<Isolation>()
        val Isolate get() = "isolate".unsafeCast<Isolation>()
    }
}

fun StyleScope.isolation(isolation: Isolation) {
    property("isolation", isolation)
}
