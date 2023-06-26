package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.foundation.layout.Arrangement

fun Arrangement.Horizontal.toClassName() = when (this) {
    Arrangement.End -> "kobweb-arrange-end"
    Arrangement.Start -> "kobweb-arrange-start"
    is Arrangement.HorizontalOrVertical -> this.toClassName()
}

fun Arrangement.Vertical.toClassName() = when (this) {
    Arrangement.Top -> "kobweb-arrange-top"
    Arrangement.Bottom -> "kobweb-arrange-bottom"
    is Arrangement.HorizontalOrVertical -> this.toClassName()
}

fun Arrangement.HorizontalOrVertical.toClassName() = when (this) {
    Arrangement.Center -> "kobweb-arrange-center"
    Arrangement.SpaceAround -> "kobweb-arrange-space-around"
    Arrangement.SpaceBetween -> "kobweb-arrange-space-between"
    Arrangement.SpaceEvenly -> "kobweb-arrange-space-evenly"
    Arrangement.FromStyle -> "kobweb-arrange-from-style"
}
