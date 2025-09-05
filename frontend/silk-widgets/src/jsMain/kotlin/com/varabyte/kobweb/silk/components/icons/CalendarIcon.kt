package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun CalendarIcon(modifier: Modifier = Modifier) {
    createIcon(viewBox = ViewBox(0, 0, 24, 24), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("M19 4h-2V2h-2v2H9V2H7v2H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2m0 16H5V9h14v11m0-13H5V6h14v1z")
        }
    }
}