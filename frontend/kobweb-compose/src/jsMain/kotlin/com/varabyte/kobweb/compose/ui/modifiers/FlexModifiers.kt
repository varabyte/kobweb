package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.flexBasis(flexBasis: FlexBasis): Modifier = styleModifier {
    flexBasis(flexBasis)
}

fun Modifier.flexBasis(value: CSSLengthOrPercentageValue): Modifier = styleModifier {
    flexBasis(value)
}

fun Modifier.flexDirection(flexDirection: FlexDirection): Modifier = styleModifier {
    flexDirection(flexDirection)
}

fun Modifier.flexWrap(flexWrap: FlexWrap): Modifier = styleModifier {
    flexWrap(flexWrap)
}

fun Modifier.flexFlow(flexDirection: FlexDirection, flexWrap: FlexWrap): Modifier = styleModifier {
    flexFlow(flexDirection, flexWrap)
}

fun Modifier.order(value: Int): Modifier = styleModifier {
    order(value)
}

fun Modifier.flexGrow(value: Number): Modifier = styleModifier {
    flexGrow(value)
}

fun Modifier.flexShrink(value: Number): Modifier = styleModifier {
    flexShrink(value)
}

fun Modifier.flex(value: String): Modifier = styleModifier {
    flex(value)
}

fun Modifier.flex(value: Int): Modifier = styleModifier {
    flex(value)
}

fun Modifier.flex(value: CSSLengthOrPercentageValue): Modifier = styleModifier {
    flex(value)
}

fun Modifier.flex(flexGrow: Int, flexBasis: CSSLengthOrPercentageValue): Modifier = styleModifier {
    flex(flexGrow, flexBasis)
}

fun Modifier.flex(flexGrow: Int, flexShrink: Int): Modifier = styleModifier {
    flex(flexGrow, flexShrink)
}

fun Modifier.flex(flexGrow: Int, flexShrink: Int, flexBasis: CSSLengthOrPercentageValue): Modifier = styleModifier {
    flex(flexGrow, flexShrink, flexBasis)
}

fun Modifier.rowGap(value: CSSLengthOrPercentageValue) = styleModifier {
    rowGap(value)
}

fun Modifier.columnGap(value: CSSLengthOrPercentageValue) = styleModifier {
    columnGap(value)
}

fun Modifier.gap(value: CSSLengthOrPercentageValue) = styleModifier {
    gap(value)
}

fun Modifier.gap(rowGap: CSSLengthOrPercentageValue, columnGap: CSSLengthOrPercentageValue) = styleModifier {
    gap(rowGap, columnGap)
}
