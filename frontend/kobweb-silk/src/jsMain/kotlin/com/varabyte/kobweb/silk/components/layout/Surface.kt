package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.background
import com.varabyte.kobweb.compose.ui.fillMaxSize
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
    shape: Shape = Rect(),
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .background(SilkTheme.palette.surface)
            .then(modifier)
            .clip(shape),
    ) {
        content()
    }
}