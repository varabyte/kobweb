package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.browser.dom.css.CssPropertyName
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

private fun CssPropertyName.stretchKeyword() = firstSupportedValue("stretch", "-webkit-fill-available")

internal sealed interface CssSizeValues<T: StylePropertyValue> {
    fun of(value: CSSLengthOrPercentageNumericValue) = "$value".unsafeCast<T>()
    fun of(auto: CSSAutoKeyword) = "$auto".unsafeCast<T>()

    // Keyword
    // Not widely supported: https://caniuse.com/mdn-css_properties_width_fit-content_function
    // fun FitContent(value: CSSLengthOrPercentageNumericValue) = "fit-content($value)".unsafeCast<T>()
    val FitContent get() = "fit-content".unsafeCast<T>()
    val MaxContent get() = "max-content".unsafeCast<T>()
    val MinContent get() = "min-content".unsafeCast<T>()
}

internal sealed interface CssMaxSizeValues<T: StylePropertyValue> {
    fun of(value: CSSLengthOrPercentageNumericValue) = "$value".unsafeCast<T>()

    // Keyword
    val None get() = "none".unsafeCast<T>()

    // Not widely supported: https://caniuse.com/mdn-css_properties_max-width_fit-content_function
    // fun FitContent(value: CSSLengthOrPercentageNumericValue) = "fit-content($value)".unsafeCast<T>()
    val FitContent get() = "fit-content".unsafeCast<T>()
    val MaxContent get() = "max-content".unsafeCast<T>()
    val MinContent get() = "min-content".unsafeCast<T>()
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/block-size
sealed interface BlockSize : StylePropertyValue {
    companion object : CssSizeValues<BlockSize>, CssGlobalValues<BlockSize> {
        // Stretch is not currently supported across all browsers
        // https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/block-size#browser_compatibility
    }
}

fun StyleScope.blockSize(blockSize: BlockSize) {
    property("block-size", blockSize)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/width
sealed interface Width : StylePropertyValue {
    companion object : CssSizeValues<Width>, CssGlobalValues<Width> {
        val Stretch get() = CssPropertyName("width").stretchKeyword().unsafeCast<Width>()
    }
}

fun AttrsScope<*>.width(width: Int) {
    attr("width", width.toString())
}

fun StyleScope.width(width: Width) {
    property("width", width)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/min-width
sealed interface MinWidth : StylePropertyValue {
    companion object : CssSizeValues<MinWidth>, CssGlobalValues<MinWidth>
}

fun StyleScope.minWidth(minWidth: MinWidth) {
    property("min-width", minWidth)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/height
sealed interface Height : StylePropertyValue {
    companion object : CssSizeValues<Height>, CssGlobalValues<Height> {
        val Stretch get() = CssPropertyName("height").stretchKeyword().unsafeCast<Height>()
    }
}

fun AttrsScope<*>.height(height: Int) {
    attr("height", height.toString())
}

fun StyleScope.height(height: Height) {
    property("height", height)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/min-height
sealed interface MinHeight : StylePropertyValue {
    companion object : CssSizeValues<MinHeight>, CssGlobalValues<MinHeight>
}


fun StyleScope.minHeight(minHeight: MinHeight) {
    property("min-height", minHeight)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-width
sealed interface MaxWidth : StylePropertyValue {
    companion object : CssMaxSizeValues<MaxWidth>, CssGlobalValues<MaxWidth>
}

fun StyleScope.maxWidth(maxWidth: MaxWidth) {
    property("max-width", maxWidth)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-height
sealed interface MaxHeight : StylePropertyValue {
    companion object : CssMaxSizeValues<MaxHeight>, CssGlobalValues<MaxHeight>
}

fun StyleScope.maxHeight(maxHeight: MaxHeight) {
    property("max-height", maxHeight)
}
