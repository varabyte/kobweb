package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun CheckCircleIcon(modifier: Modifier = Modifier) {
    // We use a viewBox of 24x24, a common standard for icons.
    // IconRenderStyle.Stroke() ensures the icon is an outline.
    createIcon(viewBox = ViewBox(0, 0, 24, 24), renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        // This path draws the circle part of the icon.
        Path {
            d("M12 2a10 10 0 100 20 10 10 0 000-20z")
        }
        // This path draws the checkmark part of the icon.
        Path {
            d("M9 12l2 2 4-4")
        }
    }
}
