package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import org.jetbrains.compose.web.css.*

interface App {
    @Composable
    fun render(content: @Composable () -> Unit)
}

object DefaultStyleSheet : StyleSheet() {
    init {
        "html, body" style {
            // Allow our app to stretch the full screen
            padding(0.px)
            margin(0.px)
        }

        "*" style {
            // See also: https://css-tricks.com/box-sizing
            boxSizing("border-box")

            // The following transition settings make changing the color mode look good
            transitionProperty("background-color", "color")
            transitionDuration(200.ms)
        }
    }
}

object DefaultApp : App {
    @Composable
    override fun render(content: @Composable () -> Unit) {
        Style(DefaultStyleSheet)
        content()
    }
}