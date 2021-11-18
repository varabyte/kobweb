package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.color
import com.varabyte.kobweb.compose.ui.fillMaxSize
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.ms

val SurfaceStyle = ComponentStyle("silk-surface") { colorMode ->
    val palette = SilkTheme.palettes[colorMode]
    base = Modifier
        .background(palette.background)
        .color(palette.color)
        .styleModifier {
            // Toggling color mode looks much more engaging if it animates instead of being instant
            transitionProperty("background-color", "color")
            transitionDuration(200.ms)
        }
}

/**
 * An area which provides a SilkTheme-aware colored area, usually for largish UI areas.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    variant: ComponentVariant? = null,
    content: @Composable () -> Unit
) {
    Box(
        SurfaceStyle.toModifier(variant).then(modifier)
    ) {
        content()
    }
}