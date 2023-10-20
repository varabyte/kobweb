package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope

fun AttrsScope<*>.ariaDisabled(value: Boolean = true) {
    attr("aria-disabled", value.toString())
}

fun AttrsScope<*>.ariaHidden(value: Boolean = true) {
    attr("aria-hidden", value.toString())
}

fun AttrsScope<*>.ariaInvalid(value: Boolean = true) {
    attr("aria-invalid", value.toString())
}

fun AttrsScope<*>.ariaLabel(value: String) {
    attr("aria-label", value)
}

fun AttrsScope<*>.ariaRequired(value: Boolean = true) {
    attr("aria-required", value.toString())
}

fun AttrsScope<*>.role(value: String) {
    attr("role", value)
}
