package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

fun Modifier.position(position: Position) = styleModifier {
    position(position)
}

fun Modifier.top(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    top(value.unsafeCast<CSSLengthOrPercentageValue>())
}

fun Modifier.top(value: CSSAutoKeyword) = styleModifier {
    top(value)
}

fun Modifier.top(top: Top) = styleModifier {
    top(top)
}

fun Modifier.bottom(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    bottom(value.unsafeCast<CSSLengthOrPercentageValue>())
}

fun Modifier.bottom(value: CSSAutoKeyword) = styleModifier {
    bottom(value)
}

fun Modifier.bottom(bottom: Bottom) = styleModifier {
    bottom(bottom)
}

fun Modifier.left(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    left(value.unsafeCast<CSSLengthOrPercentageValue>())
}

fun Modifier.left(value: CSSAutoKeyword) = styleModifier {
    left(value)
}

fun Modifier.left(left: Left) = styleModifier {
    left(left)
}

fun Modifier.right(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    right(value.unsafeCast<CSSLengthOrPercentageValue>())
}

fun Modifier.right(value: CSSAutoKeyword) = styleModifier {
    right(value)
}

fun Modifier.right(right: Right) = styleModifier {
    right(right)
}

fun Modifier.float(float: CSSFloat) = styleModifier {
    float(float)
}

fun Modifier.float(float: Float) = styleModifier {
    float(float)
}
