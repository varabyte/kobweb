package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun CodeIcon(modifier: Modifier = Modifier) {
    createIcon(
        // Updated viewbox (instead of path, which would have been harder) to appear larger and more centered.
        viewBox = ViewBox(4, 4, 16, 16),
        renderStyle = IconRenderStyle.Stroke(2),
        attrs = modifier.toAttrs()
    ) {
        // Left angle bracket <
        Path {
            d("M8 8l-4 4 4 4")
        }
        // Forward slash /
        Path {
            d("M10 16l4-8")
        }
        // Right angle bracket >
        Path {
            d("M16 8l4 4-4 4")
        }
    }
}
