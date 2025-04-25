package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain
sealed class Contain(private val value: String) : StylePropertyValue {

    override fun toString() = value

    sealed class SingleValue(value: String) : Contain(value)
    class RepeatableValue internal constructor(value: String) : SingleValue(value)
    private class Keyword(value: String) : SingleValue(value)

    companion object {
        /* Keyword values */
        val None get() = RepeatableValue("none")
        val Strict get() = RepeatableValue("strict")
        val Content get() = RepeatableValue("content")
        val Size get() = RepeatableValue("size")
        val InlineSize get() = RepeatableValue("inline-size")
        val Layout get() = RepeatableValue("layout")
        val Style get() = RepeatableValue("style")
        val Paint get() = RepeatableValue("paint")

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

fun StyleScope.contain(vararg values: Contain.SingleValue) {
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
        fun of(length: CSSLengthNumericValue, auto: Boolean = false) = if (!auto) {
            ContainIntrinsicBlockSize("auto $length")
        } else {
            ContainIntrinsicBlockSize("$length")
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
        fun of(length: CSSLengthNumericValue, isAuto: Boolean = false) = if (!isAuto) {
            ContainIntrinsicInlineSize("auto $length")
        } else {
            ContainIntrinsicInlineSize("$length")
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