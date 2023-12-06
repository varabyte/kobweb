package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
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

fun Modifier.lineHeight(value: CSSLengthOrPercentageValue): Modifier = styleModifier {
    lineHeight(value)
}

fun Modifier.lineHeight(value: Number): Modifier = styleModifier {
    lineHeight(value.toString())
}

fun Modifier.lineHeight(lineHeight: LineHeight): Modifier = styleModifier {
    lineHeight(lineHeight)
}

fun Modifier.fillMaxWidth(percent: CSSLengthOrPercentageValue = 100.percent) = styleModifier {
    width(percent)
}

fun Modifier.fillMaxHeight(percent: CSSPercentageValue = 100.percent) = styleModifier {
    height(percent)
}

fun Modifier.fillMaxSize(percent: CSSPercentageValue = 100.percent): Modifier = styleModifier {
    width(percent)
    height(percent)
}

fun Modifier.size(size: CSSLengthOrPercentageValue): Modifier = size(width = size, height = size)

fun Modifier.size(width: CSSLengthOrPercentageValue, height: CSSLengthOrPercentageValue): Modifier = styleModifier {
    width(width)
    height(height)
}

fun Modifier.minSize(size: CSSLengthOrPercentageValue): Modifier = minSize(width = size, height = size)

fun Modifier.minSize(width: CSSLengthOrPercentageValue, height: CSSLengthOrPercentageValue): Modifier = styleModifier {
    minWidth(width)
    minHeight(height)
}

fun Modifier.maxSize(size: CSSLengthOrPercentageValue): Modifier = maxSize(width = size, height = size)

fun Modifier.maxSize(width: CSSLengthOrPercentageValue, height: CSSLengthOrPercentageValue): Modifier = styleModifier {
    maxWidth(width)
    maxHeight(height)
}

fun Modifier.width(size: CSSLengthOrPercentageValue): Modifier = styleModifier {
    width(size)
}

