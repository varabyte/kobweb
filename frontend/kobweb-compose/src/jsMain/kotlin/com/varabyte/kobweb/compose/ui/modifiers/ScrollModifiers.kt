package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.overscrollBehavior(overscrollBehavior: OverscrollBehavior) = styleModifier {
    overscrollBehavior(overscrollBehavior)
}

fun Modifier.overscrollBehavior(x: OverscrollBehavior.RepeatableValue, y: OverscrollBehavior.RepeatableValue) =
    styleModifier {
        overscrollBehavior(OverscrollBehavior.of(x, y))
    }

class OverscrollBehaviorScope internal constructor(private val styleScope: StyleScope) {
    fun x(overscrollBehavior: OverscrollBehavior.SingleValue) = styleScope.property("overscroll-behavior-x", overscrollBehavior)
    fun y(overscrollBehavior: OverscrollBehavior.SingleValue) = styleScope.property("overscroll-behavior-y", overscrollBehavior)
}

fun Modifier.overscrollBehavior(scope: OverscrollBehaviorScope.() -> Unit) = styleModifier {
    OverscrollBehaviorScope(this).scope()
}

fun Modifier.overscrollBehaviorBlock(overscrollBehaviorBlock: OverscrollBehaviorBlock) = styleModifier {
    overscrollBehaviorBlock(overscrollBehaviorBlock)
}

fun Modifier.overscrollBehaviorInline(overscrollBehaviorInline: OverscrollBehaviorInline) = styleModifier {
    overscrollBehaviorInline(overscrollBehaviorInline)
}

fun Modifier.scrollBehavior(scrollBehavior: ScrollBehavior) = styleModifier {
    scrollBehavior(scrollBehavior)
}

fun Modifier.scrollSnapType(scrollSnapType: ScrollSnapType) = styleModifier {
    scrollSnapType(scrollSnapType)
}

fun Modifier.scrollSnapType(axis: ScrollSnapType.Axis, strictness: ScrollSnapType.Strictness) = styleModifier {
    scrollSnapType(ScrollSnapType.of(axis, strictness))
}

fun Modifier.scrollPadding(all: CSSLengthOrPercentageNumericValue) = styleModifier {
    scrollPadding(all)
}

fun Modifier.scrollPadding(
    topBottom: CSSLengthOrPercentageNumericValue = autoLength,
    leftRight: CSSLengthOrPercentageNumericValue = autoLength,
) = styleModifier {
    scrollPadding(topBottom, leftRight)
}

fun Modifier.scrollPadding(
    top: CSSLengthOrPercentageNumericValue = autoLength,
    leftRight: CSSLengthOrPercentageNumericValue = autoLength,
    bottom: CSSLengthOrPercentageNumericValue = autoLength,
) = styleModifier {
    scrollPadding(top, leftRight, bottom)
}

fun Modifier.scrollPadding(
    top: CSSLengthOrPercentageNumericValue = autoLength,
    right: CSSLengthOrPercentageNumericValue = autoLength,
    bottom: CSSLengthOrPercentageNumericValue = autoLength,
    left: CSSLengthOrPercentageNumericValue = autoLength,
) = styleModifier {
    scrollPadding(top, right, bottom, left)
}

fun Modifier.scrollPaddingInline(both: CSSLengthOrPercentageNumericValue) = styleModifier {
    scrollPaddingInline(both)
}

fun Modifier.scrollPaddingInline(
    start: CSSLengthOrPercentageNumericValue = autoLength,
    end: CSSLengthOrPercentageNumericValue = autoLength,
) = styleModifier {
    scrollPaddingInline(start, end)
}

fun Modifier.scrollPaddingBlock(both: CSSLengthOrPercentageNumericValue) = styleModifier {
    scrollPaddingBlock(both)
}

fun Modifier.scrollPaddingBlock(
    start: CSSLengthOrPercentageNumericValue = autoLength,
    end: CSSLengthOrPercentageNumericValue = autoLength,
) = styleModifier {
    scrollPaddingBlock(start, end)
}

fun Modifier.scrollSnapAlign(scrollSnapAlign: ScrollSnapAlign) = styleModifier {
    scrollSnapAlign(scrollSnapAlign)
}

fun Modifier.scrollSnapAlign(blockAxis: ScrollSnapAlign.Alignment, inlineAxis: ScrollSnapAlign.Alignment) =
    styleModifier {
        scrollSnapAlign(ScrollSnapAlign.of(blockAxis, inlineAxis))
    }

fun Modifier.scrollSnapStop(scrollSnapStop: ScrollSnapStop) = styleModifier {
    scrollSnapStop(scrollSnapStop)
}

fun Modifier.scrollMargin(all: CSSLengthNumericValue) = styleModifier {
    scrollMargin(all)
}

fun Modifier.scrollMargin(
    topBottom: CSSLengthNumericValue = 0.px,
    leftRight: CSSLengthNumericValue = 0.px,
) = styleModifier {
    scrollMargin(topBottom, leftRight)
}

fun Modifier.scrollMargin(
    top: CSSLengthNumericValue = 0.px,
    leftRight: CSSLengthNumericValue = 0.px,
    bottom: CSSLengthNumericValue = 0.px,
) = styleModifier {
    scrollMargin(top, leftRight, bottom)
}

fun Modifier.scrollMargin(
    top: CSSLengthNumericValue = 0.px,
    right: CSSLengthNumericValue = 0.px,
    bottom: CSSLengthNumericValue = 0.px,
    left: CSSLengthNumericValue = 0.px,
) = styleModifier {
    scrollMargin(top, right, bottom, left)
}

fun Modifier.scrollMarginInline(both: CSSLengthNumericValue) = styleModifier {
    scrollMarginInline(both)
}

fun Modifier.scrollMarginInline(start: CSSLengthNumericValue = 0.px, end: CSSLengthNumericValue = 0.px) =
    styleModifier {
        scrollMarginInline(start, end)
    }

fun Modifier.scrollMarginBlock(both: CSSLengthNumericValue) = styleModifier {
    scrollMarginBlock(both)
}

fun Modifier.scrollMarginBlock(start: CSSLengthNumericValue = 0.px, end: CSSLengthNumericValue = 0.px) = styleModifier {
    scrollMarginBlock(start, end)
}

fun Modifier.scrollbarWidth(scrollbarWidth: ScrollbarWidth) = styleModifier {
    scrollbarWidth(scrollbarWidth)
}