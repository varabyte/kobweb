package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope

fun AttrsScope<*>.disabled() {
    attr("disabled", "true")
}
