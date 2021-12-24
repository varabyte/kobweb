package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.CSSBorder
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSLengthValue
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.style
import org.jetbrains.compose.web.css.width

inline fun StyleBuilder.borderTop(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-top", CSSBorder().apply(borderBuild))
}

fun StyleBuilder.borderTop(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderTop {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

inline fun StyleBuilder.borderBottom(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-bottom", CSSBorder().apply(borderBuild))
}

fun StyleBuilder.borderBottom(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderBottom {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

inline fun StyleBuilder.borderLeft(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-left", CSSBorder().apply(borderBuild))
}

fun StyleBuilder.borderLeft(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderLeft {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}

inline fun StyleBuilder.borderRight(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-right", CSSBorder().apply(borderBuild))
}

fun StyleBuilder.borderRight(
    width: CSSLengthValue? = null,
    style: LineStyle? = null,
    color: CSSColorValue? = null
) {
    borderRight {
        width?.let { width(it) }
        style?.let { style(it) }
        color?.let { color(it) }
    }
}