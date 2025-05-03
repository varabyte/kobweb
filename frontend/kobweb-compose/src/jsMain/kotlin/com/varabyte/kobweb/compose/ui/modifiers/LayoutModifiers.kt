package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.aspectRatio(ratio: Number) = styleModifier {
    aspectRatio(AspectRatio.of(ratio))
}

fun Modifier.aspectRatio(width: Number, height: Number) = styleModifier {
    aspectRatio(AspectRatio.of(width, height))
}

fun Modifier.aspectRatio(ratio: AspectRatio): Modifier = styleModifier {
    aspectRatio(ratio)
}

fun Modifier.clear(clear: Clear) = styleModifier {
    clear(clear)
}

fun Modifier.lineHeight(value: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    lineHeight(value)
}

fun Modifier.lineHeight(value: Number): Modifier = styleModifier {
    lineHeight(value.toString())
}

fun Modifier.lineHeight(lineHeight: LineHeight): Modifier = styleModifier {
    lineHeight(lineHeight)
}

class MarginScope internal constructor(private val styleScope: StyleScope) {
    fun left(value: CSSLengthOrPercentageNumericValue) = styleScope.marginLeft(value)
    fun right(value: CSSLengthOrPercentageNumericValue) = styleScope.marginRight(value)
    fun top(value: CSSLengthOrPercentageNumericValue) = styleScope.marginTop(value)
    fun bottom(value: CSSLengthOrPercentageNumericValue) = styleScope.marginBottom(value)
}

fun MarginScope.topBottom(value: CSSLengthOrPercentageNumericValue) {
    top(value)
    bottom(value)
}

fun MarginScope.leftRight(value: CSSLengthOrPercentageNumericValue) {
    left(value)
    right(value)
}

fun Modifier.margin(all: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    margin(all)
}

fun Modifier.margin(scope: MarginScope.() -> Unit): Modifier = styleModifier {
    MarginScope(this).scope()
}

fun Modifier.margin(
    topBottom: CSSLengthOrPercentageNumericValue = 0.px,
    leftRight: CSSLengthOrPercentageNumericValue = 0.px
): Modifier = styleModifier {
    margin(topBottom, leftRight)
}

fun Modifier.margin(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    leftRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
): Modifier = styleModifier {
    margin(top, leftRight, bottom)
}

fun Modifier.margin(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    right: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
    left: CSSLengthOrPercentageNumericValue = 0.px
): Modifier = styleModifier {
    margin(top, right, bottom, left)
}

fun Modifier.marginInline(both: CSSLengthOrPercentageNumericValue) = styleModifier {
    marginInline(both)
}

fun Modifier.marginInline(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    marginInline(start, end)
}

class MarginInlineScope internal constructor(private val styleScope: StyleScope) {
    fun start(value: CSSLengthOrPercentageNumericValue) = styleScope.marginInlineStart(value)
    fun end(value: CSSLengthOrPercentageNumericValue) = styleScope.marginInlineEnd(value)
}

fun Modifier.marginInline(scope: MarginInlineScope.() -> Unit) = styleModifier {
    MarginInlineScope(this).scope()
}

fun Modifier.marginBlock(both: CSSLengthOrPercentageNumericValue) = styleModifier {
    marginBlock(both)
}

fun Modifier.marginBlock(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    marginBlock(start, end)
}

class MarginBlockScope internal constructor(private val styleScope: StyleScope) {
    fun start(value: CSSLengthOrPercentageNumericValue) = styleScope.marginBlockStart(value)
    fun end(value: CSSLengthOrPercentageNumericValue) = styleScope.marginBlockEnd(value)
}

fun Modifier.marginBlock(scope: MarginBlockScope.() -> Unit) = styleModifier {
    MarginBlockScope(this).scope()
}

class PaddingScope internal constructor(private val styleScope: StyleScope) {
    fun left(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingLeft(value)
    fun right(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingRight(value)
    fun top(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingTop(value)
    fun bottom(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingBottom(value)
}

fun PaddingScope.topBottom(value: CSSLengthOrPercentageNumericValue) {
    top(value)
    bottom(value)
}

fun PaddingScope.leftRight(value: CSSLengthOrPercentageNumericValue) {
    left(value)
    right(value)
}

fun Modifier.padding(all: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
    padding(all)
}

fun Modifier.padding(scope: PaddingScope.() -> Unit) = styleModifier {
    PaddingScope(this).scope()
}

fun Modifier.padding(
    topBottom: CSSLengthOrPercentageNumericValue = 0.px,
    leftRight: CSSLengthOrPercentageNumericValue = 0.px
): Modifier = styleModifier {
    padding(topBottom, leftRight)
}

fun Modifier.padding(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    leftRight: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
): Modifier = styleModifier {
    padding(top, leftRight, bottom)
}

fun Modifier.padding(
    top: CSSLengthOrPercentageNumericValue = 0.px,
    right: CSSLengthOrPercentageNumericValue = 0.px,
    bottom: CSSLengthOrPercentageNumericValue = 0.px,
    left: CSSLengthOrPercentageNumericValue = 0.px
): Modifier = styleModifier {
    padding(top, right, bottom, left)
}

fun Modifier.paddingInline(both: CSSLengthOrPercentageNumericValue) = styleModifier {
    paddingInline(both)
}

fun Modifier.paddingInline(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    paddingInline(start, end)
}

class PaddingInlineScope internal constructor(private val styleScope: StyleScope) {
    fun start(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingInlineStart(value)
    fun end(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingInlineEnd(value)
}

fun Modifier.paddingInline(scope: PaddingInlineScope.() -> Unit) = styleModifier {
    PaddingInlineScope(this).scope()
}

fun Modifier.paddingBlock(both: CSSLengthOrPercentageNumericValue) = styleModifier {
    paddingBlock(both)
}

fun Modifier.paddingBlock(
    start: CSSLengthOrPercentageNumericValue = 0.px,
    end: CSSLengthOrPercentageNumericValue = 0.px,
) = styleModifier {
    paddingBlock(start, end)
}

class PaddingBlockScope internal constructor(private val styleScope: StyleScope) {
    fun start(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingBlockStart(value)
    fun end(value: CSSLengthOrPercentageNumericValue) = styleScope.paddingBlockEnd(value)
}

fun Modifier.paddingBlock(scope: PaddingBlockScope.() -> Unit) = styleModifier {
    PaddingBlockScope(this).scope()
}

fun Modifier.resize(resize: Resize) = styleModifier {
    resize(resize)
}

fun Modifier.verticalAlign(verticalAlign: VerticalAlign) = styleModifier {
    verticalAlign(verticalAlign)
}

fun Modifier.verticalAlign(value: CSSLengthOrPercentageNumericValue) = styleModifier {
    verticalAlign(value)
}

fun Modifier.zIndex(value: Number) = styleModifier {
    zIndex(value)
}
