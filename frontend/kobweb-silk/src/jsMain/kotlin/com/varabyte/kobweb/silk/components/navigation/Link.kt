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
import com.varabyte.kobweb.core.rememberPageContext
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
        var isHovering by remember { mutableStateOf(false) }

        val modifier = Modifier
            .color(SilkTheme.palette.secondary)
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

object UndecoratedLinkVariant : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        return Modifier.styleModifier {
            textDecorationLine(TextDecorationLine.None)
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
    variant: ComponentModifier? = null
) {
    val ctx = rememberPageContext()

    A(
        href = path,
        attrs = SilkTheme.componentModifiers[LinkKey].then(variant).toModifier(null)
            .then(modifier)
            .clickable { evt ->
                evt.preventDefault()
                ctx.router.navigateTo(path)
            }
            .asAttributeBuilder()
    ) {
        Text(text ?: path)
    }
}