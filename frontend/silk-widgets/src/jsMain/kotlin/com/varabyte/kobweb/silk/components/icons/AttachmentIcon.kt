package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun AttachmentIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(21.843, 3.455)
                ellipticalArc(6.961, 6.961, 0, 0, 0, -9.846, 0, isRelative = true)
                lineTo(1.619, 13.832)
                ellipticalArc(5.128, 5.128, 0, 0, 0, 7.252, 7.252, isRelative = true)
                lineTo(17.3, 12.653)
                ellipticalArc(3.293, 3.293, 0, 1, 0, 12.646, 8)
                lineTo(7.457, 13.184)
                ellipticalArc(1, 1, 0, 1, 0, 8.871, 14.6)
                lineTo(14.06, 9.409)
                ellipticalArc(1.294, 1.294, 0, 0, 1, 1.829, 1.83, isRelative = true)
                lineTo(7.457, 19.67)
                ellipticalArc(3.128, 3.128, 0, 0, 1, -4.424, -4.424, isRelative = true)
                lineTo(13.411, 4.869)
                ellipticalArc(4.962, 4.962, 0, 1, 1, 7.018, 7.018, isRelative = true)
                lineTo(12.646, 19.67)
                ellipticalArc(1, 1, 0, 1, 0, 1.414, 1.414, isRelative = true)
                lineTo(21.843, 13.3)
                ellipticalArc(6.96, 6.96, 0, 0, 0, 0, -9.846, isRelative = true)
                closePath()
            }
        }
    }
}