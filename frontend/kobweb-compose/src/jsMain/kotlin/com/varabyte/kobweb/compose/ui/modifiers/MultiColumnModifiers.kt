package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.multiColumn(multiColumn: MultiColumn) = styleModifier {
    multiColumn(multiColumn)
}

fun Modifier.multiColumn(color: CSSColorValue) = styleModifier {
    multiColumn(color)
}

fun Modifier.multiColumn(style: LineStyle) = styleModifier {
    multiColumn(style)
}

fun Modifier.multiColumn(width: CSSLengthNumericValue) = styleModifier {
    multiColumn(width)
}

fun Modifier.multiColumn(
    width: CSSLengthNumericValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) = styleModifier {
    multiColumn(
        width, style, color
    )
}