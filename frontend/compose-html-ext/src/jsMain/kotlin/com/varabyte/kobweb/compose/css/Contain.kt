@file:Suppress("PropertyName", "FunctionName")

package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

internal sealed interface CssContainIntrinsicValues<T : StylePropertyValue> {
    fun of(value: CSSLengthNumericValue) = "$value".unsafeCast<T>()

    // Keywords
    val None get() = "none".unsafeCast<T>()

    fun Auto(value: CSSLengthNumericValue) = "auto $value".unsafeCast<T>()

    /**
     * A value that allows the "contain intrinsic" element to fall back to `"none"` if no remembered value exists.
     *
     * Official documentation recommends preferring `"auto none"` over `"auto 0px"`.
     */
    val AutoNone get() = "auto none".unsafeCast<T>()
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain
sealed interface Contain : StylePropertyValue {
    sealed interface Listable : Contain

    companion object : CssGlobalValues<Contain> {
        fun list(vararg values: Listable): Contain = values.joinToString(" ").unsafeCast<Contain>()

        // Keywords
        val None: Contain get() = "none".unsafeCast<Contain>()
        val Strict: Contain get() = "strict".unsafeCast<Contain>()
        val Content: Contain get() = "content".unsafeCast<Contain>()
        val Size: Listable get() = "size".unsafeCast<Listable>()
        val InlineSize: Listable get() = "inline-size".unsafeCast<Listable>()
        val Layout: Listable get() = "layout".unsafeCast<Listable>()
        val Style: Listable get() = "style".unsafeCast<Listable>()
        val Paint: Listable get() = "paint".unsafeCast<Listable>()
    }
}

fun StyleScope.contain(contain: Contain) {
    property("contain", contain)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-block-size
sealed interface ContainIntrinsicBlockSize : StylePropertyValue {
    companion object : CssContainIntrinsicValues<ContainIntrinsicBlockSize>, CssGlobalValues<ContainIntrinsicBlockSize>
}

fun StyleScope.containIntrinsicBlockSize(containIntrinsicBlockSize: ContainIntrinsicBlockSize) {
    property("contain-intrinsic-block-size", containIntrinsicBlockSize)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-inline-size
sealed interface ContainIntrinsicInlineSize : StylePropertyValue {
    companion object : CssContainIntrinsicValues<ContainIntrinsicInlineSize>,
        CssGlobalValues<ContainIntrinsicInlineSize>
}

fun StyleScope.containIntrinsicInlineSize(containIntrinsicInlineSize: ContainIntrinsicInlineSize) {
    property("contain-intrinsic-inline-size", containIntrinsicInlineSize)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-width
sealed interface ContainIntrinsicWidth : StylePropertyValue {
    companion object : CssContainIntrinsicValues<ContainIntrinsicWidth>, CssGlobalValues<ContainIntrinsicWidth>
}

fun StyleScope.containIntrinsicWidth(containIntrinsicWidth: ContainIntrinsicWidth) {
    property("contain-intrinsic-width", containIntrinsicWidth)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-height
sealed interface ContainIntrinsicHeight : StylePropertyValue {
    companion object : CssContainIntrinsicValues<ContainIntrinsicHeight>, CssGlobalValues<ContainIntrinsicHeight>
}

fun StyleScope.containIntrinsicHeight(containIntrinsicHeight: ContainIntrinsicHeight) {
    property("contain-intrinsic-height", containIntrinsicHeight)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-size
sealed interface ContainIntrinsicSize : StylePropertyValue {
    companion object : CssContainIntrinsicValues<ContainIntrinsicSize>, CssGlobalValues<ContainIntrinsicSize> {
        fun of(width: CSSLengthNumericValue, height: CSSLengthNumericValue) = "$width $height".unsafeCast<ContainIntrinsicSize>()
        fun of(width: ContainIntrinsicWidth, height: CSSLengthNumericValue) = "$width $height".unsafeCast<ContainIntrinsicSize>()
        fun of(width: CSSLengthNumericValue, height: ContainIntrinsicHeight) = "$width $height".unsafeCast<ContainIntrinsicSize>()
        fun of(width: ContainIntrinsicWidth, height: ContainIntrinsicHeight) = "$width $height".unsafeCast<ContainIntrinsicSize>()
    }
}

fun StyleScope.containIntrinsicSize(containIntrinsicSize: ContainIntrinsicSize) {
    property("contain-intrinsic-size", containIntrinsicSize)
}

