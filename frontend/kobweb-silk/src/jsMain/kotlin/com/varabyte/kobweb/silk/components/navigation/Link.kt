package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.clickable
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.core.Router
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme

/**
 * Linkable text which, when clicked, navigates to the target [path].
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 */
@Composable
fun Link(
    path: String,
    text: String = path,
    modifier: Modifier = Modifier,
    color: Color = SilkTheme.palette.secondary,
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