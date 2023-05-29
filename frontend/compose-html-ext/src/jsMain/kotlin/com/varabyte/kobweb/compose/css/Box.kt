package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-decoration-break
class BoxDecorationBreak private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val Slice get() = BoxDecorationBreak("slice")
        val Clone get() = BoxDecorationBreak("clone")

        // Global
        val Inherit get() = BoxDecorationBreak("inherit")
        val Initial get() = BoxDecorationBreak("initial")
        val Revert get() = BoxDecorationBreak("revert")
        val Unset get() = BoxDecorationBreak("unset")
    }
}

fun StyleScope.boxDecorationBreak(boxDecorationBreak: BoxDecorationBreak) {
    property("box-decoration-break", boxDecorationBreak)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
class BoxSizing private constructor(private val value: String): StylePropertyValue {
    override fun toString() = value

    companion object {
        // Keyword
        val BorderBox get() = BoxSizing("border-box")
        val ContentBox get() = BoxSizing("content-box")

        // Global
        val Inherit get() = BoxSizing("inherit")
        val Initial get() = BoxSizing("initial")
        val Revert get() = BoxSizing("revert")
        val Unset get() = BoxSizing("unset")
    }
}

fun StyleScope.boxSizing(boxSizing: BoxSizing) {
    boxSizing(boxSizing.toString())
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
fun StyleScope.boxShadow(value: String) {
    property("box-shadow", value)
}

fun StyleScope.boxShadow(
    offsetX: CSSLengthValue = 0.px,
    offsetY: CSSLengthValue = 0.px,
    blurRadius: CSSLengthValue? = null,
    spreadRadius: CSSLengthValue? = null,
    color: CSSColorValue? = null,
    inset: Boolean = false,
) {
    boxShadow(buildString {
        if (inset) {
            append("inset")
            append(' ')
        }
        append(offsetX)
        append(' ')
        append(offsetY)

        if (blurRadius != null) {
            append(' ')
            append(blurRadius)
        }

        if (spreadRadius != null) {
            if (blurRadius == null) {
                append(' ')
                append('0')
            }
            append(' ')
            append(spreadRadius)
        }

        if (color != null) {
            append(' ')
            append(color)
        }
    })
}
