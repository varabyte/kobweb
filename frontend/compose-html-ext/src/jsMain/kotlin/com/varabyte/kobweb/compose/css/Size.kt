package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.global.CssGlobalValues
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/width
class Width private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<Width> {
        // Keyword
        val FitContent get() = Width("fit-content")
        val MaxContent get() = Width("max-content")
        val MinContent get() = Width("min-content")
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

    companion object: CssGlobalValues<Height> {
        // Keyword
        val FitContent get() = Height("fit-content")
        val MaxContent get() = Height("max-content")
        val MinContent get() = Height("min-content")
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

    companion object: CssGlobalValues<MaxWidth> {
        // Keyword
        val FitContent get() = MaxWidth("fit-content")
        val MaxContent get() = MaxWidth("max-content")
        val MinContent get() = MaxWidth("min-content")
        val None get() = MaxWidth("none")
    }
}

fun StyleScope.maxWidth(maxWidth: MaxWidth) {
    property("max-width", maxWidth)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/height
class MaxHeight private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    companion object: CssGlobalValues<MaxHeight> {
        // Keyword
        val FitContent get() = MaxHeight("fit-content")
        val MaxContent get() = MaxHeight("max-content")
        val MinContent get() = MaxHeight("min-content")
        val None get() = MaxHeight("none")
    }
}

fun StyleScope.maxHeight(maxHeight: MaxHeight) {
    property("max-height", maxHeight)
}
