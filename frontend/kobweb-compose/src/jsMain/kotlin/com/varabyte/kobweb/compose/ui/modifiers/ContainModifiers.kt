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

fun Modifier.contain(vararg values: Contain.SingleValue) = styleModifier {
    contain(*values)
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

fun Modifier.containIntrinsicBlockSize(length: CSSLengthNumericValue, auto: Boolean = false) = styleModifier {
    containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(length, auto))
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

fun Modifier.containIntrinsicInlineSize(length: CSSLengthNumericValue, auto: Boolean = false) = styleModifier {
    containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(length, auto))
}
