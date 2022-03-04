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
                // Alpha always has to be a float: https://www.w3schools.com/cssref/func_rgba.asp
                rgba(this.red, this.green, this.blue, this.alphaf)
            }
        }
    }
}