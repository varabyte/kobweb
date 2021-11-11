package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.fillMaxSize
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme

val SurfaceStyle = ComponentStyle("silk-surface") { colorMode ->
    base = Modifier.background(SilkTheme.palettes[colorMode].background)
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