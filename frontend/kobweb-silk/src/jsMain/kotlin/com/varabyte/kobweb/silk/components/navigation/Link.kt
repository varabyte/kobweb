package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.clickable
import com.varabyte.kobweb.compose.ui.color
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.navigation.Router
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentStyle
import com.varabyte.kobweb.silk.components.ComponentVariant
import com.varabyte.kobweb.silk.components.CursorState
import com.varabyte.kobweb.silk.components.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

interface LinkStyle : ComponentStyle<CursorState>
class DefaultLinkStyle : LinkStyle {
    @Composable
    @ReadOnlyComposable
    override fun toModifier(state: CursorState): Modifier {
        return Modifier.color(SilkTheme.palette.secondary).styleModifier {
            textDecorationLine(TextDecorationLine.Underline)
            cursor(Cursor.Pointer)
        }
    }
}

object LinkKey : ComponentKey<LinkStyle>
interface LinkVariant : ComponentVariant<CursorState, LinkStyle>

object UndecoratedLinkVariant : LinkVariant {
    override val style: LinkStyle = object : LinkStyle {
        @Composable
        @ReadOnlyComposable
        override fun toModifier(state: CursorState): Modifier {
            return Modifier.styleModifier {
                textDecorationLine(TextDecorationLine.None)
            }
        }
    }
}

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
    variant: LinkVariant? = null,
) {
    A(
        href = path,
        attrs = SilkTheme.componentStyles[LinkKey].toModifier(CursorState.DEFAULT, variant)
            .clickable { evt ->
                evt.preventDefault()
                Router.navigateTo(path)
            }
            .then(modifier)
            .asAttributeBuilder()
    ) {
        Text(text)
    }
}