package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// NOTE: This class is used as a typealias and should not be referenced directly by the end user.
class CSSElementSize private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<CSSElementSize> {
        fun of(value: CSSLengthOrPercentageNumericValue) = CSSElementSize("$value")
        fun of(width: CSSAutoKeyword) = CSSElementSize("$width")

        // Keyword
        @Suppress("FunctionName")
        fun FitContent(value: CSSLengthOrPercentageNumericValue) = CSSElementSize("fit-content($value)")
        val FitContent get() = CSSElementSize("fit-content")
        val MaxContent get() = CSSElementSize("max-content")
        val MinContent get() = CSSElementSize("min-content")
    }
}

class CSSElementMaxSize private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object : CssGlobalValues<CSSElementMaxSize> {
        fun of(value: CSSLengthOrPercentageNumericValue) = CSSElementMaxSize("$value")
        fun of(width: CSSAutoKeyword) = CSSElementMaxSize("$width")

        // Keyword
        val None get() = CSSElementMaxSize("none")
        @Suppress("FunctionName")
        fun FitContent(value: CSSLengthOrPercentageNumericValue) = CSSElementMaxSize("fit-content($value)")
        val FitContent get() = CSSElementMaxSize("fit-content")
        val MaxContent get() = CSSElementMaxSize("max-content")
        val MinContent get() = CSSElementMaxSize("min-content")
    }
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/width
typealias Width = CSSElementSize

fun AttrsScope<*>.width(width: Int) {
    attr("width", width.toString())
}

fun StyleScope.width(width: Width) {
    property("width", width)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/min-width
typealias MinWidth = CSSElementSize

fun StyleScope.minWidth(minWidth: MinWidth) {
    property("min-width", minWidth)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/height
typealias Height = CSSElementSize

fun AttrsScope<*>.height(height: Int) {
    attr("height", height.toString())
}

fun StyleScope.height(height: Height) {
    property("height", height)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/min-height
typealias MinHeight = CSSElementSize

fun StyleScope.minHeight(minHeight: MinHeight) {
    property("min-height", minHeight)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-width
typealias MaxWidth = CSSElementMaxSize

fun StyleScope.maxWidth(maxWidth: MaxWidth) {
    property("max-width", maxWidth)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-height
typealias MaxHeight = CSSElementMaxSize

fun StyleScope.maxHeight(maxHeight: MaxHeight) {
    property("max-height", maxHeight)
}
