package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-decoration-break
sealed class BoxDecorationBreak(val value: String) {
    // Keyword
    object Slice : BoxDecorationBreak("slice")
    object Clone : BoxDecorationBreak("clone")

    // Global
    object Inherit : BoxDecorationBreak("inherit")
    object Initial : BoxDecorationBreak("initial")
    object Revert : BoxDecorationBreak("revert")
    object RevertLayer : BoxDecorationBreak("revert-layer")
    object Unset : BoxDecorationBreak("unset")
}

fun StyleScope.boxDecorationBreak(boxDecorationBreak: BoxDecorationBreak) {
    property("box-decoration-break", boxDecorationBreak.value)
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
sealed class BoxSizing(val value: String) {
    // Keyword
    object BorderBox : BoxSizing("border-box")
    object ContentBox : BoxSizing("content-box")

    // Global
    object Inherit : BoxSizing("inherit")
    object Initial : BoxSizing("initial")
    object Revert : BoxSizing("revert")
    object RevertLayer : BoxSizing("revert-layer")
    object Unset : BoxSizing("unset")
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
            append(' ')
            append(spreadRadius)
        }

        if (color != null) {
            append(' ')
            append(color)
        }
    })
}
