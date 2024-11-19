package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Line
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun HamburgerIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(3), attrs = modifier.toAttrs()) {
        for (y in listOf(3, 12, 21)) {
            Line {
                x1(0)
                x2(23)
                y1(y)
                y2(y)
            }
        }
    }
}