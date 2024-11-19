package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun ArrowUpIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(4, 12)
                lineTo(1.41, 1.41, isRelative = true)
                lineTo(11, 7.83)
                verticalLineTo(20)
                horizontalLineTo(2, isRelative = true)
                verticalLineTo(7.83)
                lineTo(5.58, 5.59, isRelative = true)
                lineTo(20, 12)
                lineTo(-8, -8, isRelative = true)
                lineTo(-8, 8, isRelative = true)
                closePath()
            }
        }
    }
}