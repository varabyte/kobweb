package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun ChevronLeftIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(15.41, 7.41)
                lineTo(14, 6)
                lineTo(-6, 6, true)
                lineTo(6, 6, true)
                lineTo(1.41, -1.41, true)
                lineTo(10.83, 12)
                closePath()
            }
        }
    }
}