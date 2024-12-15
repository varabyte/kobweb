package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.KobwebComposeStyles
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.silk.init.SilkWidgetVariables
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.document

@Composable
fun SilkApp(content: @Composable () -> Unit) {
    KobwebApp {
        KobwebComposeStyles()
        SilkFoundationStyles()
        SilkWidgetVariables()

        // Create a meta element to communicate the current theme color of the overall site to the browser
        // This affects UI like scrollbars and form controls
        run {
            val colorMode = ColorMode.current
            DisposableEffect(colorMode) {
                val colorSchemeElement = document.createElement("meta").apply {
                    setAttribute("name", "color-scheme")
                    setAttribute("content", colorMode.name.lowercase())
                }.also { document.head!!.appendChild(it) }

                onDispose {
                    colorSchemeElement.remove()
                }
            }
        }

        content()
    }
}
