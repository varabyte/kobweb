package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import com.varabyte.kobweb.core.KobwebApp
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.ms

object SilkStyleSheet : StyleSheet() {
    init {
        "*" style {
            // The following transition settings make changing the color mode look good
            transitionProperty("background-color", "color", "opacity")
            transitionDuration(200.ms)
        }
    }
}

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    KobwebApp {
        Style(SilkStyleSheet)
        content()
    }
}