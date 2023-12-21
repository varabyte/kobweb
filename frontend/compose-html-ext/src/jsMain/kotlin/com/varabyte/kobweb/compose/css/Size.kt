package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/width
class Width private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = Width("fit-content")
        val MaxContent get() = Width("max-content")

        @Deprecated(
            "Misspelling fixed. Please use `MaxContent` instead.",
            ReplaceWith("Width.MaxContent", "com.varabyte.kobweb.compose.css.Width")
        )
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

fun AttrsScope<*>.width(width: Int) {
    attr("width", width.toString())
}

fun StyleScope.width(width: Width) {
    property("width", width)
}

fun StyleScope.minWidth(minWidth: MinWidth) {
    property("min-width", minWidth)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/height
class Height private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = Height("fit-content")
        val MaxContent get() = Height("max-content")

        @Deprecated(
            "Misspelling fixed. Please use `MaxContent` instead.",
            ReplaceWith("Height.MaxContent", "com.varabyte.kobweb.compose.css.Height")
        )
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

fun AttrsScope<*>.height(height: Int) {
    attr("height", height.toString())
}

fun StyleScope.height(height: Height) {
    property("height", height)
}

fun StyleScope.minHeight(minHeight: MinHeight) {
    property("min-height", minHeight)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/max-width
class MaxWidth private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = MaxWidth("fit-content")
        val MaxContent get() = MaxWidth("max-content")

        @Deprecated(
            "Misspelling fixed. Please use `MaxContent` instead.",
            ReplaceWith("MaxWidth.MaxContent", "com.varabyte.kobweb.compose.css.MaxWidth")
        )
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

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/height
class MaxHeight private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val FitContent get() = MaxHeight("fit-content")
        val MaxContent get() = MaxHeight("max-content")

        @Deprecated(
            "Misspelling fixed. Please use `MaxContent` instead.",
            ReplaceWith("MaxHeight.MaxContent", "com.varabyte.kobweb.compose.css.MaxHeight")
        )
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
