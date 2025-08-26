package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun UserIcon(modifier: Modifier = Modifier) {
    createIcon(viewBox = ViewBox(0, 0, 24, 24), renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d("M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2")
        }
        Path {
            d("M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z")
        }
    }
}