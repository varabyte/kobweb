package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
)
fun Modifier.listStyle(value: String) = styleModifier {
    listStyle(value)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
)
fun Modifier.listStyleType(value: String) = styleModifier {
    listStyleType(value)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
)
fun Modifier.listStyleImage(value: String) = styleModifier {
    listStyleImage(value)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
)
fun Modifier.listStylePosition(value: String) = styleModifier {
    listStylePosition(value)
}

fun Modifier.listStyle(
    type: ListStyleType? = null,
    position: ListStylePosition? = null,
    image: ListStyleImage? = null
) = styleModifier {
    listStyle(type, position, image)
}
