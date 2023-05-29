package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*

// region width See: https://developer.mozilla.org/en-US/docs/Web/CSS/width

class Width private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = Width("fit-content")
        val MaxContent get() = Width("max-content")
        @Deprecated("Misspelling fixed. Please use `MaxContent` instead.", ReplaceWith("Width.MaxContent", "com.varabyte.kobweb.compose.css.Width"))
        val MaxContext get() = MaxContent
        val MinContent get() = Width("min-content")

        // Global
        val Inherit get() = Width("inherit")
        val Initial get() = Width("initial")
        val Revert get() = Width("revert")
        val Unset get() = Width("unset")
    }
}
typealias MinWidth = Width

fun AttrsScope<*>.width(width: CSSNumeric) {
    attr("width", width.toString())
}

fun StyleScope.width(width: Width) {
    property("width", width)
}

fun StyleScope.minWidth(minWidth: MinWidth) {
    property("min-width", minWidth)
}

// endregion

// region height See: https://developer.mozilla.org/en-US/docs/Web/CSS/height

class Height private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = Height("fit-content")
        val MaxContent get() = Height("max-content")
        @Deprecated("Misspelling fixed. Please use `MaxContent` instead.", ReplaceWith("Height.MaxContent", "com.varabyte.kobweb.compose.css.Height"))
        val MaxContext get() = MaxContent

        val MinContent get() = Height("min-content")

        // Global
        val Inherit get() = Height("inherit")
        val Initial get() = Height("initial")
        val Revert get() = Height("revert")
        val Unset get() = Height("unset")
    }
}
typealias MinHeight = Height

fun AttrsScope<*>.height(height: CSSNumeric) {
    attr("height", height.toString())
}

fun StyleScope.height(height: Height) {
    property("height", height)
}

fun StyleScope.minHeight(minHeight: MinHeight) {
    property("min-height", minHeight)
}

// endregion

// region max-width See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-width

class MaxWidth private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = MaxWidth("fit-content")
        val MaxContent get() = MaxWidth("max-content")
        @Deprecated("Misspelling fixed. Please use `MaxContent` instead.", ReplaceWith("MaxWidth.MaxContent", "com.varabyte.kobweb.compose.css.MaxWidth"))
        val MaxContext get() = MaxContent

        val MinContent get() = MaxWidth("min-content")
        val None get() = MaxWidth("none")

        // Global
        val Inherit get() = MaxWidth("inherit")
        val Initial get() = MaxWidth("initial")
        val Revert get() = MaxWidth("revert")
        val Unset get() = MaxWidth("unset")
    }
}

fun StyleScope.maxWidth(maxWidth: MaxWidth) {
    property("max-width", maxWidth)
}

// endregion

// region height See: https://developer.mozilla.org/en-US/docs/Web/CSS/height

class MaxHeight private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = MaxHeight("fit-content")
        val MaxContent get() = MaxHeight("max-content")
        @Deprecated("Misspelling fixed. Please use `MaxContent` instead.", ReplaceWith("MaxHeight.MaxContent", "com.varabyte.kobweb.compose.css.MaxHeight"))
        val MaxContext get() = MaxContent

        val MinContent get() = MaxHeight("min-content")
        val None get() = MaxHeight("none")

        // Global
        val Inherit get() = MaxHeight("inherit")
        val Initial get() = MaxHeight("initial")
        val Revert get() = MaxHeight("revert")
        val Unset get() = MaxHeight("unset")
    }
}

fun StyleScope.maxHeight(maxHeight: MaxHeight) {
    property("max-height", maxHeight)
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

class VerticalAlign private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

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
    property("vertical-align", verticalAlign)
}

fun StyleScope.verticalAlign(value: CSSNumeric) {
    property("vertical-align", value)
}

// endregion

// region aspect ratio See: https://developer.mozilla.org/en-US/docs/Web/CSS/aspect-ratio

class AspectRatio(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Auto get() = AspectRatio("auto")

        // Global values
        val Inherit get() = AspectRatio("inherit")
        val Initial get() = AspectRatio("initial")
        val Revert get() = AspectRatio("revert")
        val Unset get() = AspectRatio("unset")
    }
}

fun StyleScope.aspectRatio(ratio: Number) {
    property("aspect-ratio", ratio)
}

fun StyleScope.aspectRatio(width: Number, height: Number) {
    property("aspect-ratio", "$width / $height")
}

fun StyleScope.aspectRatio(aspectRatio: AspectRatio) {
    property("aspect-ratio", aspectRatio)
}

// endregion

// region line height See: https://developer.mozilla.org/en-US/docs/Web/CSS/line-height

class LineHeight(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keywords
        val Normal get() = LineHeight("normal")

        // Global values
        val Inherit get() = LineHeight("inherit")
        val Initial get() = LineHeight("initial")
        val Revert get() = LineHeight("revert")
        val Unset get() = LineHeight("unset")
    }
}

fun StyleScope.lineHeight(lineHeight: LineHeight) {
    property("line-height", lineHeight)
}

// endregion

// region resize See: https://developer.mozilla.org/en-US/docs/Web/CSS/resize

class Resize private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val None get() = Resize("none")
        val Both get() = Resize("both")
        val Horizontal get() = Resize("horizontal")
        val Vertical get() = Resize("vertical")
        val Block get() = Resize("block")
        val Inline get() = Resize("inline")

        // Global
        val Inherit get() = Resize("inherit")
        val Initial get() = Resize("initial")
        val Revert get() = Resize("revert")
        val Unset get() = Resize("unset")
    }
}

fun StyleScope.resize(resize: Resize) {
    property("resize", resize)
}

// endregion

// region z-index See: https://developer.mozilla.org/en-US/docs/Web/CSS/z-index

fun StyleScope.zIndex(value: Number) {
    property("z-index", value)
}

// endregion