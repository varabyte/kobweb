package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun ArrowForwardIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(12, 4)
                lineTo(-1.14, 1.41, isRelative = true)
                lineTo(16.17, 11)
                horizontalLineTo(4)
                verticalLineTo(2, isRelative = true)
                horizontalLineTo(12.17, isRelative = true)
                lineTo(-5.58, 5.59, isRelative = true)
                lineTo(12, 20)
                lineTo(8, -8, isRelative = true)
                closePath()
            }
        }
    }
}