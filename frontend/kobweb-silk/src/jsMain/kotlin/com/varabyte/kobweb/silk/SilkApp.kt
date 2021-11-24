package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.graphics.CanvasStyle
import com.varabyte.kobweb.silk.components.layout.SimpleGridStyle
import com.varabyte.kobweb.silk.components.layout.SurfaceStyle
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.text.TextStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkConfigInstance
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet

object ComponentStyleSheet : StyleSheet()

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    remember {
        // Use (abuse?) remember to run logic only first time SilkApp is called
        val mutableTheme = MutableSilkTheme()
        mutableTheme.registerComponentStyle(ButtonStyle)
        mutableTheme.registerComponentStyle(CanvasStyle)
        mutableTheme.registerComponentStyle(SimpleGridStyle)
        mutableTheme.registerComponentStyle(SurfaceStyle)
        mutableTheme.registerComponentStyle(TextStyle)
        mutableTheme.registerComponentStyle(LinkStyle)
        initSilkHook(InitSilkContext(SilkConfigInstance, mutableTheme))

        SilkTheme = ImmutableSilkTheme(mutableTheme)
        SilkTheme.registerStyles(ComponentStyleSheet)
    }

    KobwebApp {
        Style(ComponentStyleSheet)
        content()
    }
}