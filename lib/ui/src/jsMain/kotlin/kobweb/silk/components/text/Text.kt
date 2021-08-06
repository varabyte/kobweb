package kobweb.silk.components.text

import androidx.compose.runtime.Composable
import kobweb.compose.css.Cursor
import kobweb.compose.css.TextDecorationLine
import kobweb.compose.css.cursor
import kobweb.compose.css.textDecorationLine
import kobweb.compose.ui.color
import kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.asAttributeBuilderApplier
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * A span of text.
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SilkTheme.colors.getActivePalette().fg,
    decorationLine: TextDecorationLine? = null,
    cursor: Cursor? = null,
) {
    Span(
        attrs = modifier
            .color(color)
            .asAttributeBuilderApplier {
                if (decorationLine != null) {
                    style { textDecorationLine(decorationLine) }
                }
                if (cursor != null) {
                    style { cursor(cursor) }
                }
                style {
                    // Prevent spaces in text from being collapsed
                    whiteSpace("pre-wrap")
                }
            }
    ) {
        Text(text)
    }
}
