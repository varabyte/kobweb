package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline
@Deprecated("Use `Outline` instead. The name `CSSOutline` was chosen to mimic a similar class in Compose HTML but we have since come up with new, broader, consistent standards to apply to these CSS classes, and `Outline` better matches the source CSS property it comes from.", ReplaceWith("Outline"))
@Suppress("EqualsOrHashCode", "DEPRECATION")
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

@Deprecated("Use `outline(Outline.of(...)` instead.")
fun StyleScope.outline(outlineBuilder: CSSOutline.() -> Unit) {
    val outlineValues = CSSOutline().apply(outlineBuilder)
    outline(
        Outline.of(
            outlineValues.width?.let { OutlineWidth.of(it) },
            outlineValues.style,
            outlineValues.color,
        )
    )
}

@Deprecated("Use `outline(Outline.of(...)` instead.")
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

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-offset
fun StyleScope.outlineOffset(value: CSSLengthNumericValue) {
    property("outline-offset", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-width
class OutlineWidth private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        fun of(value: CSSLengthNumericValue) = OutlineWidth(value.toString())

        // Keyword
        val Thin get() = OutlineWidth("thin")
        val Medium get() = OutlineWidth("medium")
        val Thick get() = OutlineWidth("thick")

        // Global
        val Inherit get() = OutlineWidth("inherit")
        val Initial get() = OutlineWidth("initial")
        val Revert get() = OutlineWidth("revert")
        val Unset get() = OutlineWidth("unset")
    }
}

fun StyleScope.outlineWidth(outlineWidth: OutlineWidth) {
    property("outline-width", outlineWidth)
}

fun StyleScope.outlineWidth(value: CSSLengthNumericValue) {
    property("outline-width", value)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/outline
// Eventually deprecate CSSOutline?
class Outline private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value
    companion object {
        fun of(outlineWidth: OutlineWidth? = null, outlineStyle: LineStyle? = null, outlineColor: CSSColorValue? = null) =
            Outline(listOfNotNull(outlineWidth, outlineStyle, outlineColor).joinToString(" "))

        // Global
        val Inherit get() = Outline("inherit")
        val Initial get() = Outline("initial")
        val Revert get() = Outline("revert")
        val Unset get() = Outline("unset")
    }
}

fun StyleScope.outline(outline: Outline) {
    property("outline", outline)
}