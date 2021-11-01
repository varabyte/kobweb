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
import com.varabyte.kobweb.silk.components.ComponentModifier
import com.varabyte.kobweb.silk.components.then
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

val LinkKey = ComponentKey("silk-link")

object DefaultLinkModifier : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        return Modifier.color(SilkTheme.palette.secondary)
    }
}

object UndecoratedLinkVariant : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        return Modifier.styleModifier {
            textDecorationLine(TextDecorationLine.None)
        }
    }
}

/** A link which shows an underline only when the cursor is hovering over it. */
object UnderCursorLinkVariant : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        var isHovering by remember { mutableStateOf(false) }

        val modifier = Modifier
            .onMouseEnter {
                isHovering = true
            }
            .onMouseLeave {
                isHovering = false
            }

        return if (!isHovering) {
            modifier.styleModifier { textDecorationLine(TextDecorationLine.None) }
        } else modifier
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
    variant: ComponentModifier? = null
) {
    A(
        href = path,
        attrs = SilkTheme.componentModifiers[LinkKey].then(variant).toModifier(null)
            .then(modifier)
            .clickable { evt ->
                evt.preventDefault()
                Router.navigateTo(path)
            }
            .asAttributeBuilder()
    ) {
        Text(text ?: path)
    }
}