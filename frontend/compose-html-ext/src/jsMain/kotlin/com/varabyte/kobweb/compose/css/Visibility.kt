package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/visibility
sealed interface Visibility : StylePropertyValue {
    companion object : CssGlobalValues<Visibility> {
        // Keyword
        val Visible get() = "visible".unsafeCast<Visibility>()
        val Hidden get() = "hidden".unsafeCast<Visibility>()
        val Collapse get() = "collapse".unsafeCast<Visibility>()
    }
}

fun StyleScope.visibility(visibility: Visibility) {
    property("visibility", visibility)
}
