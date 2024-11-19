package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Circle
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineCap
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun QuestionIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            strokeLineCap(SVGStrokeLineCap.Round)
            d("M9,9a3,3,0,1,1,4,2.829,1.5,1.5,0,0,0-1,1.415V14.25")
        }
        Path {
            strokeLineCap(SVGStrokeLineCap.Round)
            d("M12,17.25a.375.375,0,1,0,.375.375A.375.375,0,0,0,12,17.25h0")
        }
        Circle {
            strokeMiterLimit(10)
            cx(12)
            cy(12)
            r(11.25)
        }
    }
}
