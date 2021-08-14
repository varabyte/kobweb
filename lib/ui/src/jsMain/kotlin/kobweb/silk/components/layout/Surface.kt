package kobweb.silk.components.layout

import androidx.compose.runtime.Composable
import kobweb.compose.foundation.layout.Box
import kobweb.compose.ui.Modifier
import kobweb.compose.ui.background
import kobweb.compose.ui.fillMaxSize
import kobweb.compose.ui.graphics.Color
import kobweb.silk.theme.SilkPallete
import kobweb.silk.theme.shapes.Rect
import kobweb.silk.theme.shapes.Shape
import kobweb.silk.theme.shapes.clip

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = SilkPallete.current.surface,
    shape: Shape = Rect(),
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .background(color)
            .clip(shape),
        content = content
    )
}
