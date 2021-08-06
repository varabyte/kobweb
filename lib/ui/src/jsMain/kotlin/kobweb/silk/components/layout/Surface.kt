package kobweb.silk.components.layout

import androidx.compose.runtime.Composable
import kobweb.compose.css.Cursor
import kobweb.compose.css.TextDecorationLine
import kobweb.compose.css.cursor
import kobweb.compose.css.textDecorationLine
import kobweb.compose.ui.color
import kobweb.compose.ui.fillMaxSize
import kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.asAttributeBuilderApplier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * An area which provides a SilkTheme-aware background color.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = SilkTheme.colors.getActivePalette().bg,
    content: @Composable () -> Unit
) {
    Box(modifier.background(color), content)
}
