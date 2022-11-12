package com.varabyte.kobweb.compose.ui.graphics

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba

@Deprecated("Color.toCssColor is no longer required as Color now inherits CSSColorValue",
    ReplaceWith("this")
)
fun Color.toCssColor(): CSSColorValue {
    return this
}