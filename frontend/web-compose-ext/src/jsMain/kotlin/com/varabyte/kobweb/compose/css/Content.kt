package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

fun StyleBuilder.content(value: String) {
    property("content", value)
}
