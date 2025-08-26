package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun EyeIcon(modifier: Modifier = Modifier) {
    createIcon(
        viewBox = ViewBox(0, 0, 24, 24),
        renderStyle = IconRenderStyle.Stroke(2),
        attrs = modifier.toAttrs()
    ) {
        // Outer eye shape
        Path {
            d("M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z")
        }
        // Pupil
        Path {
            d("M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6z")
        }
    }
}
