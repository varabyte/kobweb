package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region padding-inline See: https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline

fun StyleScope.paddingInline(vararg value: CSSNumeric) {
    property("padding-inline", value.joinToString(" "))
}

fun StyleScope.paddingInlineStart(value: CSSNumeric) {
    property("padding-inline-start", value)
}

fun StyleScope.paddingInlineEnd(value: CSSNumeric) {
    property("padding-inline-end", value)
}

// endregion

// region padding-block See: https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block

fun StyleScope.paddingBlock(vararg value: CSSNumeric) {
    property("padding-block", value.joinToString(" "))
}

fun StyleScope.paddingBlockStart(value: CSSNumeric) {
    property("padding-block-start", value)
}

fun StyleScope.paddingBlockEnd(value: CSSNumeric) {
    property("padding-block-end", value)
}

// endregion

// region margin-inline See https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline

fun StyleScope.marginInline(vararg value: CSSNumeric) {
    property("margin-inline", value.joinToString(" "))
}

fun StyleScope.marginInlineStart(value: CSSNumeric) {
    property("margin-inline-start", value)
}

fun StyleScope.marginInlineEnd(value: CSSNumeric) {
    property("margin-inline-end", value)
}

// endregion

// region margin-block See: https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block

fun StyleScope.marginBlock(vararg value: CSSNumeric) {
    property("margin-block", value.joinToString(" "))
}

fun StyleScope.marginBlockStart(value: CSSNumeric) {
    property("margin-block-start", value)
}

fun StyleScope.marginBlockEnd(value: CSSNumeric) {
    property("margin-block-end", value)
}

// endregion

// region vertical-align See: https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align

class VerticalAlign(val value: String) {
    companion object {
        // Keyword
        val Baseline get() = VerticalAlign("baseline")
        val Sub get() = VerticalAlign("sub")
        val Super get() = VerticalAlign("super")
        val TextTop get() = VerticalAlign("text-top")
        val TextBottom get() = VerticalAlign("text-bottom")
        val Middle get() = VerticalAlign("middle")
        val Top get() = VerticalAlign("top")
        val Bottom get() = VerticalAlign("bottom")

        // Global
        val Inherit get() = WritingMode("inherit")
        val Initial get() = WritingMode("initial")
        val Revert get() = WritingMode("revert")
        val Unset get() = WritingMode("unset")
    }
}

fun StyleScope.verticalAlign(verticalAlign: VerticalAlign) {
    property("vertical-align", verticalAlign.value)
}

fun StyleScope.verticalAlign(value: CSSNumeric) {
    property("vertical-align", value)
}

// endregion