package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

@Composable
fun SettingsIcon(modifier: Modifier = Modifier) {
    createIcon(
        viewBox = ViewBox(0, 0, 24, 24),
        renderStyle = IconRenderStyle.Stroke(2),
        attrs = modifier.toAttrs()
    ) {
        Path {
            d(
                // Outer gear
                "M19.14 12.94a7.94 7.94 0 0 0 0-1.88l2.11-1.65" +
                    "a.5.5 0 0 0 .12-.63l-2-3.46a.5.5 0 0 0-.6-.22" +
                    "l-2.49 1a7.79 7.79 0 0 0-1.62-.94l-.38-2.65" +
                    "A.5.5 0 0 0 13 2h-2a.5.5 0 0 0-.5.42l-.38 2.65" +
                    "a7.79 7.79 0 0 0-1.62.94l-2.49-1a.5.5 0 0 0-.6.22" +
                    "l-2 3.46a.5.5 0 0 0 .12.63l2.11 1.65a7.94 7.94 0 0 0 0 1.88" +
                    "l-2.11 1.65a.5.5 0 0 0-.12.63l2 3.46a.5.5 0 0 0 .6.22" +
                    "l2.49-1a7.79 7.79 0 0 0 1.62.94l.38 2.65A.5.5 0 0 0 11 22h2" +
                    "a.5.5 0 0 0 .5-.42l.38-2.65a7.79 7.79 0 0 0 1.62-.94" +
                    "l2.49 1a.5.5 0 0 0 .6-.22l2-3.46a.5.5 0 0 0-.12-.63z"
            )
        }
        Path {
            // Perfect circle for the center
            d("M12 15.5a3.5 3.5 0 1 1 0-7a3.5 3.5 0 0 1 0 7z")
        }
    }
}
