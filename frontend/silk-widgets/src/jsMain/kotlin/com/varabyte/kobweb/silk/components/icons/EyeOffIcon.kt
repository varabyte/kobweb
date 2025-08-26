package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun EyeOffIcon(modifier: Modifier = Modifier) {
    createIcon(
        viewBox = ViewBox(0, 0, 24, 24),
        renderStyle = IconRenderStyle.Stroke(2),
        attrs = modifier.toAttrs()
    ) {
        // Outer eye shape
        Path {
            d("M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a20.87 20.87 0 0 1 5.06-5.94")
        }
        // Inner eye shape (hidden state)
        Path {
            d("M9.88 9.88a3 3 0 0 0 4.24 4.24")
        }
        // Slash across the eye
        Path {
            d("M1 1l22 22")
        }
        // Remaining outer eye arc
        Path {
            d("M6.1 6.1A10.94 10.94 0 0 1 12 5c7 0 11 7 11 7a20.87 20.87 0 0 1-4.06 4.94")
        }
    }
}
