package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun ChevronUpIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(12, 8)
                lineTo(-6, 6, true)
                lineTo(1.41, 1.41, true)
                lineTo(12, 10.83)
                lineTo(4.59, 4.58, true)
                lineTo(18, 14)
                closePath()
            }
        }
    }
}