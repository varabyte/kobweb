package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.color(color: CSSColorValue) = styleModifier {
    color(color)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"color\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    ),
)
fun Modifier.color(value: String) = styleModifier {
    property("color", value)
}

fun Modifier.opacity(value: Number) = styleModifier {
    opacity(value)
}

fun Modifier.opacity(value: CSSSizeValue<CSSUnit.percent>) = styleModifier {
    opacity(value)
}