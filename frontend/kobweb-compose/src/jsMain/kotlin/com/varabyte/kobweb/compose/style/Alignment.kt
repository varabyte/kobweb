package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.ui.Alignment

fun Alignment.toClassName() = when(this) {
    Alignment.TopStart -> "kobweb-top-start"
    Alignment.TopCenter -> "kobweb-top-center"
    Alignment.TopEnd -> "kobweb-top-end"
    Alignment.CenterStart -> "kobweb-center-start"
    Alignment.Center -> "kobweb-center"
    Alignment.CenterEnd -> "kobweb-center-end"
    Alignment.BottomStart -> "kobweb-bottom-start"
    Alignment.BottomCenter -> "kobweb-bottom"
    Alignment.BottomEnd -> "kobweb-bottom-end"
    else -> error("Unexpected alignment: $this")
}

fun Alignment.Vertical.toClassName() = when(this) {
    Alignment.Top -> "kobweb-top"
    Alignment.CenterVertically -> "kobweb-center-vert"
    Alignment.Bottom -> "kobweb-bottom"
    else -> error("Unexpected alignment: $this")
}

fun Alignment.Horizontal.toClassName() = when(this) {
    Alignment.Start -> "kobweb-start"
    Alignment.CenterHorizontally -> "kobweb-center-horiz"
    Alignment.End -> "kobweb-end"
    else -> error("Unexpected alignment: $this")
}