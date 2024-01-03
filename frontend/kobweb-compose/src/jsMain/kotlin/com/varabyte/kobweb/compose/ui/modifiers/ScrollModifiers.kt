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

fun Modifier.scrollSnapType(axis: ScrollSnapType.Axis, strictness: ScrollSnapType.Strictness) = styleModifier {
    scrollSnapType(ScrollSnapType.of(axis, strictness))
}

@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
@Deprecated("`ScrollSnapAxis` and `ScrollSnapMode` are deprecated. Use `ScrollSnapType.Axis` and `ScrollSnapType.Strictness` instead.")
fun Modifier.scrollSnapType(axis: ScrollSnapAxis, mode: ScrollSnapMode? = null) = styleModifier {
    scrollSnapType(axis, mode)
}

fun Modifier.scrollPadding(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    right: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
    left: CSSLengthOrPercentageNumericValue = 0.px
) = styleModifier {
    scrollPadding(top, right, bottom, left)
}

fun Modifier.scrollPadding(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    scrollPadding(value)
}

fun Modifier.scrollPaddingInline(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px
) = styleModifier {
    scrollPaddingInline(start, end)
}

fun Modifier.scrollPaddingBlock(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px
) = styleModifier {
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
    top: CSSLengthNumericValue = 0.px,
    right: CSSLengthNumericValue = 0.px,
    bottom: CSSLengthNumericValue = 0.px,
    left: CSSLengthNumericValue = 0.px
) = styleModifier {
    scrollMargin(top, right, bottom, left)
}

fun Modifier.scrollMargin(value: CSSLengthNumericValue) = styleModifier {
    scrollMargin(value)
}

fun Modifier.scrollMarginInline(start: CSSLengthNumericValue = 0.px, end: CSSLengthNumericValue = 0.px) =
    styleModifier {
        scrollMarginInline(start, end)
    }

fun Modifier.scrollMarginBlock(start: CSSLengthNumericValue = 0.px, end: CSSLengthNumericValue = 0.px) = styleModifier {
    scrollMarginBlock(start, end)
}
