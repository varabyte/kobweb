package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun DownloadIcon(modifier: Modifier = Modifier) {
    createIcon(viewBox = ViewBox(0, 0, 14, 14), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(11.2857, 6.05714)
                lineTo(10.08571, 4.85714)
                lineTo(7.85714, 7.14786)
                lineTo(7.85714, 1)
                lineTo(6.14286, 1)
                lineTo(6.14286, 7.14786)
                lineTo(3.91429, 4.85714)
                lineTo(2.71429, 6.05714)
                lineTo(7, 10.42857)
                lineTo(11.2857, 6.05714)
                closePath()
                moveTo(1, 11.2857)
                lineTo(1, 13)
                lineTo(13, 13)
                lineTo(13, 11.2857)
                lineTo(1, 11.2857)
                closePath()
            }
        }
    }
}