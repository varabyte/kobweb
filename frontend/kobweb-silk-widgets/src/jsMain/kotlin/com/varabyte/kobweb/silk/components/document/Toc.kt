package com.varabyte.kobweb.silk.components.document

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.theme.colors.BorderColorVar
import org.jetbrains.compose.web.css.*

// Note: The Silk `Toc` widget itself is defined in the kobweb-silk module since it has dependencies on kobweb-core
// indirectly, via `Link`.
// However, the styles are defined here, since this module is responsible for registering them, and it can still be
// useful to use them even without Kobweb.

object TocVars {
    val BorderColor by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
}

val TocStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .listStyle(ListStyleType.None)
        .textAlign(TextAlign.Start)
        .padding(0.cssRem) // Clear default UL padding
}

val TocBorderedVariant by TocStyle.addVariantBase {
    Modifier
        .borderRadius(5.px)
        .border(1.px, LineStyle.Solid, TocVars.BorderColor.value())
        .padding(1.cssRem)
}
