package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.clickable
import com.varabyte.kobweb.compose.ui.color
import com.varabyte.kobweb.compose.ui.onMouseEnter
import com.varabyte.kobweb.compose.ui.onMouseLeave
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.navigation.Router
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentState
import com.varabyte.kobweb.silk.components.ComponentStyle
import com.varabyte.kobweb.silk.components.ComponentVariant
import com.varabyte.kobweb.silk.components.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

enum class LinkState : ComponentState {
    /** The cursor is not over the component */
    DEFAULT,
    /** The cursor is over the component */
    HOVER,
}

interface LinkStyle : ComponentStyle<LinkState>
class DefaultLinkStyle : LinkStyle {
    @Composable
    @ReadOnlyComposable
    override fun toModifier(state: LinkState): Modifier {
        return Modifier.color(SilkTheme.palette.secondary)
    }
}

object LinkKey : ComponentKey<LinkStyle>
interface LinkVariant : ComponentVariant<LinkState, LinkStyle>

/** A link which doesn't show any underline at all. */
object UndecoratedLinkVariant : LinkVariant {
    override val style: LinkStyle = object : LinkStyle {
        @Composable
        @ReadOnlyComposable
        override fun toModifier(state: LinkState): Modifier {
            return Modifier.styleModifier {
                textDecorationLine(TextDecorationLine.None)
            }
        }
    }
}

/** A link which shows an underline only when the cursor is hovering over it. */
object UnderCursorLinkVariant : LinkVariant {
    override val style: LinkStyle = object : LinkStyle {
        @Composable
        @ReadOnlyComposable
        override fun toModifier(state: LinkState): Modifier {
            return if (state == LinkState.DEFAULT) {
                Modifier.styleModifier { textDecorationLine(TextDecorationLine.None) }
            } else Modifier
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
    text: String? = null,
    modifier: Modifier = Modifier,
    variant: LinkVariant? = null,
) {
    var state by remember { mutableStateOf(LinkState.DEFAULT) }
    A(
        href = path,
        attrs = SilkTheme.componentStyles[LinkKey].toModifier(state, variant)
            .then(modifier)
            .clickable { evt ->
                evt.preventDefault()
                Router.navigateTo(path)
            }
            .onMouseEnter {
                state = LinkState.HOVER
            }
            .onMouseLeave {
                state = LinkState.DEFAULT
            }
            .asAttributeBuilder()
    ) {
        Text(text ?: path)
    }
}