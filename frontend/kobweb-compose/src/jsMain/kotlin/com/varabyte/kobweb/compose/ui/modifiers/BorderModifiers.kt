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

class BorderScope internal constructor(private val styleScope: StyleScope, private val subproperty: String = "") {
    fun color(color: CSSColorValue) = styleScope.borderColor(color)
    fun style(lineStyle: LineStyle) = styleScope.borderStyle(lineStyle)
    fun width(width: CSSNumeric) = styleScope.borderWidth(width)
    fun width(topAndBottom: CSSNumeric, leftAndRight: CSSNumeric) = styleScope.borderWidth(topAndBottom, leftAndRight)
    fun width(top: CSSNumeric, leftAndRight: CSSNumeric, bottom: CSSNumeric) =
        styleScope.borderWidth(top, leftAndRight, bottom)

    fun width(top: CSSNumeric, right: CSSNumeric, bottom: CSSNumeric, left: CSSNumeric) =
        styleScope.borderWidth(top, right, bottom, left)
}

fun Modifier.border(scope: BorderScope.() -> Unit) = styleModifier {
    BorderScope(this).apply(scope)
}

fun Modifier.borderCollapse(borderCollapse: BorderCollapse) = styleModifier {
    borderCollapse(borderCollapse)
}

@Deprecated("Use border { color(...) } instead.", ReplaceWith("border { color(color) }"))
fun Modifier.borderColor(color: CSSColorValue) = border { color(color) }

class BorderSideScope internal constructor(private val styleScope: StyleScope, private val side: String) {
    fun color(color: CSSColorValue) = styleScope.property("border$side-color", color)
    fun style(lineStyle: LineStyle) = styleScope.property("border$side-style", lineStyle)
    fun width(width: CSSNumeric) = styleScope.property("border$side-width", width)
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

fun Modifier.borderTop(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-top").apply(scope)
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

fun Modifier.borderBottom(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-bottom").apply(scope)
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

fun Modifier.borderLeft(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-left").apply(scope)
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

fun Modifier.borderRight(scope: BorderSideScope.() -> Unit) = styleModifier {
    BorderSideScope(this, "-right").apply(scope)
}

fun Modifier.borderRadius(r: CSSNumeric) = styleModifier {
    borderRadius(r)
}

fun Modifier.borderRadius(topLeftAndBottomRight: CSSNumeric = 0.px, topRightAndBottomLeft: CSSNumeric = 0.px) =
    styleModifier {
        borderRadius(topLeftAndBottomRight, topRightAndBottomLeft)
    }

fun Modifier.borderRadius(
    topLeft: CSSNumeric = 0.px,
    topRightAndBottomLeft: CSSNumeric = 0.px,
    bottomRight: CSSNumeric = 0.px,
) = styleModifier {
    borderRadius(topLeft, topRightAndBottomLeft, bottomRight)
}

fun Modifier.borderRadius(
    topLeft: CSSNumeric = 0.px,
    topRight: CSSNumeric = 0.px,
    bottomRight: CSSNumeric = 0.px,
    bottomLeft: CSSNumeric = 0.px,
) = styleModifier {
    borderRadius(topLeft, topRight, bottomRight, bottomLeft)
}

@Deprecated("Use border { style(...) } instead.", ReplaceWith("border { style(lineStyle) }"))
fun Modifier.borderStyle(lineStyle: LineStyle) = border { style(lineStyle) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(width) }"))
fun Modifier.borderWidth(width: CSSNumeric) = border { width(width) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(topAndBottom, leftAndRight) }"))
fun Modifier.borderWidth(topAndBottom: CSSNumeric, leftAndRight: CSSNumeric) = border {
    width(topAndBottom, leftAndRight)
}

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(top, leftAndRight, bottom) }"))
fun Modifier.borderWidth(
    top: CSSNumeric,
    leftAndRight: CSSNumeric,
    bottom: CSSNumeric
) = border { width(top, leftAndRight, bottom) }

@Deprecated("Use border { width(...) } instead.", ReplaceWith("border { width(top, right, bottom, left) }"))
fun Modifier.borderWidth(
    top: CSSNumeric,
    right: CSSNumeric,
    bottom: CSSNumeric,
    left: CSSNumeric
) = border { width(top, right, bottom, left) }
