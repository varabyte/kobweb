package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.text.TextStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
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

object ComponentStyleSheet : StyleSheet()

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    remember {
        // Use (abuse?) remember to run logic only first time SilkApp is called
        val mutableTheme = MutableSilkTheme()
        mutableTheme.registerComponentStyle(ButtonStyle)
        mutableTheme.registerComponentStyle(CanvasStyle)
        mutableTheme.registerComponentStyle(SurfaceStyle)
        mutableTheme.registerComponentStyle(TextStyle)
        mutableTheme.registerComponentStyle(LinkStyle)
        initSilkHook(InitSilkContext(mutableTheme))

        SilkTheme = ImmutableSilkTheme(mutableTheme)
        SilkTheme.registerStyles(ComponentStyleSheet)
    }

    KobwebApp {
        Style(SilkStyleSheet)
        Style(ComponentStyleSheet)
        content()
    }
}
