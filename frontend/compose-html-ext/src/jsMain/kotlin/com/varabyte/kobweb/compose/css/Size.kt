package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

// NOTE: This class is used as a typealias and should not be referenced directly by the end user.
sealed interface CSSElementSize : StylePropertyValue {
    companion object : CssGlobalValues<CSSElementSize> {
        fun of(value: CSSLengthOrPercentageNumericValue) = "$value".unsafeCast<CSSElementSize>()
        fun of(width: CSSAutoKeyword) = "$width".unsafeCast<CSSElementSize>()

        // Keyword
        @Suppress("FunctionName")
        fun FitContent(value: CSSLengthOrPercentageNumericValue) = "fit-content($value)".unsafeCast<CSSElementSize>()
        val FitContent get() = "fit-content".unsafeCast<CSSElementSize>()
        val MaxContent get() = "max-content".unsafeCast<CSSElementSize>()
        val MinContent get() = "min-content".unsafeCast<CSSElementSize>()
    }
}

sealed interface CSSElementMaxSize : StylePropertyValue {
    companion object : CssGlobalValues<CSSElementMaxSize> {
        fun of(value: CSSLengthOrPercentageNumericValue) = "$value".unsafeCast<CSSElementMaxSize>()
        fun of(width: CSSAutoKeyword) = "$width".unsafeCast<CSSElementMaxSize>()

        // Keyword
        val None get() = "none".unsafeCast<CSSElementMaxSize>()
        @Suppress("FunctionName")
        fun FitContent(value: CSSLengthOrPercentageNumericValue) = "fit-content($value)".unsafeCast<CSSElementMaxSize>()
        val FitContent get() = "fit-content".unsafeCast<CSSElementMaxSize>()
        val MaxContent get() = "max-content".unsafeCast<CSSElementMaxSize>()
        val MinContent get() = "min-content".unsafeCast<CSSElementMaxSize>()
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
