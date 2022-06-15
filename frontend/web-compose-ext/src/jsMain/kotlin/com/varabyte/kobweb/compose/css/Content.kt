package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleScope.content(value: String) {
    property("content", value)
}
