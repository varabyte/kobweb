@file:Suppress("DeprecatedCallableAddReplaceWith")

package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

fun Modifier.aspectRatio(ratio: Number) = styleModifier {
    aspectRatio(ratio)
}

fun Modifier.aspectRatio(width: Number, height: Number) = styleModifier {
    aspectRatio(width, height)
}

fun Modifier.aspectRatio(ratio: AspectRatio): Modifier = styleModifier {
    aspectRatio(ratio)
}

fun Modifier.lineHeight(value: CSSNumeric): Modifier = styleModifier {
    lineHeight(value)
}

fun Modifier.lineHeight(value: Number): Modifier = styleModifier {
    lineHeight(value.toString())
}

fun Modifier.lineHeight(lineHeight: LineHeight): Modifier = styleModifier {
    lineHeight(lineHeight)
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

// TODO(#168): Remove before v1.0
@Deprecated("This method will be removed before v1.0. If targeting styles, just use `size` without the type argument. Otherwise, call `attrsModifier { width(...); height(...) }`.")
fun Modifier.size(size: CSSNumeric, type: WebModifierType): Modifier = when (type) {
    WebModifierType.ATTRS -> attrsModifier {
        width(size)
        height(size)
    }

    WebModifierType.STYLE -> styleModifier {
        width(size)
        height(size)
    }
}

fun Modifier.size(size: CSSNumeric): Modifier = styleModifier {
    width(size)
    height(size)
}

fun Modifier.minSize(size: CSSNumeric): Modifier = styleModifier {
    minWidth(size)
    minHeight(size)
}

fun Modifier.maxSize(size: CSSNumeric): Modifier = styleModifier {
    maxWidth(size)
    maxHeight(size)
}


// TODO(#168): Remove before v1.0
@Deprecated("This method will be removed before v1.0. If targeting styles, just use `width` without the type argument. Otherwise, call `attrsModifier { width(...) }`.")
fun Modifier.width(size: CSSNumeric, type: WebModifierType): Modifier = when (type) {
    WebModifierType.ATTRS -> attrsModifier { width(size) }
    WebModifierType.STYLE -> styleModifier { width(size) }
}

// TODO(#168): Remove before v1.0
@Deprecated("This method will be removed before v1.0. If targeting styles, just use `height` without the type argument. Otherwise, call `attrsModifier { height(...) }`.")
fun Modifier.height(size: CSSNumeric, type: WebModifierType): Modifier = when (type) {
    WebModifierType.ATTRS -> attrsModifier { height(size) }
    WebModifierType.STYLE -> styleModifier { height(size) }
}

fun Modifier.width(size: CSSNumeric): Modifier = styleModifier {
    width(size)
}

fun Modifier.height(size: CSSNumeric): Modifier = styleModifier {
    height(size)
}

fun Modifier.width(width: Width): Modifier = styleModifier {
    width(width)
}

fun Modifier.width(auto: CSSAutoKeyword): Modifier = styleModifier {
    width(auto)
}

fun Modifier.height(height: Height): Modifier = styleModifier {
    height(height)
}

fun Modifier.height(auto: CSSAutoKeyword): Modifier = styleModifier {
    height(auto)
}

fun Modifier.minWidth(size: CSSNumeric): Modifier = styleModifier {
    minWidth(size)
}

fun Modifier.minWidth(minWidth: MinWidth): Modifier = styleModifier {
    minWidth(minWidth)
}

fun Modifier.maxWidth(size: CSSNumeric): Modifier = styleModifier {
    maxWidth(size)
}

fun Modifier.maxWidth(maxWidth: MaxWidth): Modifier = styleModifier {
    maxWidth(maxWidth)
}

fun Modifier.minHeight(size: CSSNumeric): Modifier = styleModifier {
    minHeight(size)
}

fun Modifier.minHeight(minHeight: MinHeight): Modifier = styleModifier {
    minHeight(minHeight)
}

fun Modifier.maxHeight(size: CSSNumeric): Modifier = styleModifier {
    maxHeight(size)
}

fun Modifier.maxHeight(maxHeight: MaxHeight): Modifier = styleModifier {
    maxHeight(maxHeight)
}

fun Modifier.margin(all: CSSNumeric): Modifier = styleModifier {
    margin(all)
}

fun Modifier.margin(topBottom: CSSNumeric = 0.px, leftRight: CSSNumeric = 0.px): Modifier = styleModifier {
    margin(topBottom, leftRight)
}

fun Modifier.margin(
    top: CSSNumeric = 0.px,
    leftRight: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
): Modifier = styleModifier {
    margin(top, leftRight, bottom)
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

fun Modifier.resize(resize: Resize) = styleModifier {
    resize(resize)
}

fun Modifier.padding(all: CSSNumeric): Modifier = styleModifier {
    padding(all)
}

fun Modifier.padding(topBottom: CSSNumeric = 0.px, leftRight: CSSNumeric = 0.px): Modifier = styleModifier {
    padding(topBottom, leftRight)
}

fun Modifier.padding(
    top: CSSNumeric = 0.px,
    leftRight: CSSNumeric = 0.px,
    bottom: CSSNumeric = 0.px,
): Modifier = styleModifier {
    padding(top, leftRight, bottom)
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

fun Modifier.paddingInlineStart(value: CSSNumeric) = styleModifier {
    paddingInlineStart(value)
}

fun Modifier.paddingInlineEnd(value: CSSNumeric) = styleModifier {
    paddingInlineEnd(value)
}

fun Modifier.paddingBlock(
    start: CSSNumeric = 0.px,
    end: CSSNumeric = 0.px,
) = styleModifier {
    paddingBlock(start, end)
}

fun Modifier.paddingBlockStart(value: CSSNumeric) = styleModifier {
    paddingBlockStart(value)
}

fun Modifier.paddingBlockEnd(value: CSSNumeric) = styleModifier {
    paddingBlockEnd(value)
}

fun Modifier.overflow(overflow: Overflow) = styleModifier {
    overflow(overflow)
}

fun Modifier.overflow(overflowX: Overflow, overflowY: Overflow) = styleModifier {
    overflow(overflowX, overflowY)
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
    zIndex(value)
}
