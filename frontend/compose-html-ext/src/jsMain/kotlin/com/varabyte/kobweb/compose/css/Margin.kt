package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

class CSSMargin internal constructor(
    val top: CSSLengthOrPercentageNumericValue = 0.px,
    val right: CSSLengthOrPercentageNumericValue = 0.px,
    val bottom: CSSLengthOrPercentageNumericValue = 0.px,
    val left: CSSLengthOrPercentageNumericValue = 0.px
) {
    override fun toString(): String {
        return "$top $right $bottom $left"
    }
}

fun margin(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    right: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
    left: CSSLengthOrPercentageNumericValue = 0.px
) = CSSMargin(top, right, bottom, left)

fun margin(topBottom: CSSLengthOrPercentageNumericValue = 0.px, leftRight: CSSLengthOrPercentageNumericValue = 0.px) =
    margin(topBottom, leftRight, topBottom, leftRight)

fun margin(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    leftRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px
) = margin(top, leftRight, bottom, leftRight)

fun margin(all: CSSLengthOrPercentageNumericValue) = margin(all, all, all, all)
