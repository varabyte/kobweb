package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.init.initSilkHook
import com.varabyte.kobweb.silk.init.setSilkVariables
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    key(Unit) {
        // Use (abuse?) key to run logic only first time SilkApp is called
        initSilk { ctx -> initSilkHook(ctx) }
    }
    val root = remember { document.getElementById("root") as HTMLElement }
    root.setSilkVariables()

    KobwebApp {
        Style(KobwebComposeStyleSheet)
        Style(SilkStyleSheet)
        content()
    }
}