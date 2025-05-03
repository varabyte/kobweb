package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/widows
sealed interface Widows : StylePropertyValue {
    companion object : CssGlobalValues<Widows> {
        // <integer> values
        fun of(numLines: Int) = "$numLines".unsafeCast<Widows>()
    }
}

fun StyleScope.widows(widows: Widows) {
    property("widows", widows)
}