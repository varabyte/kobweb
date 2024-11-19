package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Line
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun CloseIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(3), attrs = modifier.toAttrs()) {
        Line {
            x1(1)
            x2(23)
            y1(1)
            y2(23)
        }
        Line {
            x1(23)
            x2(1)
            y1(1)
            y2(23)
        }
    }
}
