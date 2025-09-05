package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun UploadIcon(modifier: Modifier = Modifier) {
    createIcon(viewBox = ViewBox(0, 0, 14, 14), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(11.2857, 7.94286)
                lineTo(10.08571, 9.14286)
                lineTo(7.85714, 6.85214)
                lineTo(7.85714, 13)
                lineTo(6.14286, 13)
                lineTo(6.14286, 6.85214)
                lineTo(3.91429, 9.14286)
                lineTo(2.71429, 7.94286)
                lineTo(7, 3.57143)
                lineTo(11.2857, 7.94286)
                closePath()
                moveTo(1, 2.71429)
                lineTo(1, 1)
                lineTo(13, 1)
                lineTo(13, 2.71429)
                lineTo(1, 2.71429)
                closePath()
            }
        }
    }
}