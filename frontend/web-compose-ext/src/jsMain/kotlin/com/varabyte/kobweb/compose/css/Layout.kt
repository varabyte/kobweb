package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region width See: https://developer.mozilla.org/en-US/docs/Web/CSS/width

class Width private constructor(val value: String) {
    companion object {
        // Keyword
        val FitContent = Width("fit-content")
        val MaxContext = Width("max-content")
        val MinContent = Width("min-content")

        // Global
        val Inherit = Width("inherit")
        val Initial = Width("initial")
        val Revert = Width("revert")
        val RevertLayer = Width("revert-layer")
        val Unset = Width("unset")
    }
}
typealias MinWidth = Width

fun StyleScope.width(width: Width) {
    property("width", width.value)
}

fun StyleScope.minWidth(minWidth: MinWidth) {
    property("min-width", minWidth.value)
}

// endregion

// region height See: https://developer.mozilla.org/en-US/docs/Web/CSS/height

class Height private constructor(val value: String) {
    companion object {
        // Keyword
        val FitContent = Height("fit-content")
        val MaxContext = Height("max-content")
        val MinContent = Height("min-content")

        // Global
        val Inherit = Height("inherit")
        val Initial = Height("initial")
        val Revert = Height("revert")
        val RevertLayer = Height("revert-layer")
        val Unset = Height("unset")
    }
}
typealias MinHeight = Height

fun StyleScope.height(height: Height) {
    property("height", height.value)
}

fun StyleScope.minHeight(minHeight: MinHeight) {
    property("min-height", minHeight.value)
}

// endregion

// region max-width See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-width

class MaxWidth private constructor(val value: String) {
    companion object {
        // Keyword
        val FitContent = MaxWidth("fit-content")
        val MaxContext = MaxWidth("max-content")
        val MinContent = MaxWidth("min-content")
        val None = MaxWidth("none")

        // Global
        val Inherit = MaxWidth("inherit")
        val Initial = MaxWidth("initial")
        val Revert = MaxWidth("revert")
        val RevertLayer = MaxWidth("revert-layer")
        val Unset = MaxWidth("unset")
    }
}

fun StyleScope.maxWidth(maxWidth: MaxWidth) {
    property("max-width", maxWidth.value)
}

// endregion

// region height See: https://developer.mozilla.org/en-US/docs/Web/CSS/height

class MaxHeight private constructor(val value: String) {
    companion object {
        // Keyword
        val FitContent = MaxHeight("fit-content")
        val MaxContext = MaxHeight("max-content")
        val MinContent = MaxHeight("min-content")
        val None = MaxHeight("none")

        // Global
        val Inherit = MaxHeight("inherit")
        val Initial = MaxHeight("initial")
        val Revert = MaxHeight("revert")
        val RevertLayer = MaxHeight("revert-layer")
        val Unset = MaxHeight("unset")
    }
}

fun StyleScope.maxHeight(maxHeight: MaxHeight) {
    property("max-height", maxHeight.value)
}

// endregion

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

class VerticalAlign private constructor(val value: String) {
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
        val Inherit get() = VerticalAlign("inherit")
        val Initial get() = VerticalAlign("initial")
        val Revert get() = VerticalAlign("revert")
        val Unset get() = VerticalAlign("unset")
    }
}

fun StyleScope.verticalAlign(verticalAlign: VerticalAlign) {
    property("vertical-align", verticalAlign.value)
}

fun StyleScope.verticalAlign(value: CSSNumeric) {
    property("vertical-align", value)
}

// endregion
