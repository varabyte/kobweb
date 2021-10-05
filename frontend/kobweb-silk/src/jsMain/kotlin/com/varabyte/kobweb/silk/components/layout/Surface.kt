package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.fillMaxSize
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.Shape
import com.varabyte.kobweb.silk.theme.shapes.clip

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = SilkTheme.palette.surface,
    shape: Shape = Rect(),
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .background(color)
            .clip(shape),
    ) {
        content()
    }
}