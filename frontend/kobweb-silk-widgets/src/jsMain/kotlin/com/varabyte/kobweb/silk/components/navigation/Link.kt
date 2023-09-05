package com.varabyte.kobweb.silk.components.navigation

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.link
import com.varabyte.kobweb.silk.components.style.visited
import com.varabyte.kobweb.silk.theme.colors.ColorVar
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A

// Note: The Silk `Link` widget itself is defined in the kobweb-silk module since it has dependencies on kobweb-core
// However, the styles are defined here, since this module is responsible for registering them, and it can still be
// useful to use them even without Kobweb.

object LinkVars {
    val DefaultColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val VisitedColor by StyleVariable<CSSColorValue>(prefix = "silk")
}

/**
 * Style to use with [A] tags to give them Silk-themed colors.
 */
val LinkStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier.textDecorationLine(TextDecorationLine.None)
    }

    link {
        Modifier.color(LinkVars.DefaultColor.value())
    }
    visited {
        Modifier.color(LinkVars.VisitedColor.value())
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
