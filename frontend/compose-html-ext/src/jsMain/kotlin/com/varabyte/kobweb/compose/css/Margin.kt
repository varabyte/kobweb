package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

class CSSMargin internal constructor(
    val top: CSSLengthOrPercentageValue = 0.px,
    val right: CSSLengthOrPercentageValue = 0.px,
    val bottom: CSSLengthOrPercentageValue = 0.px,
    val left: CSSLengthOrPercentageValue = 0.px
) {
    override fun toString(): String {
        return "$top $right $bottom $left"
    }
}

fun margin(
    top: CSSLengthOrPercentageValue = 0.px,
    right: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px,
    left: CSSLengthOrPercentageValue = 0.px
) = CSSMargin(top, right, bottom, left)

fun margin(topBottom: CSSLengthOrPercentageValue = 0.px, leftRight: CSSLengthOrPercentageValue = 0.px) =
    margin(topBottom, leftRight, topBottom, leftRight)

fun margin(
    top: CSSLengthOrPercentageValue = 0.px,
    leftRight: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px
) = margin(top, leftRight, bottom, leftRight)

fun margin(all: CSSLengthOrPercentageValue) = margin(all, all, all, all)
