package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline
@Suppress("EqualsOrHashCode")
class CSSOutline internal constructor() : CSSStyleValue {
    var width: CSSLengthNumericValue? = null
    var style: LineStyle? = null
    var color: CSSColorValue? = null

    override fun equals(other: Any?): Boolean {
        return if (other is CSSOutline) {
            width == other.width && style == other.style && color == other.color
        } else false
    }

    override fun toString(): String {
        val values = listOfNotNull(color, style, width)
        return values.joinToString(" ")
    }
}

fun StyleScope.outline(outlineBuilder: CSSOutline.() -> Unit) {
    property("outline", CSSOutline().apply(outlineBuilder))
}

fun StyleScope.outline(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    outline {
        this.width = width
        this.style = style
        this.color = color
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-color
class OutlineColor private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<OutlineColor> {
        // Keyword
        val Invert get() = OutlineColor("invert")
    }
}

fun StyleScope.outlineColor(outlineColor: OutlineColor) {
    property("outline-color", outlineColor)
}

fun StyleScope.outlineColor(value: CSSColorValue) {
    property("outline-color", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-offset
fun StyleScope.outlineOffset(value: CSSLengthNumericValue) {
    property("outline-offset", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-style
fun StyleScope.outlineStyle(value: LineStyle) {
    property("outline-style", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-width
class OutlineWidth private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<OutlineWidth> {
        // Keyword
        val Thin get() = OutlineWidth("thin")
        val Medium get() = OutlineWidth("medium")
        val Thick get() = OutlineWidth("thick")
    }
}

fun StyleScope.outlineWidth(outlineWidth: OutlineWidth) {
    property("outline-width", outlineWidth)
}

fun StyleScope.outlineWidth(value: CSSLengthNumericValue) {
    property("outline-width", value)
}
