package kobweb.silk.components.navigation

import androidx.compose.runtime.Composable
import kobweb.compose.css.Cursor
import kobweb.compose.css.TextDecorationLine
import kobweb.compose.ui.graphics.Color
import kobweb.core.Router
import kobweb.silk.components.text.Text
import kobweb.silk.theme.SilkPallete
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.ui.Modifier

/**
 * Linkable text which, when clicked, navigates to the target [path].
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 */
@Composable
fun Link(
    path: String,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SilkPallete.current.secondary,
    decorationLine: TextDecorationLine? = TextDecorationLine.Underline,
    cursor: Cursor? = Cursor.Pointer,
) {
    Text(
        text,
        modifier.clickable { Router.navigateTo(path) },
        color,
        decorationLine,
        cursor
    )
}
