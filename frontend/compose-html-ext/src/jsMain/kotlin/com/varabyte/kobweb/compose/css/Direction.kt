package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/direction
sealed interface Direction : StylePropertyValue {
    companion object : CssGlobalValues<Direction> {
        val Ltr get() = "ltr".unsafeCast<Direction>()
        val Rtl get() = "rtl".unsafeCast<Direction>()
    }
}

fun StyleScope.direction(direction: Direction) {
    property("direction", direction)
}