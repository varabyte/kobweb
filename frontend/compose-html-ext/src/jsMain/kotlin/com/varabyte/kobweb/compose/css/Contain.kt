package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain
sealed interface Contain : StylePropertyValue {

    sealed interface Listable : Contain

    companion object : CssGlobalValues<Contain> {
        /* Keyword values */
        val None: Contain get() = "none".unsafeCast<Contain>()
        val Strict: Contain get() = "strict".unsafeCast<Contain>()
        val Content: Contain get() = "content".unsafeCast<Contain>()
        val Size: Contain get() = "size".unsafeCast<Contain>()
        val InlineSize: Contain get() = "inline-size".unsafeCast<Contain>()
        val Layout: Contain get() = "layout".unsafeCast<Contain>()
        val Style: Contain get() = "style".unsafeCast<Contain>()
        val Paint: Contain get() = "paint".unsafeCast<Contain>()

        /* Multiple keywords */
        private fun toContent(values: List<Listable>) = buildString {
            if (values.isEmpty()) return@buildString
            append(values.joinToString(" "))
        }.unsafeCast<Contain>()

        fun of(value: Contain): Listable = value.unsafeCast<Listable>()
        fun list(vararg values: Listable): Contain = toContent(values.toList())
    }
}

fun StyleScope.contain(contain: Contain) {
    property("contain", contain)
}

fun StyleScope.contain(vararg values: Contain.Listable) {
    contain(values.unsafeCast<Contain>())
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-block-size
sealed interface ContainIntrinsicBlockSize : StylePropertyValue {

    companion object : CssGlobalValues<ContainIntrinsicBlockSize> {
        /* Keyword Values */
        val None get() = "none".unsafeCast<ContainIntrinsicBlockSize>()

        /* auto <length>  & <length> values */
        fun of(value: CSSLengthNumericValue, auto: Boolean = false) = if (!auto) {
            value.unsafeCast<ContainIntrinsicBlockSize>()
        } else {
            buildString {
                append("auto ")
                append(value)

            }.unsafeCast<ContainIntrinsicBlockSize>()

        }
    }
}

fun StyleScope.containIntrinsicBlockSize(containIntrinsicBlockSize: ContainIntrinsicBlockSize) {
    property("contain-intrinsic-block-size", containIntrinsicBlockSize)
}


// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-inline-size
sealed interface ContainIntrinsicInlineSize : StylePropertyValue {

    companion object : CssGlobalValues<ContainIntrinsicInlineSize> {
        /* Keyword Values */
        val None get() = "none".unsafeCast<ContainIntrinsicInlineSize>()

        /* auto <length>  & <length> values */
        fun of(length: CSSLengthNumericValue, auto: Boolean = false) = if (!auto) {
            length.unsafeCast<ContainIntrinsicInlineSize>()
        } else {
            buildString {
                append("auto ")
                append(length)
            }.unsafeCast<ContainIntrinsicInlineSize>()
        }
    }
}

fun StyleScope.containIntrinsicInlineSize(containIntrinsicInlineSize: ContainIntrinsicInlineSize) {
    property("contain-intrinsic-inline-size", containIntrinsicInlineSize)
}