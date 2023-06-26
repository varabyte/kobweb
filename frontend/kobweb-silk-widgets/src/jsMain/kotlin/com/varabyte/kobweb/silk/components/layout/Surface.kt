package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

val SurfaceBackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val SurfaceColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

val SurfaceStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier
            .backgroundColor(SurfaceBackgroundColorVar.value())
            .color(SurfaceColorVar.value())
    }
}

/**
 * A variant which provides a smoother color animation effect.
 *
 * Without applying this variant, colors will snap instantly between dark and light colors. With applying it, the
 * colors will transition smoothly. This variant is not applied by default, however, because sometimes children sections
 * don't themselves animate, causing a strange
 */
@Deprecated(
    "Use `SmoothColorStyle` instead, e.g. `Surface(SmoothColorStyle.toModifer())`. The approach used by `AnimatedColorSurfaceVariant` is problematic and conflicts with people trying to set transitions on their own elements."
)
val AnimatedColorSurfaceVariant by SurfaceStyle.addVariant {
    val backgroundColorTransition = Modifier.transition(CSSTransition("background-color", 200.ms))

    base { backgroundColorTransition }
    // It looks weird if parts of the screen snap colors while others transition smoothly. Do our best to make sure all
    // container elements transition as well.
    cssRule(" div") { backgroundColorTransition }
}

/**
 * A panel which encapsulates a SilkTheme-aware area.
 *
 * This should probably be somewhere near the root of your app as it defines colors that cascade down through its
 * children with Silk colors.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentAlignment: Alignment = Alignment.TopStart,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        SurfaceStyle.toModifier(variant).then(modifier),
        contentAlignment = contentAlignment,
        ref = ref,
    ) {
        content()
    }
}
