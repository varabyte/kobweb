package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.fillMaxSize
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.Shape
import com.varabyte.kobweb.silk.theme.shapes.clip

val SurfaceKey = ComponentKey("silk-surface")
object DefaultSurfaceModifier : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        return Modifier.background(SilkTheme.palette.surface)
    }
}

/**
 * An area which provides a SilkTheme-aware colored area, usually for largish UI areas.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    shape: Shape = Rect(),
    content: @Composable () -> Unit
) {
    Box(
        SilkTheme.componentModifiers[SurfaceKey].toModifier(null)
            .then(modifier)
            .clip(shape),
    ) {
        content()
    }
}