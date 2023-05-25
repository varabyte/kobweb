package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.px

class CSSMargin internal constructor(val top: CSSNumeric = 0.px, val right: CSSNumeric = 0.px, val bottom: CSSNumeric = 0.px, val left: CSSNumeric = 0.px) {
    override fun toString(): String {
        return "$top $right $bottom $left"
    }
}

fun margin(top: CSSNumeric = 0.px, right: CSSNumeric = 0.px, bottom: CSSNumeric = 0.px, left: CSSNumeric = 0.px) =
    CSSMargin(top, right, bottom, left)
fun margin(topBottom: CSSNumeric = 0.px, leftRight: CSSNumeric = 0.px) = margin(topBottom, leftRight, topBottom, leftRight)
fun margin(top: CSSNumeric = 0.px, leftRight: CSSNumeric = 0.px, bottom: CSSNumeric = 0.px) = margin(top, leftRight, bottom, leftRight)
fun margin(all: CSSNumeric) = margin(all, all, all, all)
