package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/visibility
class Visibility private constructor(val value: String) {
    companion object {
        // Keyword
        val Visible get() = Visibility("visible")
        val Hidden get() = Visibility("hidden")
        val Collapse get() = Visibility("collapse")

        // Global
        val Inherit get() = Visibility("inherit")
        val Initial get() = Visibility("initial")
    }
}

fun StyleScope.visibility(visibility: Visibility) {
    property("visibility", visibility.value)
}
