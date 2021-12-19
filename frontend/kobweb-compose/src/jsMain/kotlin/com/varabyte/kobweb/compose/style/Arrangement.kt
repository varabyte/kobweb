package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.foundation.layout.Arrangement

fun Arrangement.Horizontal.toClassName() = when(this) {
    Arrangement.End -> "kobweb-arrange-end"
    Arrangement.Start -> "kobweb-arrange-start"
    Arrangement.Center -> Arrangement.Center.toClassName() // delegate to HorizontalOrVertical.toClassName
    else -> error("Unexpected arrangement: $this")
}

fun Arrangement.Vertical.toClassName() = when(this) {
    Arrangement.Top -> "kobweb-arrange-top"
    Arrangement.Bottom -> "kobweb-arrange-bottom"
    Arrangement.Center -> Arrangement.Center.toClassName() // delegate to HorizontalOrVertical.toClassName
    else -> error("Unexpected arrangement: $this")
}

fun Arrangement.HorizontalOrVertical.toClassName() = when(this) {
    Arrangement.Center -> "kobweb-arrange-center"
    else -> error("Unexpected arrangement: $this")
}