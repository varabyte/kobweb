package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.ms

val SurfaceStyle = ComponentStyle("silk-surface") {
    val palette = SilkTheme.palettes[colorMode]
    base {
        Modifier
            .background(palette.background)
            .color(palette.color)
            .styleModifier {
                // Toggling color mode looks much more engaging if it animates instead of being instant
                transitionProperty("background-color", "color")
                transitionDuration(200.ms)
            }
    }
}

/**
 * An area which defines a SilkTheme-aware area.
 *
 * This should be somewhere at the root silk widgets [Link] and [Text] as it defines their colors for them. Their colors
 * are defined here instead of on the widgets themselves because it allows users to create intermediate parent divs to
 * override colors for all their children in localized areas as necessary.
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