fun Modifier.height(size: CSSLengthOrPercentageValue): Modifier = styleModifier {
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

fun Modifier.minWidth(size: CSSLengthOrPercentageValue): Modifier = styleModifier {
    minWidth(size)
}

fun Modifier.minWidth(minWidth: MinWidth): Modifier = styleModifier {
    minWidth(minWidth)
}

fun Modifier.maxWidth(size: CSSLengthOrPercentageValue): Modifier = styleModifier {
    maxWidth(size)
}

fun Modifier.maxWidth(maxWidth: MaxWidth): Modifier = styleModifier {
    maxWidth(maxWidth)
}

fun Modifier.minHeight(size: CSSLengthOrPercentageValue): Modifier = styleModifier {
    minHeight(size)
}

fun Modifier.minHeight(minHeight: MinHeight): Modifier = styleModifier {
    minHeight(minHeight)
}

fun Modifier.maxHeight(size: CSSLengthOrPercentageValue): Modifier = styleModifier {
    maxHeight(size)
}

fun Modifier.maxHeight(maxHeight: MaxHeight): Modifier = styleModifier {
    maxHeight(maxHeight)
}

class MarginScope internal constructor(private val styleScope: StyleScope) {
    fun left(value: CSSLengthOrPercentageValue) = styleScope.marginLeft(value)
    fun right(value: CSSLengthOrPercentageValue) = styleScope.marginRight(value)
    fun top(value: CSSLengthOrPercentageValue) = styleScope.marginTop(value)
    fun bottom(value: CSSLengthOrPercentageValue) = styleScope.marginBottom(value)
}

fun Modifier.margin(all: CSSLengthOrPercentageValue): Modifier = styleModifier {
    margin(all)
}

fun Modifier.margin(scope: MarginScope.() -> Unit): Modifier = styleModifier {
    MarginScope(this).scope()
}

fun Modifier.margin(
    topBottom: CSSLengthOrPercentageValue = 0.px,
    leftRight: CSSLengthOrPercentageValue = 0.px
): Modifier = styleModifier {
    margin(topBottom, leftRight)
}

fun Modifier.margin(
    top: CSSLengthOrPercentageValue = 0.px,
    leftRight: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px,
): Modifier = styleModifier {
    margin(top, leftRight, bottom)
}

fun Modifier.margin(
    top: CSSLengthOrPercentageValue = 0.px,
    right: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px,
    left: CSSLengthOrPercentageValue = 0.px
): Modifier = styleModifier {
    margin(top, right, bottom, left)
}

fun Modifier.marginInline(
    start: CSSLengthOrPercentageValue = 0.px,
    end: CSSLengthOrPercentageValue = 0.px,
) = styleModifier {
    marginInline(start, end)
}

fun Modifier.marginBlock(
    start: CSSLengthOrPercentageValue = 0.px,
    end: CSSLengthOrPercentageValue = 0.px,
) = styleModifier {
    marginBlock(start, end)
}

fun Modifier.resize(resize: Resize) = styleModifier {
    resize(resize)
}

class PaddingScope internal constructor(private val styleScope: StyleScope) {
    fun left(value: CSSLengthOrPercentageValue) = styleScope.paddingLeft(value)
    fun right(value: CSSLengthOrPercentageValue) = styleScope.paddingRight(value)
    fun top(value: CSSLengthOrPercentageValue) = styleScope.paddingTop(value)
    fun bottom(value: CSSLengthOrPercentageValue) = styleScope.paddingBottom(value)
}

fun Modifier.padding(all: CSSLengthOrPercentageValue): Modifier = styleModifier {
    padding(all)
}

fun Modifier.padding(scope: PaddingScope.() -> Unit) = styleModifier {
    PaddingScope(this).scope()
}

fun Modifier.padding(
    topBottom: CSSLengthOrPercentageValue = 0.px,
    leftRight: CSSLengthOrPercentageValue = 0.px
): Modifier = styleModifier {
    padding(topBottom, leftRight)
}

fun Modifier.padding(
    top: CSSLengthOrPercentageValue = 0.px,
    leftRight: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px,
): Modifier = styleModifier {
    padding(top, leftRight, bottom)
}

fun Modifier.padding(
    top: CSSLengthOrPercentageValue = 0.px,
    right: CSSLengthOrPercentageValue = 0.px,
    bottom: CSSLengthOrPercentageValue = 0.px,
    left: CSSLengthOrPercentageValue = 0.px
): Modifier = styleModifier {
    padding(top, right, bottom, left)
}

fun Modifier.paddingInline(
    start: CSSLengthOrPercentageValue = 0.px,
    end: CSSLengthOrPercentageValue = 0.px,
) = styleModifier {
    paddingInline(start, end)
}

class PaddingInlineScope internal constructor(private val styleScope: StyleScope) {
    fun start(value: CSSLengthOrPercentageValue) = styleScope.paddingInlineStart(value)
    fun end(value: CSSLengthOrPercentageValue) = styleScope.paddingInlineEnd(value)
}

fun Modifier.paddingInline(scope: PaddingInlineScope.() -> Unit) = styleModifier {
    PaddingInlineScope(this).scope()
}

// TODO(#168): Remove these before v1.0
@Deprecated("Use paddingInline { start(value) } instead.", ReplaceWith("paddingInline { start(value) }"))
fun Modifier.paddingInlineStart(value: CSSLengthOrPercentageValue) = paddingInline { start(value) }

@Deprecated("Use paddingInline { end(value) } instead.", ReplaceWith("paddingInline { end(value) }"))
fun Modifier.paddingInlineEnd(value: CSSLengthOrPercentageValue) = paddingInline { end(value) }

fun Modifier.paddingBlock(
    start: CSSLengthOrPercentageValue = 0.px,
    end: CSSLengthOrPercentageValue = 0.px,
) = styleModifier {
    paddingBlock(start, end)
}

class PaddingBlockScope internal constructor(private val styleScope: StyleScope) {
    fun start(value: CSSLengthOrPercentageValue) = styleScope.paddingBlockStart(value)
    fun end(value: CSSLengthOrPercentageValue) = styleScope.paddingBlockEnd(value)
}

fun Modifier.paddingBlock(scope: PaddingBlockScope.() -> Unit) = styleModifier {
    PaddingBlockScope(this).scope()
}

// TODO(#168): Remove these before v1.0
@Deprecated("Use paddingBlock { start(value) } instead.", ReplaceWith("paddingBlock { start(value) }"))
fun Modifier.paddingBlockStart(value: CSSLengthOrPercentageValue) = paddingBlock { start(value) }

@Deprecated("Use paddingBlock { end(value) } instead.", ReplaceWith("paddingBlock { end(value) }"))
fun Modifier.paddingBlockEnd(value: CSSLengthOrPercentageValue) = paddingBlock { end(value) }

fun Modifier.overflow(overflow: Overflow) = styleModifier {
    overflow(overflow)
}

fun Modifier.overflow(overflowX: Overflow, overflowY: Overflow) = styleModifier {
    overflow(overflowX, overflowY)
}

class OverflowScope internal constructor(private val styleScope: StyleScope) {
    fun x(overflowX: Overflow) = styleScope.overflowX(overflowX)
    fun y(overflowY: Overflow) = styleScope.overflowY(overflowY)
}

fun Modifier.overflow(scope: OverflowScope.() -> Unit) = styleModifier {
    OverflowScope(this).scope()
}

// TODO(#168): Remove these before v1.0
@Deprecated("Use overflow { x(overflowX) } instead.", ReplaceWith("overflow { x(overflowX) }"))
fun Modifier.overflowX(overflowX: Overflow) = overflow { x(overflowX) }

@Deprecated("Use overflow { y(overflowY) } instead.", ReplaceWith("overflow { y(overflowY) }"))
fun Modifier.overflowY(overflowY: Overflow) = overflow { y(overflowY) }

fun Modifier.overflowWrap(overflowWrap: OverflowWrap) = styleModifier {
    overflowWrap(overflowWrap)
}

fun Modifier.verticalAlign(verticalAlign: VerticalAlign) = styleModifier {
    verticalAlign(verticalAlign)
}

fun Modifier.verticalAlign(value: CSSLengthOrPercentageValue) = styleModifier {
    verticalAlign(value)
}

fun Modifier.zIndex(value: Number) = styleModifier {
    zIndex(value)
}
