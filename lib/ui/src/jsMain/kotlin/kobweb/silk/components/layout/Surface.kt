package kobweb.silk.components.layout

import androidx.compose.runtime.Composable
import kobweb.compose.ui.fillMaxSize
import kobweb.silk.theme.SilkTheme
import kobweb.silk.theme.shapes.Shape
import kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = SilkTheme.colors.getActivePalette().bg,
    shape: Shape = SilkTheme.shapes.large,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .background(color)
            .clip(shape),
        content
    )
}
