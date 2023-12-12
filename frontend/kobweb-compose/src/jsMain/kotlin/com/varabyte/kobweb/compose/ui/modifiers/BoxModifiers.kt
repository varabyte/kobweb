package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.boxDecorationBreak(boxDecorationBreak: BoxDecorationBreak) = styleModifier {
    boxDecorationBreak(boxDecorationBreak)
}

fun Modifier.boxShadow(
    offsetX: CSSLengthNumericValue = 0.px,
    offsetY: CSSLengthNumericValue = 0.px,
    blurRadius: CSSLengthNumericValue? = null,
    spreadRadius: CSSLengthNumericValue? = null,
    color: CSSColorValue? = null,
    inset: Boolean = false,
) = styleModifier {
    this.boxShadow(offsetX, offsetY, blurRadius, spreadRadius, color, inset)
}

fun Modifier.boxSizing(boxSizing: BoxSizing) = styleModifier {
    boxSizing(boxSizing)
}
