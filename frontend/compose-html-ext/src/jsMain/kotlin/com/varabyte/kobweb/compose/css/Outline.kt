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

@Suppress("DEPRECATION")
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
    @Suppress("DEPRECATION")
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

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-style
fun StyleScope.outlineStyle(value: LineStyle) {
    property("outline-style", value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/outline-width
sealed interface OutlineWidth : StylePropertyValue {
    companion object : CssGlobalValues<OutlineWidth> {
        fun of(value: CSSLengthNumericValue) = value.toString().unsafeCast<OutlineWidth>()

        // Keyword
        val Thin get() = "thin".unsafeCast<OutlineWidth>()
        val Medium get() = "medium".unsafeCast<OutlineWidth>()
        val Thick get() = "thick".unsafeCast<OutlineWidth>()
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
sealed interface Outline : StylePropertyValue {
    companion object : CssGlobalValues<Outline> {
        fun of(outlineWidth: OutlineWidth? = null, outlineStyle: LineStyle? = null, outlineColor: CSSColorValue? = null) =
            listOfNotNull(outlineWidth, outlineStyle, outlineColor).joinToString(" ").unsafeCast<Outline>()

        fun of(outlineWidth: CSSLengthNumericValue, outlineStyle: LineStyle? = null, outlineColor: CSSColorValue? = null) =
            of(OutlineWidth.of(outlineWidth), outlineStyle, outlineColor)
    }
}

fun StyleScope.outline(outline: Outline) {
    property("outline", outline)
}