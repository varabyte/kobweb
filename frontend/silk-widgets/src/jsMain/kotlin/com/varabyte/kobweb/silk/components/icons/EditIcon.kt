package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun EditIcon(modifier: Modifier = Modifier) {
    // We use a standard 24x24 viewBox and set the render style to stroke for an outlined icon.
    createIcon(viewBox = ViewBox(0, 0, 24, 24), renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        // This path data draws a classic pen/pencil icon.
        Path {
            d("M17 3a2.828 2.828 0 114 4L7.5 20.5 2 22l1.5-5.5L17 3z")
        }
    }
}
