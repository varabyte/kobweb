package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// region width See: https://developer.mozilla.org/en-US/docs/Web/CSS/width

sealed class Width private constructor(val value: String) {
    // Keyword
    class FitContent(value: CSSLengthOrPercentageValue) : Width("fit-content($value)")
    object MaxContext : Width("max-content")
    object MinContent : Width("min-content")

    // Global
    object Inherit : Width("inherit")
    object Initial : Width("initial")
    object Revert : Width("revert")
    object RevertLayer : Width("revert-layer")
    object Unset : Width("unset")
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

sealed class Height private constructor(val value: String) {
    // Keyword
    class FitContent(value: CSSLengthOrPercentageValue) : Height("fit-content($value)")
    object MaxContext : Height("max-content")
    object MinContent : Height("min-content")

    // Global
    object Inherit : Height("inherit")
    object Initial : Height("initial")
    object Revert : Height("revert")
    object RevertLayer : Height("revert-layer")
    object Unset : Height("unset")
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

sealed class MaxWidth private constructor(val value: String) {
    // Keyword
    class FitContent(value: CSSLengthOrPercentageValue) : MaxWidth("fit-content($value)")
    object MaxContext : MaxWidth("max-content")
    object MinContent : MaxWidth("min-content")
    object None : MaxWidth("none")

    // Global
    object Inherit : MaxWidth("inherit")
    object Initial : MaxWidth("initial")
    object Revert : MaxWidth("revert")
    object RevertLayer : MaxWidth("revert-layer")
    object Unset : MaxWidth("unset")
}

fun StyleScope.maxWidth(maxWidth: MaxWidth) {
    property("max-width", maxWidth.value)
}

// endregion

// region height See: https://developer.mozilla.org/en-US/docs/Web/CSS/height

sealed class MaxHeight private constructor(val value: String) {
    // Keyword
    class FitContent(value: CSSLengthOrPercentageValue) : MaxHeight("fit-content($value)")
    object MaxContext : MaxHeight("max-content")
    object MinContent : MaxHeight("min-content")
    object None : MaxHeight("none")

    // Global
    object Inherit : MaxHeight("inherit")
    object Initial : MaxHeight("initial")
    object Revert : MaxHeight("revert")
    object RevertLayer : MaxHeight("revert-layer")
    object Unset : MaxHeight("unset")
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
