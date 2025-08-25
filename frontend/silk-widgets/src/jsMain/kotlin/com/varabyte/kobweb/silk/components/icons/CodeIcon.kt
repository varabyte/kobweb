package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun CodeIcon(modifier: Modifier = Modifier) {
    createIcon(
        viewBox = ViewBox(0, 0, 24, 24),
        renderStyle = IconRenderStyle.Stroke(2),
        attrs = modifier.toAttrs()
    ) {
        // Left angle bracket <
        Path {
            d("M8 8l-4 4 4 4")
        }
        // Forward slash /
        Path {
            d("M12 16l4-8")
        }
        // Right angle bracket >
        Path {
            d("M16 8l4 4-4 4")
        }
    }
}
