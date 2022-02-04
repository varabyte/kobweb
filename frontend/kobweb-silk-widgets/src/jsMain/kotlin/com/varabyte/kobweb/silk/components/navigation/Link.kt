package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.link
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.visited
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

val LinkStyle = ComponentStyle("silk-link") {
    base {
        Modifier.textDecorationLine(TextDecorationLine.None)
    }

    val linkColors = colorMode.toSilkPalette().link
    link {
        Modifier.color(linkColors.default)
    }
    visited {
        Modifier.color(linkColors.visited)
    }
    hover {
        Modifier.textDecorationLine(TextDecorationLine.Underline)
    }
}

val UndecoratedLinkVariant = LinkStyle.addVariant("undecorated") {
    hover {
        Modifier.textDecorationLine(TextDecorationLine.None)
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
    variant: ComponentVariant? = null
) {
    Link(path, modifier, variant) {
        Text(text ?: path)
    }
}

/**
 * Linkable content which, when clicked, navigates to the target [path].
 */
@Composable
fun Link(
    path: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    content: @Composable () -> Unit = {}
) {
    A(
        path,
        attrs = LinkStyle.toModifier(variant).then(modifier).asAttributeBuilder()
    ) {
        content()
    }
}