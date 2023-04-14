package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*

fun AttrsScope<*>.disabled() {
    attr("disabled", "true")
}
