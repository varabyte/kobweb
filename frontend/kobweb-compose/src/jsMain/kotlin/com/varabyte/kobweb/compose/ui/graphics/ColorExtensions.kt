package com.varabyte.kobweb.compose.ui.graphics

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba

fun Color.toCssColor(): CSSColorValue {
    return when (this) {
        is Color.Rgb -> {
            if (this.alpha == 0xFF) {
                rgb(this.red, this.green, this.blue)
            } else {
                rgba(this.red, this.green, this.blue, this.alpha)
            }
        }
    }
}