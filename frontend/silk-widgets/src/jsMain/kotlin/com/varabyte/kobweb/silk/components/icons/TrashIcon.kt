package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun TrashIcon(modifier: Modifier = Modifier) {
    // We use a viewBox of 24x24 and a stroke render style for an outlined icon.
    createIcon(viewBox = ViewBox(0, 0, 24, 24), renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        // This path creates the trash can shape.
        Path {
            d("M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2")
        }
    }
}