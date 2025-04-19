package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/visibility
class Visibility private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Visibility> {
        // Keyword
        val Visible get() = Visibility("visible")
        val Hidden get() = Visibility("hidden")
        val Collapse get() = Visibility("collapse")
    }
}

fun StyleScope.visibility(visibility: Visibility) {
    property("visibility", visibility)
}
