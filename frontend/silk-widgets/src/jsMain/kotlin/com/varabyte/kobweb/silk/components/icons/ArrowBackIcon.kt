package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun ArrowBackIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(20, 11)
                horizontalLineTo(7.83)
                lineTo(5.59, -5.59, isRelative = true)
                lineTo(12, 4)
                lineTo(-8, 8, isRelative = true)
                lineTo(8, 8, isRelative = true)
                lineTo(1.41, -1.41, isRelative = true)
                lineTo(7.83, 13)
                horizontalLineTo(20)
                verticalLineTo(-2, isRelative = true)
                closePath()
            }
        }
    }
}