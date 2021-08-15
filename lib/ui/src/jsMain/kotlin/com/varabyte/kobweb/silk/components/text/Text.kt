package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.color
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.SilkPallete
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Div
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
    color: Color = SilkPallete.current.onPrimary,
    decorationLine: TextDecorationLine? = null,
    cursor: Cursor? = null,
) {
    Div(
        attrs = modifier
            .color(color)
            .asAttributeBuilder {
                if (decorationLine != null) {
                    style { textDecorationLine(decorationLine) }
                }
                if (cursor != null) {
                    style {
                        cursor(cursor)
                        if (cursor != Cursor.Text) {
                            userSelect(UserSelect.None)
                        }
                    }
                }
                if (text.startsWith(' ') || text.endsWith(' ')) {
                    style {
                        // Prevent spaces in text from being collapsed
                        whiteSpace("pre-wrap")
                    }
                }
            }
    ) {
        Text(text)
    }
}
