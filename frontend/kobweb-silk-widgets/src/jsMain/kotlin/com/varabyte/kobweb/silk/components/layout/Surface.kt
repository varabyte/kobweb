package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.init.setSilkVariables
import com.varabyte.kobweb.silk.theme.colors.BackgroundColorVar
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorVar
import com.varabyte.kobweb.silk.theme.colors.LocalColorMode
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

val SurfaceBackgroundColorVar by StyleVariable(prefix = "silk", defaultFallback = BackgroundColorVar.value())
val SurfaceColorVar by StyleVariable(prefix = "silk", defaultFallback = ColorVar.value())

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
 * This widget is similar to a Box except it also responsible for setting the site's color look and feel.
 *
 * You can also explicitly pass in a color mode, which, if set, will override the value for all of its children. In this
 * way, you can nest child surfaces if you want some areas to have overridden color modes, which can be useful
 * for things like areas that are always light or always dark regardless of the site's overall theme. See also
 * [ColorMode.current] (for reading the color mode) and [ColorMode.currentState] if you need to change it.
 *
 * All Silk apps expect to have a root Surface at or near the top of their layout.
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    colorModeOverride: ColorMode? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var surfaceElement by remember { mutableStateOf<HTMLElement?>(null)}
    Box(
        SurfaceStyle.toModifier(variant).then(modifier),
        contentAlignment = contentAlignment,
        ref = refScope {
            add(ref)
            ref { surfaceElement = it }
        },
    ) {
        if (colorModeOverride != null) {
            surfaceElement?.let { surfaceElement ->
                CompositionLocalProvider(LocalColorMode provides mutableStateOf(colorModeOverride)) {
                    val currColorMode = ColorMode.current // Can recompose if child changes ColorMode.currentState
                    LaunchedEffect(currColorMode) { surfaceElement.setSilkVariables(currColorMode) }
                    content()
                }
            }
        } else {
            content()
        }
    }
}
