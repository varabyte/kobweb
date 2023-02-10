package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

val SurfaceStyle = ComponentStyle("silk-surface") {
    base {
        val palette = colorMode.toSilkPalette()
        Modifier
            .backgroundColor(palette.background)
            .color(palette.color)
    }
}

/**
 * A variant which provides a smoother color animation effect for this surface and all of its div children.
 *
 * Without applying this variant, colors will snap instantly between dark and light colors. With applying it, the
 * colors will transition smoothly.
 */
val AnimatedColorSurfaceVariant = SurfaceStyle.addVariant("animated-color") {
    val backgroundColorTransition = Modifier.transition(CSSTransition("background-color", 200.ms))

    base { backgroundColorTransition }
    // It looks weird if parts of the screen snap colors while others transition smoothly. Do our best to make sure all
    // container elements transition as well.
    cssRule(" div") { backgroundColorTransition }
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
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable () -> Unit
) {
    Box(
        SurfaceStyle.toModifier(variant).then(modifier),
        ref = ref,
    ) {
        content()
    }
}