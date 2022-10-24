package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.transitionDuration
import com.varabyte.kobweb.compose.ui.modifiers.transitionProperty
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

val SurfaceStyle = ComponentStyle("silk-surface") {
    base {
        val palette = colorMode.toSilkPalette()
        Modifier
            .backgroundColor(palette.background)
            .color(palette.color)
            // Toggling color mode looks much more engaging if it animates instead of being instant
            .transitionProperty("background-color")
            .transitionDuration(200.ms)
    }
}

/**
 * A panel which encapsulates a SilkTheme-aware area.
 *
 * This should probably be somewhere near the root of your app as it defines colors that cascade down through its
 * children with Silk colors.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    variant: ComponentVariant? = null,
    elementScope: (@Composable ElementScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        SurfaceStyle.toModifier(variant).then(modifier),
        elementScope = elementScope
    ) {
        content()
    }
}