package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

inline fun StyleScope.borderTop(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-top", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderTop(
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

inline fun StyleScope.borderBottom(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-bottom", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderBottom(
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

inline fun StyleScope.borderLeft(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-left", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderLeft(
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

inline fun StyleScope.borderRight(crossinline borderBuild: CSSBorder.() -> Unit) {
    property("border-right", CSSBorder().apply(borderBuild))
}

fun StyleScope.borderRight(
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