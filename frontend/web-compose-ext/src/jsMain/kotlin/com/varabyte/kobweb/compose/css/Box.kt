package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-decoration-break
class BoxDecorationBreak private constructor(val value: String) {
    companion object {
        // Keyword
        val Slice = BoxDecorationBreak("slice")
        val Clone = BoxDecorationBreak("clone")

        // Global
        val Inherit = BoxDecorationBreak("inherit")
        val Initial = BoxDecorationBreak("initial")
        val Revert = BoxDecorationBreak("revert")
        val RevertLayer = BoxDecorationBreak("revert-layer")
        val Unset = BoxDecorationBreak("unset")
    }
}

fun StyleScope.boxDecorationBreak(boxDecorationBreak: BoxDecorationBreak) {
    property("box-decoration-break", boxDecorationBreak.value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
class BoxSizing private constructor(val value: String) {
    companion object {
        // Keyword
        val BorderBox = BoxSizing("border-box")
        val ContentBox = BoxSizing("content-box")

        // Global
        val Inherit = BoxSizing("inherit")
        val Initial = BoxSizing("initial")
        val Revert = BoxSizing("revert")
        val RevertLayer = BoxSizing("revert-layer")
        val Unset = BoxSizing("unset")
    }
}

fun StyleScope.boxSizing(boxSizing: BoxSizing) {
    boxSizing(boxSizing.value)
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
