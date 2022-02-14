package com.varabyte.kobweb.silk.components.navigation

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.link
import com.varabyte.kobweb.silk.components.style.visited
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.dom.A

// Note: The Silk `Link` widget itself is defined in the kobweb-silk module since it has dependencies on kobweb-core
// However, the styles are defined here, since this module is responsible for registering them, and it can still be
// useful to use them even without Kobweb.

/**
 * Style to use with [A] tags to give them Silk-themed colors.
 */
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

val UncoloredLinkVariant = LinkStyle.addVariant("uncolored") {
    val textColor = colorMode.toSilkPalette().color
    link { Modifier.color(textColor) }
    visited { Modifier.color(textColor) }
}
