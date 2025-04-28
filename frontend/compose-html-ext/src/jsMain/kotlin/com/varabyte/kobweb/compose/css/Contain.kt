package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain
sealed class Contain(private val value: String) : StylePropertyValue {

    override fun toString() = value

    sealed class RepeatableValue(value: String) : Contain(value)
    private class Keyword(value: String) : RepeatableValue(value)

    companion object {
        /* Keyword values */
        val None: Contain get() = Keyword("none")
        val Strict: Contain get() = Keyword("strict")
        val Content: Contain get() = Keyword("content")
        val Size: Contain get() = Keyword("size")
        val InlineSize: Contain get() = Keyword("inline-size")
        val Layout: Contain get() = Keyword("layout")
        val Style: Contain get() = Keyword("style")
        val Paint: Contain get() = Keyword("paint")

        /* Multiple keywords */
        fun of(vararg value: RepeatableValue): Contain = Keyword("$value")

        /* Global values */
        val Inherit: Contain get() = Keyword("inherit")
        val Initial: Contain get() = Keyword("initial")
        val Revert: Contain get() = Keyword("revert")
        val RevertLayer: Contain get() = Keyword("revert-layer")
        val Unset: Contain get() = Keyword("unset")
    }
}

fun StyleScope.contain(contain: Contain) {
    property("contain", contain)
}

fun StyleScope.contain(vararg values: Contain.RepeatableValue) {
    if (values.isNotEmpty()) {
        property("contain", values.joinToString(" "))
    }
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-block-size
class ContainIntrinsicBlockSize private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {
        /* Keyword Values */
        val None get() = ContainIntrinsicBlockSize("none")

        /* auto <length>  & <length> values */
        fun of(value: CSSLengthNumericValue, auto: Boolean = false) = if (!auto) {
            ContainIntrinsicBlockSize("$value")
        } else {
            ContainIntrinsicBlockSize("auto $value")

        }

        /* Global values */
        val Inherit get() = ContainIntrinsicBlockSize("inherit")
        val Initial get() = ContainIntrinsicBlockSize("initial")
        val Revert get() = ContainIntrinsicBlockSize("revert")
        val RevertLayer get() = ContainIntrinsicBlockSize("revert-layer")
        val Unset get() = ContainIntrinsicBlockSize("unset")

    }
}

fun StyleScope.containIntrinsicBlockSize(containIntrinsicBlockSize: ContainIntrinsicBlockSize) {
    property("contain-intrinsic-block-size", containIntrinsicBlockSize)
}


// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-inline-size
class ContainIntrinsicInlineSize private constructor(private val value: String) : StylePropertyValue {

    override fun toString() = value

    companion object {
        /* Keyword Values */
        val None get() = ContainIntrinsicInlineSize("none")

        /* auto <length>  & <length> values */
        fun of(length: CSSLengthNumericValue, auto: Boolean = false) = if (!auto) {
            ContainIntrinsicInlineSize("$length")
        } else {
            ContainIntrinsicInlineSize("auto $length")
        }

        /* Global values */
        val Inherit get() = ContainIntrinsicInlineSize("inherit")
        val Initial get() = ContainIntrinsicInlineSize("initial")
        val Revert get() = ContainIntrinsicInlineSize("revert")
        val RevertLayer get() = ContainIntrinsicInlineSize("revert-layer")
        val Unset get() = ContainIntrinsicInlineSize("unset")

    }
}

fun StyleScope.containIntrinsicInlineSize(containIntrinsicInlineSize: ContainIntrinsicInlineSize) {
    property("contain-intrinsic-inline-size", containIntrinsicInlineSize)
}