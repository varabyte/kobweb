package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.display(value: DisplayStyle) = styleModifier {
    display(value)
}

fun Modifier.lineHeight(value: CSSNumeric): Modifier = styleModifier {
    lineHeight(value)
}

fun Modifier.lineHeight(value: Number): Modifier = styleModifier {
    lineHeight(value.toString())
}

fun Modifier.fillMaxWidth(percent: CSSPercentageValue = 100.percent) = styleModifier {
    width(percent)
}

fun Modifier.fillMaxHeight(percent: CSSPercentageValue = 100.percent) = styleModifier {
    height(percent)
}

fun Modifier.fillMaxSize(percent: CSSPercentageValue = 100.percent): Modifier = styleModifier {
    width(percent)
    height(percent)
}

fun Modifier.size(size: CSSNumeric): Modifier = styleModifier {
    width(size)
    height(size)
}

fun Modifier.width(size: CSSNumeric, type: WebModifierType = WebModifierType.STYLE): Modifier = when (type) {
    WebModifierType.ATTRS -> attrsModifier { width(size) }
    WebModifierType.STYLE -> styleModifier { width(size) }
}

fun Modifier.height(size: CSSNumeric, type: WebModifierType = WebModifierType.STYLE): Modifier = when (type) {
    WebModifierType.ATTRS -> attrsModifier { height(size) }
    WebModifierType.STYLE -> styleModifier { height(size) }
}

fun Modifier.minWidth(size: CSSNumeric): Modifier = styleModifier {
    minWidth(size)
}

fun Modifier.maxWidth(size: CSSNumeric): Modifier = styleModifier {
    maxWidth(size)
}

fun Modifier.minHeight(size: CSSNumeric): Modifier = styleModifier {
    minHeight(size)
}

fun Modifier.maxHeight(size: CSSNumeric): Modifier = styleModifier {
    maxHeight(size)
}

fun Modifier.margin(all: CSSNumeric): Modifier = styleModifier {
    margin(all)
}

fun Modifier.margin(topBottom: CSSNumeric = 0.px, leftRight: CSSNumeric = 0.px): Modifier = styleModifier {
    margin(topBottom, leftRight)
}

fun Modifier.margin(
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
): Modifier = styleModifier {
    margin(top, right, bottom, left)
}

fun Modifier.marginInline(
    start: CSSNumeric = 0.px,
    end: CSSNumeric = 0.px,
) = styleModifier {
    marginInline(start, end)
}

fun Modifier.marginBlock(
    start: CSSNumeric = 0.px,
    end: CSSNumeric = 0.px,
) = styleModifier {
    marginBlock(start, end)
}

fun Modifier.padding(all: CSSNumeric): Modifier = styleModifier {
    padding(all)
}

fun Modifier.padding(topBottom: CSSNumeric = 0.px, leftRight: CSSNumeric = 0.px): Modifier = styleModifier {
    padding(topBottom, leftRight)
}

fun Modifier.padding(
    top: CSSNumeric = 0.px,
    right: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
    left: CSSNumeric = 0.px
): Modifier = styleModifier {
    padding(top, right, bottom, left)
}

fun Modifier.paddingInline(
    start: CSSNumeric = 0.px,
    end: CSSNumeric = 0.px,
) = styleModifier {
    paddingInline(start, end)
}

fun Modifier.paddingBlock(
    start: CSSNumeric = 0.px,
    end: CSSNumeric = 0.px,
) = styleModifier {
    paddingBlock(start, end)
}


fun Modifier.overflow(vararg overflows: Overflow) = styleModifier {
    overflow(*overflows)
}

fun Modifier.overflowX(overflowX: Overflow) = styleModifier {
    overflowX(overflowX)
}

fun Modifier.overflowY(overflowY: Overflow) = styleModifier {
    overflowY(overflowY)
}

fun Modifier.overflowWrap(overflowWrap: OverflowWrap) = styleModifier {
    overflowWrap(overflowWrap)
}

fun Modifier.verticalAlign(verticalAlign: VerticalAlign) = styleModifier {
    verticalAlign(verticalAlign)
}

fun Modifier.verticalAlign(value: CSSNumeric) = styleModifier {
    verticalAlign(value)
}

fun Modifier.zIndex(value: Number) = styleModifier {
    property("z-index", value)
}