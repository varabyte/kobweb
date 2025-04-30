package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope

// https://developer.mozilla.org/en-US/docs/Web/CSS/all
sealed interface All : StylePropertyValue {
    companion object : CssGlobalValues<All>
}

fun StyleScope.all(all: All) {
    property("all", all)
}