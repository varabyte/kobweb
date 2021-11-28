package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.KobwebApp
import org.jetbrains.compose.web.css.Style

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    remember {
        // Use (abuse?) remember to run logic only first time SilkApp is called
        initSilk { ctx -> initSilkHook(ctx) }
    }

    KobwebApp {
        Style(SilkStyleSheet)
        content()
    }
}