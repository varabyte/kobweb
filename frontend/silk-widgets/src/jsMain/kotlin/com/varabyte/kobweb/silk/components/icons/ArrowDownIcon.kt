package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun ArrowDownIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(20, 12)
                lineTo(-1.41, -1.41, isRelative = true)
                lineTo(13, 16.17)
                verticalLineTo(4)
                horizontalLineTo(-2, isRelative = true)
                verticalLineTo(12.17, isRelative = true)
                lineTo(-5.58, -5.59, isRelative = true)
                lineTo(4, 12)
                lineTo(8, 8, isRelative = true)
                lineTo(8, -8, isRelative = true)
                closePath()
            }
        }
    }
}