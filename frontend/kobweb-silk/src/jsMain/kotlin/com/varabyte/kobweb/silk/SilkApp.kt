package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import com.varabyte.kobweb.core.DefaultApp
import com.varabyte.kobweb.silk.components.forms.ButtonKey
import com.varabyte.kobweb.silk.components.forms.DefaultButtonStyle
import com.varabyte.kobweb.silk.components.navigation.DefaultLinkStyle
import com.varabyte.kobweb.silk.components.navigation.LinkKey
import com.varabyte.kobweb.silk.components.text.DefaultTextStyle
import com.varabyte.kobweb.silk.components.text.TextKey
import com.varabyte.kobweb.silk.theme.SilkTheme
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
    remember {
        SilkTheme.componentStyles.apply {
            this[ButtonKey] = DefaultButtonStyle()
            this[LinkKey] = DefaultLinkStyle()
            this[TextKey] = DefaultTextStyle()
        }
    }
    DefaultApp {
        Style(SilkStyleSheet)
        content()
    }
}