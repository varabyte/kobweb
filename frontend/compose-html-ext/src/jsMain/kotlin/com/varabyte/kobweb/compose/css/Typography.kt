package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

//See: https://developer.mozilla.org/en-US/docs/Web/CSS/orphans
sealed interface Orphans : StylePropertyValue {
    companion object : CssGlobalValues<Orphans> {
        /* <integer> values */
        fun of(value: Int) = value.unsafeCast<Orphans>()
    }
}

fun StyleScope.orphans(orphans: Orphans) {
    property("orphans", orphans)
}

//See: https://developer.mozilla.org/en-US/docs/Web/CSS/widows
sealed interface Widows : StylePropertyValue {
    companion object : CssGlobalValues<Widows> {
        // <integer> values
        fun of(numLines: Int) = "$numLines".unsafeCast<Widows>()
    }
}

fun StyleScope.widows(widows: Widows) {
    property("widows", widows)
}