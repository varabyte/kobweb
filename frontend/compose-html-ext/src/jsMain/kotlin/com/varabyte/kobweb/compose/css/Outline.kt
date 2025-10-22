package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

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
