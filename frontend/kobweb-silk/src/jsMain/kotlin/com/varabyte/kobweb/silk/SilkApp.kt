package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.silk.init.setSilkWidgetVariables
import kotlinx.browser.document

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    KobwebApp {
        prepareSilkFoundation {
            document.setSilkWidgetVariables()
            content()
        }
    }
}
