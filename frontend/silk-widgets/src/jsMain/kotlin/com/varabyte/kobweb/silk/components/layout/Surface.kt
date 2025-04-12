package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.css.CssIdent
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.style.ColorModeStrategy
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.useScope
import com.varabyte.kobweb.silk.style.vars.color.BackgroundColorVar
import com.varabyte.kobweb.silk.style.vars.color.ColorVar
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.cssClass
import com.varabyte.kobweb.silk.theme.colors.isSuffixedWith
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import com.varabyte.kobweb.silk.theme.colors.withColorModeSuffixRemoved
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

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
    val surfaceModifier = SurfaceStyle.toModifier(variant)
        .then(modifier)
        .thenIf(colorModeOverride != null) {
            Modifier.classNames(colorModeOverride!!.cssClass)
        }

    if (colorModeOverride == null || ColorModeStrategy.current.useScope) {
        Box(
            surfaceModifier,
            contentAlignment = contentAlignment,
            ref = ref,
        ) {
            if (colorModeOverride != null) {
                CompositionLocalProvider(colorModeOverride.provide()) {
                    content()
                }
            } else {
                content()
            }
        }
    } else {
        var surfaceElement by remember { mutableStateOf<HTMLElement?>(null) }
        Box(
            surfaceModifier,
            contentAlignment = contentAlignment,
            ref = refScope {
                add(ref)
                ref { surfaceElement = it }
            },
        ) {
            surfaceElement?.let { surfaceElement ->
                // Minor hack - the passed in modifier may have color styles applied to it which came from a different
                // color mode than the one we want to use starting from this surface and below. For example:
                // ```
                // TestStyle = CssStyle.base {
                //     Modifier.backgroundColor(if (colorMode.isDark) Colors.DarkRed else Colors.Pink)
                // }
                // Surface(TestStyle.toModifier(), colorModeOverride = ColorMode.LIGHT) { ... }
                // ```
                // In the above case, the generated `TestStyle.toModifier` will be created using the color mode from
                // one layer above the surface. We handle that case here by replacing all class names associated with
                // the wrong target color mode by modifying the surface's classnames directly.
                //
                // Children of the surface don't need to do this because their parent colormode scope will be set
                // correctly.
                val parentColorMode = ColorMode.current
                LaunchedEffect(parentColorMode, colorModeOverride) {
                    surfaceElement.classList.asList().forEach { className ->
                        val ident = CssIdent(className)
                        // To be extra safe, we only replace class names that we can confirm came from Silk (and not,
                        // say, a third party JS library that happens to use a name like "bright_light" or something.)
                        if (
                            ident.isSuffixedWith(colorModeOverride.opposite) &&
                            SilkTheme.hasStyle(ident.withColorModeSuffixRemoved().asStr)
                        ) {
                            surfaceElement.classList.replace(className, ident.suffixedWith(colorModeOverride).asStr)
                        }
                    }
                }

                CompositionLocalProvider(colorModeOverride.provide()) {
                    content()
                }
            }
        }
    }
}
