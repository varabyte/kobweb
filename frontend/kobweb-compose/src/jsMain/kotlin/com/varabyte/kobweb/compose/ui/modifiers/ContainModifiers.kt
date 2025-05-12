package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.contain(contain: Contain) = styleModifier {
    contain(contain)
}

fun Modifier.contain(values: List<Contain.Listable>) = styleModifier {
    contain(Contain.list(values))
}

fun Modifier.containIntrinsicBlockSize(containIntrinsicBlockSize: ContainIntrinsicBlockSize) = styleModifier {
    containIntrinsicBlockSize(containIntrinsicBlockSize)
}

fun Modifier.containIntrinsicBlockSize(length: CSSLengthNumericValue, auto: Boolean = false) = styleModifier {
    containIntrinsicBlockSize(ContainIntrinsicBlockSize.of(length, auto))
}

fun Modifier.containIntrinsicInlineSize(containIntrinsicInlineSize: ContainIntrinsicInlineSize) = styleModifier {
    containIntrinsicInlineSize(containIntrinsicInlineSize)
}

fun Modifier.containIntrinsicInlineSize(length: CSSLengthNumericValue, auto: Boolean = false) = styleModifier {
    containIntrinsicInlineSize(ContainIntrinsicInlineSize.of(length, auto))
}
