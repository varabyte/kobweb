package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun MoonIcon(modifier: Modifier = Modifier) {
    createIcon(ViewBox.sized(200), renderStyle = IconRenderStyle.Stroke(20), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(175, 106.583)
                ellipticalArc(75, 75, 0, 1, 1, 93.417, 25)
                ellipticalArc(58.333, 58.333, 0, 0, 0, 175, 106.583)
                closePath()
            }
        }
    }
}