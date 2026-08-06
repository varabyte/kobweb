package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.contain(contain: Contain) = styleModifier {
    contain(contain)
}

fun Modifier.contain(vararg values: Contain.Listable) = styleModifier {
    contain(Contain.list(*values))
}

fun Modifier.contain(values: List<Contain.Listable>) = styleModifier {
    contain(Contain.list(*values.toTypedArray()))
}

fun Modifier.containIntrinsicBlockSize(containIntrinsicBlockSize: ContainIntrinsicBlockSize) = styleModifier {
    containIntrinsicBlockSize(containIntrinsicBlockSize)
}

fun Modifier.containIntrinsicBlockSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(length))
}

fun Modifier.containIntrinsicBlockSize(length: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(length))
}

fun Modifier.containIntrinsicInlineSize(containIntrinsicInlineSize: ContainIntrinsicInlineSize) = styleModifier {
    containIntrinsicInlineSize(containIntrinsicInlineSize)
}

fun Modifier.containIntrinsicInlineSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(length))
}

fun Modifier.containIntrinsicInlineSize(length: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(length))
}

fun Modifier.containIntrinsicWidth(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicWidth(ContainIntrinsicWidth.of(length))
}

fun Modifier.containIntrinsicWidth(length: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicWidth(ContainIntrinsicWidth.of(length))
}

fun Modifier.containIntrinsicWidth(width: ContainIntrinsicWidth) = styleModifier {
    containIntrinsicWidth(width)
}

fun Modifier.containIntrinsicHeight(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicHeight(ContainIntrinsicHeight.of(length))
}

fun Modifier.containIntrinsicHeight(length: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicHeight(ContainIntrinsicHeight.of(length))
}

fun Modifier.containIntrinsicHeight(height: ContainIntrinsicHeight) = styleModifier {
    containIntrinsicHeight(height)
}

fun Modifier.containIntrinsicSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(length))
}

fun Modifier.containIntrinsicSize(length: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(length))
}

fun Modifier.containIntrinsicSize(width: CSSLengthNumericValue, height: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(width, height))
}

fun Modifier.containIntrinsicSize(width: CSSAutoLengthNumericValue, height: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(width, height))
}

fun Modifier.containIntrinsicSize(width: CSSLengthNumericValue, height: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(width, height))
}

fun Modifier.containIntrinsicSize(width: CSSAutoLengthNumericValue, height: CSSAutoLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(width, height))
}

fun Modifier.containIntrinsicSize(size: ContainIntrinsicSize) = styleModifier {
    containIntrinsicSize(size)
}