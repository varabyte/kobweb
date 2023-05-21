package com.varabyte.kobweb.silk.components.navigation

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.colors.ColorVar
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.dom.A

// Note: The Silk `Link` widget itself is defined in the kobweb-silk module since it has dependencies on kobweb-core
// However, the styles are defined here, since this module is responsible for registering them, and it can still be
// useful to use them even without Kobweb.

val LinkDefaultColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val LinkVisitedColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

/**
 * Style to use with [A] tags to give them Silk-themed colors.
 */
val LinkStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier.textDecorationLine(TextDecorationLine.None)
    }

    link {
        Modifier.color(LinkDefaultColorVar.value())
    }
    visited {
        Modifier.color(LinkVisitedColorVar.value())
    }
    hover {
        Modifier.textDecorationLine(TextDecorationLine.Underline)
    }
}

val UndecoratedLinkVariant by LinkStyle.addVariant {
    hover {
        Modifier.textDecorationLine(TextDecorationLine.None)
    }
}

val UncoloredLinkVariant by LinkStyle.addVariant {
    link { Modifier.color(ColorVar.value()) }
    visited { Modifier.color(ColorVar.value()) }
}
