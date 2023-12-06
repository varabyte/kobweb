package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.scrollBehavior(scrollBehavior: ScrollBehavior) = styleModifier {
    scrollBehavior(scrollBehavior)
}

fun Modifier.scrollSnapType(scrollSnapType: ScrollSnapType) = styleModifier {
    scrollSnapType(scrollSnapType)
}

fun Modifier.scrollSnapType(axis: ScrollSnapAxis, mode: ScrollSnapMode? = null) = styleModifier {
    scrollSnapType(axis, mode)
}

fun Modifier.scrollPadding(
    top: CSSLengthOrPercentageValue = 0.px,
    right: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px,
    left: CSSLengthOrPercentageValue = 0.px
) = styleModifier {
    scrollPadding(top, right, bottom, left)
}

fun Modifier.scrollPadding(value: CSSLengthOrPercentageValue) = styleModifier {
    scrollPadding(value)
}

fun Modifier.scrollPaddingInline(start: CSSLengthOrPercentageValue = 0.px, end: CSSLengthOrPercentageValue = 0.px) =
    styleModifier {
    scrollPaddingInline(start, end)
}

fun Modifier.scrollPaddingBlock(start: CSSLengthOrPercentageValue = 0.px, end: CSSLengthOrPercentageValue = 0.px) =
    styleModifier {
    scrollPaddingBlock(start, end)
}

fun Modifier.scrollSnapAlign(align: ScrollSnapAlign) = styleModifier {
    scrollSnapAlign(align)
}

fun Modifier.scrollSnapAlign(blockAxis: ScrollSnapAlign, inlineAxis: ScrollSnapAlign? = null) = styleModifier {
    scrollSnapAlign(blockAxis, inlineAxis)
}

fun Modifier.scrollSnapStop(scrollSnapStop: ScrollSnapStop) = styleModifier {
    scrollSnapStop(scrollSnapStop)
}

fun Modifier.scrollMargin(
    top: CSSLengthValue = 0.px,
    right: CSSLengthValue = 0.px,
    bottom: CSSLengthValue = 0.px,
    left: CSSLengthValue = 0.px
) = styleModifier {
    scrollMargin(top, right, bottom, left)
}

fun Modifier.scrollMargin(value: CSSLengthValue) = styleModifier {
    scrollMargin(value)
}

fun Modifier.scrollMarginInline(start: CSSLengthValue = 0.px, end: CSSLengthValue = 0.px) = styleModifier {
    scrollMarginInline(start, end)
}

fun Modifier.scrollMarginBlock(start: CSSLengthValue = 0.px, end: CSSLengthValue = 0.px) = styleModifier {
    scrollMarginBlock(start, end)
}
