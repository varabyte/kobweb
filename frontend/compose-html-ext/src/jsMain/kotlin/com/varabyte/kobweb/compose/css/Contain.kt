package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// https://developer.mozilla.org/en-US/docs/Web/CSS/contain
sealed class Contain(private val value: String) : StylePropertyValue {

    override fun toString() = value

    sealed class SingleValue(value: String) : Contain(value)
    class RepeatableValue internal constructor(value: String) : SingleValue(value)
    private class Keyword(value: String) : SingleValue(value)
    private class TwoValue(width: RepeatableValue, height: RepeatableValue) : Contain("$width $height")
    private class AutoSingleValue(value: String) : Contain("auto $value")

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

fun StyleScope.contain(vararg value: Contain.SingleValue) {
    property("contain", value.joinToString(" "))
}

fun StyleScope.containIntrinsicBlockSize(containIntrinsicBlockSize: Contain.SingleValue) {
    property("contain-intrinsic-block-size", containIntrinsicBlockSize)
}

fun StyleScope.containIntrinsicInlineSize(containIntrinsicInlineSize: Contain.SingleValue) {
    property("contain-intrinsic-inline-size", containIntrinsicInlineSize)
}

fun StyleScope.containIntrinsicSize(containIntrinsicSize: Contain.SingleValue) {
    property("contain-intrinsic-size", containIntrinsicSize)
}

fun StyleScope.containIntrinsicWidth(containIntrinsicWidth: Contain.SingleValue) {
    property("contain-intrinsic-width", containIntrinsicWidth)
}

fun StyleScope.containIntrinsicHeight(containIntrinsicHeight: Contain.SingleValue) {
    property("contain-intrinsic-height", containIntrinsicHeight)
}