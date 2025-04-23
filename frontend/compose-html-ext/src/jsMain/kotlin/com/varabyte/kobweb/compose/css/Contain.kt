package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain
class Contain private constructor(private val value: String) : StylePropertyValue {

    companion object {
        /* Keyword values */
        val None get() = Contain("none")
        val Strict get() = Contain("strict")
        val Content get() = Contain("content")
        val Size get() = Contain("size")
        val InlineSize get() = Contain("inline-size")
        val Layout get() = Contain("layout")
        val Style get() = Contain("style")
        val Paint get() = Contain("paint")

        /* Multiple keywords */
        fun of(firstValue: String, secondValue: String) = Contain("$firstValue $secondValue")
        fun of(firstValue: String, secondValue: String, thirdValue: String) =
            Contain("$firstValue $secondValue $thirdValue")

        /* Global values */
        val Inherit get() = Contain("inherit")
        val Initial get() = Contain("initial")
        val Revert get() = Contain("revert")
        val RevertLayer get() = Contain("revert-layer")
        val Unset get() = Contain("unset")
    }
}

fun StyleScope.contain(contain: Contain) {
    property("contain", contain)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-block-size
class ContainIntrinsicBlockSize private constructor(private val value: String) : StylePropertyValue {

    companion object {
        /* Keyword Values */
        val None get() = ContainIntrinsicBlockSize("none")

        /* auto <length>  & <length> values */
        fun of(length: CSSLengthNumericValue, isAuto: Boolean = false) = if (!isAuto) {
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

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-height
class ContainIntrinsicHeight private constructor(private val value: String) : StylePropertyValue {

    companion object {
        /* Keyword Values */
        val None get() = ContainIntrinsicHeight("none")

        /* auto <length>  & <length> values */
        fun of(length: CSSLengthNumericValue, isAuto: Boolean = false) = if (!isAuto) {
            ContainIntrinsicHeight("auto $length")
        } else {
            ContainIntrinsicHeight("$length")
        }

        /* Global values */
        val Inherit get() = ContainIntrinsicHeight("inherit")
        val Initial get() = ContainIntrinsicHeight("initial")
        val Revert get() = ContainIntrinsicHeight("revert")
        val RevertLayer get() = ContainIntrinsicHeight("revert-layer")
        val Unset get() = ContainIntrinsicHeight("unset")

    }
}

fun StyleScope.containIntrinsicHeight(containIntrinsicHeight: ContainIntrinsicHeight) {
    property("contain-intrinsic-height", containIntrinsicHeight)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-inline-size
class ContainIntrinsicInlineSize private constructor(private val value: String) : StylePropertyValue {

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

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-size
class ContainIntrinsicSize private constructor(private val value: String) : StylePropertyValue {

    companion object {
        /* Keyword Values */
        val None get() = ContainIntrinsicSize("none")

        /* auto <length>  & <length> values */
        fun of(length: CSSLengthNumericValue, isAuto: Boolean = false) = if (isAuto) {
            ContainIntrinsicSize("auto $length")
        } else {
            ContainIntrinsicSize("$length")
        }

        /* width | height & auto width | auto height */
        fun of(width: CSSLengthNumericValue, height: CSSLengthNumericValue, isAuto: Boolean = false) = if (isAuto) {
            ContainIntrinsicSize("auto $width auto $height")
        } else {
            ContainIntrinsicSize("$width $height")
        }


        /* Global values */
        val Inherit get() = ContainIntrinsicSize("inherit")
        val Initial get() = ContainIntrinsicSize("initial")
        val Revert get() = ContainIntrinsicSize("revert")
        val RevertLayer get() = ContainIntrinsicSize("revert-layer")
        val Unset get() = ContainIntrinsicSize("unset")

    }
}

fun StyleScope.containIntrinsicSize(containIntrinsicSize: ContainIntrinsicSize) {
    property("contain-intrinsic-size", containIntrinsicSize)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain-intrinsic-width
class ContainIntrinsicWidth private constructor(private val value: String) : StylePropertyValue {

    companion object {
        /* Keyword Values */
        val None get() = ContainIntrinsicWidth("none")

        /* auto <length>  & <length> values */
        fun of(length: CSSLengthNumericValue, isAuto: Boolean = false) = if (!isAuto) {
            ContainIntrinsicWidth("auto $length")
        } else {
            ContainIntrinsicWidth("$length")
        }

        /* Global values */
        val Inherit get() = ContainIntrinsicWidth("inherit")
        val Initial get() = ContainIntrinsicWidth("initial")
        val Revert get() = ContainIntrinsicWidth("revert")
        val RevertLayer get() = ContainIntrinsicWidth("revert-layer")
        val Unset get() = ContainIntrinsicWidth("unset")

    }
}

fun StyleScope.containIntrinsicWidth(containIntrinsicWidth: ContainIntrinsicWidth) {
    property("contain-intrinsic-width", containIntrinsicWidth)
}