package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.backdropFilter(value: String) = styleModifier {
    property("backdrop-filter", value)
}

fun Modifier.boxShadow(value: String) = styleModifier {
    property("box-shadow", value)
}

fun Modifier.boxShadow(
    offsetX: CSSLengthValue = 0.px,
    offsetY: CSSLengthValue = 0.px,
    blurRadius: CSSLengthValue? = null,
    spreadRadius: CSSLengthValue? = null,
    color: Color? = null,
    inset: Boolean = false,
) = boxShadow(buildString {
    if (inset) {
        append("inset")
        append(' ')
    }
    append(offsetX)
    append(' ')
    append(offsetY)

    if (blurRadius != null) {
        append(' ')
        append(blurRadius)
    }

    if (spreadRadius != null) {
        append(' ')
        append(spreadRadius)
    }

    if (color != null) {
        append(' ')
        append(color)
    }
})
