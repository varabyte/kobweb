package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.StyleBuilder

// region padding-inline See: https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline

fun StyleBuilder.paddingInline(vararg value: CSSNumeric) {
    property("padding-inline", value.joinToString(" "))
}

fun StyleBuilder.paddingInlineStart(value: CSSNumeric) {
    property("padding-inline-start", value)
}

fun StyleBuilder.paddingInlineEnd(value: CSSNumeric) {
    property("padding-inline-end", value)
}

// endregion

// region padding-block See: https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block

fun StyleBuilder.paddingBlock(vararg value: CSSNumeric) {
    property("padding-block", value.joinToString(" "))
}

fun StyleBuilder.paddingBlockStart(value: CSSNumeric) {
    property("padding-block-start", value)
}

fun StyleBuilder.paddingBlockEnd(value: CSSNumeric) {
    property("padding-block-end", value)
}

// endregion

// region margin-inline See https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline

fun StyleBuilder.marginInline(vararg value: CSSNumeric) {
    property("margin-inline", value.joinToString(" "))
}

fun StyleBuilder.marginInlineStart(value: CSSNumeric) {
    property("margin-inline-start", value)
}

fun StyleBuilder.marginInlineEnd(value: CSSNumeric) {
    property("margin-inline-end", value)
}

// endregion

// region margin-block See: https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block

fun StyleBuilder.marginBlock(vararg value: CSSNumeric) {
    property("margin-block", value.joinToString(" "))
}

fun StyleBuilder.marginBlockStart(value: CSSNumeric) {
    property("margin-block-start", value)
}

fun StyleBuilder.marginBlockEnd(value: CSSNumeric) {
    property("margin-block-end", value)
}

// endregion
