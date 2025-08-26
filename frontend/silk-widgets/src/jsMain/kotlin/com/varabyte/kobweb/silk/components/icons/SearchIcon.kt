package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun SearchIcon(modifier: Modifier = Modifier) {
    createIcon(viewBox = ViewBox(0, 0, 24, 24), renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d("M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z")
        }
    }
}
