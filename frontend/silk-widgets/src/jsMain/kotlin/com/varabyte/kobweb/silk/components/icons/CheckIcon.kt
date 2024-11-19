package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Polyline
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun CheckIcon(modifier: Modifier = Modifier) {
    createIcon(ViewBox.sized(24, 20), renderStyle = IconRenderStyle.Stroke(4), attrs = modifier.toAttrs()) {
        Polyline {
            points(3 to 12, 9 to 18, 21 to 2)
        }
    }
}
