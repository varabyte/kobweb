package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

fun Modifier.position(position: Position) = styleModifier {
    position(position)
}

fun Modifier.top(value: CSSLengthOrPercentageValue) = styleModifier {
    top(value)
}

fun Modifier.top(value: CSSAutoKeyword) = styleModifier {
    top(value)
}

fun Modifier.bottom(value: CSSLengthOrPercentageValue) = styleModifier {
    bottom(value)
}

fun Modifier.bottom(value: CSSAutoKeyword) = styleModifier {
    bottom(value)
}

fun Modifier.left(value: CSSLengthOrPercentageValue) = styleModifier {
    left(value)
}

fun Modifier.left(value: CSSAutoKeyword) = styleModifier {
    left(value)
}

fun Modifier.right(value: CSSLengthOrPercentageValue) = styleModifier {
    right(value)
}

fun Modifier.right(value: CSSAutoKeyword) = styleModifier {
    right(value)
}

fun Modifier.float(float: CSSFloat) = styleModifier {
    float(float)
}
