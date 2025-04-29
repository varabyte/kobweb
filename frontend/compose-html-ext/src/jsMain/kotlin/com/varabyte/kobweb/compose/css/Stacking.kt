package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// Read more about stacking contexts here:
// https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_positioned_layout/Understanding_z-index/Stacking_context

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/isolation
class Isolation private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<Isolation> {
        // Keywords
        val Auto get() = Isolation("auto")
        val Isolate get() = Isolation("isolate")
    }
}

fun StyleScope.isolation(isolation: Isolation) {
    property("isolation", isolation)
}
