package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun LockIcon(modifier: Modifier = Modifier) {
    createIcon(
        viewBox = ViewBox(0, 0, 24, 24),
        renderStyle = IconRenderStyle.Stroke(2),
        attrs = modifier.toAttrs()
    ) {
        // Lock body
        Path {
            d("M5 11h14v10H5z")
        }
        // Lock shackle
        Path {
            d("M7 11V7a5 5 0 0 1 10 0v4")
        }
    }
}
