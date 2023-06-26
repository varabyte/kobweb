package com.varabyte.kobweb.compose.ui.graphics

import org.jetbrains.compose.web.css.*

@Deprecated(
    "Color.toCssColor is no longer required as Color now inherits CSSColorValue",
    ReplaceWith("this")
)
fun Color.toCssColor(): CSSColorValue {
    return this
}
