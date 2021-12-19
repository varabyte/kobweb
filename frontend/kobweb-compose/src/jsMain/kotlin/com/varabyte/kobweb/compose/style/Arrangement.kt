package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.foundation.layout.Arrangement

fun Arrangement.Horizontal.toClassName() = when(this) {
    Arrangement.End -> "kobweb-end"
    Arrangement.Start -> "kobweb-start"
    Arrangement.Center -> Arrangement.Center.toClassName() // delegate to HorizontalOrVertical.toClassName
    else -> error("Unexpected arrangement: $this")
}

fun Arrangement.Vertical.toClassName() = when(this) {
    Arrangement.Top -> "kobweb-top"
    Arrangement.Bottom -> "kobweb-bottom"
    Arrangement.Center -> Arrangement.Center.toClassName() // delegate to HorizontalOrVertical.toClassName
    else -> error("Unexpected arrangement: $this")
}

fun Arrangement.HorizontalOrVertical.toClassName() = when(this) {
    Arrangement.Center -> "kobweb-center"
    else -> error("Unexpected arrangement: $this")
}