package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.px

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
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
) = styleModifier {
    scrollPadding(top, right, bottom, left)
}

fun Modifier.scrollPadding(value: CSSNumeric) = styleModifier {
    scrollPadding(value)
}

fun Modifier.scrollPaddingInline(start: CSSNumeric = 0.px, end: CSSNumeric = 0.px) = styleModifier {
    scrollPaddingInline(start, end)
}

fun Modifier.scrollPaddingBlock(start: CSSNumeric = 0.px, end: CSSNumeric = 0.px) = styleModifier {
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
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
) = styleModifier {
    scrollMargin(top, right, bottom, left)
}

fun Modifier.scrollMargin(value: CSSNumeric) = styleModifier {
    scrollMargin(value)
}

fun Modifier.scrollMarginInline(start: CSSNumeric = 0.px, end: CSSNumeric = 0.px) = styleModifier {
    scrollMarginInline(start, end)
}

fun Modifier.scrollMarginBlock(start: CSSNumeric = 0.px, end: CSSNumeric = 0.px) = styleModifier {
    scrollMarginBlock(start, end)
}
