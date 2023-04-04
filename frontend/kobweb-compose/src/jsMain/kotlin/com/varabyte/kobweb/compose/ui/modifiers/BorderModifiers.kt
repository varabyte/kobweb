package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"border\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    )
)
fun Modifier.border(value: String) = styleModifier {
    property("border", value)
}

fun Modifier.border(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     border(width, style, color)
}

fun Modifier.borderColor(color: CSSColorValue) = styleModifier {
    borderColor(color)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"border-top\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    )
)

fun Modifier.borderTop(value: String) = styleModifier {
    property("border-top", value)
}

fun Modifier.borderTop(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     borderTop(width, style, color)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"border-bottom\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    )
)
fun Modifier.borderBottom(value: String) = styleModifier {
    property("border-bottom", value)
}

fun Modifier.borderBottom(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    borderBottom(width, style, color)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"border-left\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    )
)
fun Modifier.borderLeft(value: String) = styleModifier {
    property("border-left", value)
}

fun Modifier.borderLeft(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     borderLeft(width, style, color)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"border-right\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    )
)
fun Modifier.borderRight(value: String) = styleModifier {
    property("border-right", value)
}

fun Modifier.borderRight(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
     borderRight(width, style, color)
}

fun Modifier.borderRadius(r: CSSNumeric) = styleModifier {
    borderRadius(r)
}

fun Modifier.borderRadius(topLeftAndBottomRight: CSSNumeric, topRightAndBottomLeft: CSSNumeric) = styleModifier {
    borderRadius(topLeftAndBottomRight, topRightAndBottomLeft)
}

fun Modifier.borderRadius(
    topLeft: CSSNumeric,
    topRightAndBottomLeft: CSSNumeric,
    bottomRight: CSSNumeric
) = styleModifier {
    borderRadius(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderRadius(
    topLeft: CSSNumeric,
    topRight: CSSNumeric,
    bottomRight: CSSNumeric,
    bottomLeft: CSSNumeric
) = styleModifier {
    borderRadius(topLeft, topRight, bottomRight, bottomLeft)
}

fun Modifier.borderStyle(lineStyle: LineStyle) = styleModifier {
    borderStyle(lineStyle)
}

fun Modifier.borderWidth(width: CSSNumeric) = styleModifier {
    borderWidth(width)
}

fun Modifier.borderWidth(topLeft: CSSNumeric, bottomRight: CSSNumeric) = styleModifier {
    borderWidth(topLeft, bottomRight)
}

fun Modifier.borderWidth(
    topLeft: CSSNumeric,
    topRightAndBottomLeft: CSSNumeric,
    bottomRight: CSSNumeric
) = styleModifier {
    borderWidth(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderWidth(
    topLeft: CSSNumeric,
    topRight: CSSNumeric,
    bottomRight: CSSNumeric,
    bottomLeft: CSSNumeric
) = styleModifier {
    borderWidth(topLeft, topRight, bottomRight, bottomLeft)
}
