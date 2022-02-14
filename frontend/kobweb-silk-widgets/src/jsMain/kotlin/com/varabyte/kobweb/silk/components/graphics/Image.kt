package com.varabyte.kobweb.silk.components.graphics

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.addBaseVariant
import org.jetbrains.compose.web.css.*

// Note: The Silk `Image` widget itself is defined in the kobweb-silk module since it has dependencies on kobweb-core
// However, the styles are defined here, since this module is responsible for registering them, and it can still be
// useful to use them even without Kobweb.

val ImageStyle = ComponentStyle("silk-image") {}

val FitWidthImageVariant = ImageStyle.addBaseVariant("fit") {
    Modifier
        .styleModifier {
            property("width", 100.percent)
            property("object-fit", "scale-down")
        }
}
