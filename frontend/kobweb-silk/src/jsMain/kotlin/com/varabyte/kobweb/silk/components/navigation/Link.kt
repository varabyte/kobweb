package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.link
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.visited
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.dom.Text
import com.varabyte.kobweb.navigation.Link as KobwebLink

val LinkStyle = ComponentStyle("silk-link") { colorMode ->
    base = Modifier.styleModifier { textDecorationLine(TextDecorationLine.None) }

    link = Modifier.color(SilkTheme.palettes[colorMode].link.default)
    visited = Modifier.color(SilkTheme.palettes[colorMode].link.visited)

    hover = Modifier.styleModifier { textDecorationLine(TextDecorationLine.Underline) }
}

val UndecoratedLinkVariant = LinkStyle.addVariant("undecorated") {
    hover = Modifier.styleModifier { textDecorationLine(TextDecorationLine.None) }
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
    KobwebLink(
        href = path,
        attrs = LinkStyle.toModifier(variant).then(modifier).asAttributeBuilder()
    ) {
        Text(text ?: path)
    }
}