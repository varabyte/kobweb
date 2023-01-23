package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.init.initSilkHook
import org.jetbrains.compose.web.css.*

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    remember {
        // Use (abuse?) remember to run logic only first time SilkApp is called
        initSilk { ctx -> initSilkHook(ctx) }
    }

    KobwebApp {
        Style(KobwebComposeStyleSheet)
        Style(SilkStyleSheet)
        content()
    }
}