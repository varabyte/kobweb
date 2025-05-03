package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/will-change
sealed interface WillChange : StylePropertyValue {
    companion object : CssGlobalValues<WillChange> {
        // Keyword values
        val Auto get() = "auto".unsafeCast<WillChange>()
        val ScrollPosition get() = "scroll-position".unsafeCast<WillChange>()
        val Contents get() = "contents".unsafeCast<WillChange>()

        // Custom ident values
        fun of(vararg values: String) = values.joinToString().unsafeCast<WillChange>()
    }
}

fun StyleScope.willChange(willChange: WillChange) {
    property("will-change", willChange)
}