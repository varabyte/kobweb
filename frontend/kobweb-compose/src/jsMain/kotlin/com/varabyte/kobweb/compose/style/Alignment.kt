package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.ui.Alignment

fun Alignment.toClassName() = when(this) {
    Alignment.TopStart -> "kobweb-align-top-start"
    Alignment.TopCenter -> "kobweb-align-top-center"
    Alignment.TopEnd -> "kobweb-align-top-end"
    Alignment.CenterStart -> "kobweb-align-center-start"
    Alignment.Center -> "kobweb-align-center"
    Alignment.CenterEnd -> "kobweb-align-center-end"
    Alignment.BottomStart -> "kobweb-align-bottom-start"
    Alignment.BottomCenter -> "kobweb-align-bottom"
    Alignment.BottomEnd -> "kobweb-align-bottom-end"
    else -> error("Unexpected alignment: $this")
}

fun Alignment.Vertical.toClassName() = when(this) {
    Alignment.Top -> "kobweb-align-top"
    Alignment.CenterVertically -> "kobweb-align-center-vert"
    Alignment.Bottom -> "kobweb-align-bottom"
    else -> error("Unexpected alignment: $this")
}

fun Alignment.Horizontal.toClassName() = when(this) {
    Alignment.Start -> "kobweb-align-start"
    Alignment.CenterHorizontally -> "kobweb-align-center-horiz"
    Alignment.End -> "kobweb-align-end"
    else -> error("Unexpected alignment: $this")
}