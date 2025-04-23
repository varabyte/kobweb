package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

/**
 * Contain Modifiers
 */
fun Modifier.contain(contain: Contain) = styleModifier {
    contain(contain)
}

fun Modifier.contain(firstValue: String, secondValue: String) = styleModifier {
    contain(Contain.of(firstValue, secondValue))
}

fun Modifier.contain(firstValue: String, secondValue: String, thirdValue: String) = styleModifier {
    contain(Contain.of(firstValue, secondValue, thirdValue))
}

/**
 * Contain Intrinsic Block Size Modifiers
 */
fun Modifier.containIntrinsicBlockSize(containIntrinsicBlockSize: ContainIntrinsicBlockSize) = styleModifier {
    containIntrinsicBlockSize(containIntrinsicBlockSize)
}

fun Modifier.containIntrinsicBlockSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(length))
}

fun Modifier.containIntrinsicBlockAutoSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(length, true))
}

/**
 * Contain Intrinsic Height Modifiers
 */
fun Modifier.containIntrinsicHeight(containIntrinsicHeight: ContainIntrinsicHeight) = styleModifier {
    containIntrinsicHeight(containIntrinsicHeight)
}

fun Modifier.containIntrinsicHeight(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicHeight(ContainIntrinsicHeight.of(length))
}

fun Modifier.containIntrinsicAutoHeight(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicHeight(ContainIntrinsicHeight.of(length, true))
}

/**
 * Contain Intrinsic Inline Size Modifiers
 */
fun Modifier.containIntrinsicInlineSize(containIntrinsicInlineSize: ContainIntrinsicInlineSize) = styleModifier {
    containIntrinsicInlineSize(containIntrinsicInlineSize)
}

fun Modifier.containIntrinsicInlineSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(length))
}

fun Modifier.containIntrinsicInlineAutoSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(length, true))
}

/**
 * Contain Intrinsic Size Modifiers
 */
fun Modifier.containIntrinsicSize(containIntrinsicSize: ContainIntrinsicSize) = styleModifier {
    containIntrinsicSize(containIntrinsicSize)
}

fun Modifier.containIntrinsicSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(length))
}

fun Modifier.containIntrinsicAutoSize(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(length, true))
}

fun Modifier.containIntrinsicSize(width: CSSLengthNumericValue, height: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(width, height))
}

fun Modifier.containIntrinsicAutoSize(width: CSSLengthNumericValue, height: CSSLengthNumericValue) = styleModifier {
    containIntrinsicSize(ContainIntrinsicSize.of(width, height, true))
}

/**
 * Contain Intrinsic Width Modifiers
 */
fun Modifier.containIntrinsicWidth(containIntrinsicWidth: ContainIntrinsicWidth) = styleModifier {
    containIntrinsicWidth(containIntrinsicWidth)
}

fun Modifier.containIntrinsicWidth(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicWidth(ContainIntrinsicWidth.of(length))
}

fun Modifier.containIntrinsicAutoWidth(length: CSSLengthNumericValue) = styleModifier {
    containIntrinsicWidth(ContainIntrinsicWidth.of(length, true))
}
