package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.initSilk
import org.jetbrains.compose.web.css.*

@Composable
fun prepareSilkFoundation(initSilk: (InitSilkContext) -> Unit = {}, content: @Composable () -> Unit) {
    key(Unit) {
        // Use (abuse?) key to run logic only first time SilkApp is called
        initSilk { ctx -> initSilk(ctx) }
    }

    Style(KobwebComposeStyleSheet)
    Style(SilkStyleSheet)
    renderWithDeferred {
        content()
    }
}
