package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/orphans
sealed interface Orphans : StylePropertyValue {
    companion object : CssGlobalValues<Orphans> {
        /* <integer> values */
        fun of(value: Int = 2) = value.unsafeCast<Orphans>()
    }
}

fun StyleScope.orphans(orphans: Orphans) {
    property("orphans", orphans)
}