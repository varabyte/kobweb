package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Line
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun PlusIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(4), attrs = modifier.toAttrs()) {
        Line {
            x1(3)
            x2(21)
            y1(12)
            y2(12)
        }
        Line {
            x1(12)
            x2(12)
            y1(3)
            y2(21)
        }
    }
}
