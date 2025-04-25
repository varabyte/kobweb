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

fun Modifier.contain(vararg value: Contain.SingleValue) = styleModifier {
    contain(*value)
}

/**
 * Contain Intrinsic Block Size Modifiers
 */
fun Modifier.containIntrinsicBlockSize(containIntrinsicBlockSize: Contain.SingleValue) = styleModifier {
    containIntrinsicBlockSize(containIntrinsicBlockSize)
}

/**
 * Contain Intrinsic Height Modifiers
 */
fun Modifier.containIntrinsicHeight(containIntrinsicHeight: Contain.SingleValue) = styleModifier {
    containIntrinsicHeight(containIntrinsicHeight)
}

/**
 * Contain Intrinsic Inline Size Modifiers
 */
fun Modifier.containIntrinsicInlineSize(containIntrinsicInlineSize: Contain.SingleValue) = styleModifier {
    containIntrinsicInlineSize(containIntrinsicInlineSize)
}

/**
 * Contain Intrinsic Size Modifiers
 */
fun Modifier.containIntrinsicSize(containIntrinsicSize: Contain.SingleValue) = styleModifier {
    containIntrinsicSize(containIntrinsicSize)
}

/**
 * Contain Intrinsic Width Modifiers
 */
fun Modifier.containIntrinsicWidth(containIntrinsicWidth: Contain.SingleValue) = styleModifier {
    containIntrinsicWidth(containIntrinsicWidth)
}
