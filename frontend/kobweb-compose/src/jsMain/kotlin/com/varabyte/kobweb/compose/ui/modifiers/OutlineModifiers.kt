package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

class OutlineScope internal constructor(private val styleScope: StyleScope) {
    fun color(color: CSSColorValue) = styleScope.property("outline-color", color)
    fun style(style: LineStyle) = styleScope.property("outline-style", style)
    fun width(width: CSSLengthNumericValue) = styleScope.outlineWidth(width)
    fun width(width: OutlineWidth) = styleScope.outlineWidth(width)
}

fun Modifier.outline(outline: Outline) = styleModifier {
    outline(outline)
}

fun Modifier.outline(scope: OutlineScope.() -> Unit) = styleModifier {
    OutlineScope(this).apply(scope)
}

@Deprecated("Use `Modifier.outline { ... }` instead.")
fun Modifier.outline(width: CSSLengthNumericValue? = null, style: LineStyle? = null, color: CSSColorValue? = null) =
    styleModifier {
        outline(width, style, color)
    }


@Deprecated("Use `Modifier.outline { ... }` instead.", ReplaceWith("outline { color(value) }"))
fun Modifier.outlineColor(value: CSSColorValue) = outline { color(value) }


fun Modifier.outlineOffset(value: CSSLengthNumericValue) = styleModifier {
    outlineOffset(value)
}

@Deprecated("Use `Modifier.outline { ... }` instead.", ReplaceWith("outline { style(value) }"))
fun Modifier.outlineStyle(value: LineStyle) = outline { style(value) }

@Deprecated("Use `Modifier.outline { ... }` instead.", ReplaceWith("outline { width(value) }"))
fun Modifier.outlineWidth(outlineWidth: OutlineWidth) = outline { width(outlineWidth) }

@Deprecated("Use `Modifier.outline { ... }` instead.", ReplaceWith("outline { width(value) }"))
fun Modifier.outlineWidth(value: CSSLengthNumericValue) = outline { width(value) }
