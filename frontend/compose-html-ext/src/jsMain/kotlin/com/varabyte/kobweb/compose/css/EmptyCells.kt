package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/empty-cells
sealed interface EmptyCells : StylePropertyValue {
    companion object : CssGlobalValues<EmptyCells> {
        val Show get() = "show".unsafeCast<EmptyCells>()
        val Hide get() = "hide".unsafeCast<EmptyCells>()
    }
}

fun StyleScope.emptyCells(emptyCells: EmptyCells) {
    property("empty-cells", emptyCells)
}
