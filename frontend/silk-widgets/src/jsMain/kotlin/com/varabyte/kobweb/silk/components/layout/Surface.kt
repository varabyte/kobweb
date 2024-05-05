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
import com.varabyte.kobweb.silk.components.style.vars.color.BackgroundColorVar
import com.varabyte.kobweb.silk.components.style.vars.color.ColorVar
import com.varabyte.kobweb.silk.init.setSilkWidgetVariables
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.w3c.dom.HTMLElement

object SurfaceVars {
    val BackgroundColor by StyleVariable(prefix = "silk", defaultFallback = BackgroundColorVar.value())
    val Color by StyleVariable(prefix = "silk", defaultFallback = ColorVar.value())
}

sealed interface SurfaceKind : ComponentKind

val SurfaceStyle = CssStyle<SurfaceKind> {
    base {
        Modifier
            .backgroundColor(SurfaceVars.BackgroundColor.value())
            .color(SurfaceVars.Color.value())
    }
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
    variant: CssStyleVariant<SurfaceKind>? = null,
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
                CompositionLocalProvider(colorModeOverride.provide()) {
                    val currColorMode = ColorMode.current // Can recompose if child changes ColorMode.currentState
                    LaunchedEffect(currColorMode) { surfaceElement.setSilkWidgetVariables(currColorMode) }
                    content()
                }
            }
        } else {
            content()
        }
    }
}
