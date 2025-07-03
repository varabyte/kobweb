package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

class OutlineScope internal constructor(private val styleScope: StyleScope) {
    fun color(color: CSSColorValue) = styleScope.outlineColor(color)
    fun style(style: LineStyle) = styleScope.outlineStyle(style)
    fun width(width: CSSLengthNumericValue) = styleScope.outlineWidth(width)
    fun width(width: OutlineWidth) = styleScope.outlineWidth(width)
}

fun Modifier.outline(outline: Outline) = styleModifier {
    outline(outline)
}

fun Modifier.outline(scope: OutlineScope.() -> Unit) = styleModifier {
    OutlineScope(this).apply(scope)
}

fun Modifier.outline(width: CSSLengthNumericValue? = null, style: LineStyle? = null, color: CSSColorValue? = null) = styleModifier {
    outline(Outline.of(width?.let { OutlineWidth.of(it) }, style, color))
}

// Deprecation message was updated but safe to delete 10/24/25 or after
@Deprecated("Use `Modifier.outline { color(...) }` instead.", ReplaceWith("outline { color(value) }"))
fun Modifier.outlineColor(value: CSSColorValue) = outline { color(value) }

fun Modifier.outlineOffset(value: CSSLengthNumericValue) = styleModifier {
    outlineOffset(value)
}

// Deprecation message was updated but safe to delete 10/24/25 or after
@Deprecated("Use `Modifier.outline { style(...) }` instead.", ReplaceWith("outline { style(value) }"))
fun Modifier.outlineStyle(value: LineStyle) = outline { style(value) }

// Deprecation message was updated but safe to delete 10/24/25 or after
@Deprecated("Use `Modifier.outline { style(...) }` instead.", ReplaceWith("outline { width(outlineWidth) }"))
fun Modifier.outlineWidth(outlineWidth: OutlineWidth) = outline { width(outlineWidth) }

// Deprecation message was updated but safe to delete 10/24/25 or after
@Deprecated("Use `Modifier.outline { width(...) }` instead.", ReplaceWith("outline { width(value) }"))
fun Modifier.outlineWidth(value: CSSLengthNumericValue) = outline { width(value) }
