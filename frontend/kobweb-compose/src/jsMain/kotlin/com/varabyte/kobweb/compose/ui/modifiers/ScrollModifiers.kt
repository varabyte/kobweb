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

fun Modifier.overscrollBehaviorBlock(x: OverscrollBehavior.SingleValue) = styleModifier {
    overscrollBehaviorBlock(x)
}

fun Modifier.overscrollBehaviorInline(y: OverscrollBehavior.SingleValue) = styleModifier {
    overscrollBehaviorInline(y)
}

class OverscrollBehaviorScope internal constructor(private val styleScope: StyleScope) {
    fun x(overscrollBehavior: OverscrollBehavior.SingleValue) = styleScope.overscrollBehaviorX(overscrollBehavior)
    fun y(overscrollBehavior: OverscrollBehavior.SingleValue) = styleScope.overscrollBehaviorY(overscrollBehavior)
}

fun Modifier.overscrollBehavior(scope: OverscrollBehaviorScope.() -> Unit) = styleModifier {
    OverscrollBehaviorScope(this).scope()
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
