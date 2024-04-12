package com.varabyte.kobweb.silk.init

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.animation.KeyframesBuilder
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.StyleModifiers
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import org.jetbrains.compose.web.css.*

/**
 * Access to useful methods that can append CSS styles and keyframes to the global stylesheet provided by Silk.
 *
 * You can use this as a replacement for defining your own stylesheet using Compose HTML. In addition to being fewer
 * lines of code, this provides an API that lets you work with [Modifier]s for providing styles.
 */
interface SilkStylesheet {
    /**
     * An alternate way to register global styles with Silk instead of using a Compose HTML StyleSheet directly.
     *
     * So this:
     *
     * ```
     * @InitSilk
     * fun initStyles(ctx: InitSilkContext) {
     *   ctx.stylesheet.registerStyle("*") {
     *     base {
     *       Modifier.fontSize(48.px)
     *     }
     *     Breakpoint.MD {
     *       ...
     *     }
     *   }
     * }
     * ```
     *
     * is a replacement for all of this:
     *
     * ```
     * object MyStyleSheet : StyleSheet() {
     *   init {
     *     "*" style {
     *       fontSize(48.px)
     *
     *       media(mediaMinWidth(...)) {
     *         style {
     *           ...
     *         }
     *       }
     *     }
     *   }
     * }
     *
     * @App
     * @Composable
     * fun AppEntry(content: @Composable () -> Unit) {
     *   SilkApp {
     *     Style(MyStyleSheet)
     *     ...
     *   }
     * }
     * ```
     */
    fun registerStyle(cssSelector: String, extraModifiers: Modifier = Modifier, init: StyleModifiers.() -> Unit) {
        registerStyle(cssSelector, { extraModifiers }, init)
    }

    fun registerStyle(cssSelector: String, extraModifiers: @Composable () -> Modifier, init: StyleModifiers.() -> Unit)

    /**
     * An alternate way to register keyframes via Silk instead of using a Compose HTML StyleSheet directly.
     *
     * So this:
     *
     * ```
     * @InitSilk
     * fun initStyles(ctx: InitSilkContext) {
     *   ctx.stylesheet.registerKeyframes("bounce") {
     *     from { Modifier.translateX((-50).percent) }
     *     to { Modifier.translateX(50.percent) }
     *   }
     * }
     * ```
     *
     * is a replacement for:
     *
     * ```
     * object MyStyleSheet : StyleSheet() {
     *   val pulse by keyframes {
     *     from { property("transform", "translateX(-50%)") }
     *     to { property("transform", "translateX(50%)") }
     *   }
     * }
     *
     * @App
     * @Composable
     * fun AppEntry(content: @Composable () -> Unit) {
     *   SilkApp {
     *     Style(MyStyleSheet)
     *     ...
     *   }
     * }
     * ```
     */
    fun registerKeyframes(name: String, build: KeyframesBuilder.() -> Unit)
}

/**
 * Convenience method when you only care about registering the base method, which can help avoid a few extra lines.
 *
 * So this:
 *
 * ```
 * ctx.stylesheet.registerStyleBase("*") {
 *   Modifier.fontSize(48.px)
 * }
 * ```
 *
 * replaces this:
 *
 * ```
 * ctx.stylesheet.registerStyle("*") {
 *   base {
 *     Modifier.fontSize(48.px)
 *   }
 * }
 * ```
 */
fun SilkStylesheet.registerStyleBase(cssSelector: String, extraModifiers: Modifier = Modifier, init: () -> Modifier) {
    registerStyleBase(cssSelector, { extraModifiers }, init)
}

fun SilkStylesheet.registerStyleBase(
    cssSelector: String,
    extraModifiers: @Composable () -> Modifier,
    init: () -> Modifier
) {
    registerStyle(cssSelector, extraModifiers) {
        base {
            init()
        }
    }
}

internal object SilkStylesheetInstance : SilkStylesheet {
    private val styles = mutableListOf<ComponentStyle>()
    private val keyframes = mutableMapOf<String, KeyframesBuilder.() -> Unit>()

    override fun registerStyle(
        cssSelector: String,
        extraModifiers: @Composable () -> Modifier,
        init: StyleModifiers.() -> Unit
    ) {
        styles.add(ComponentStyle(cssSelector, extraModifiers, prefix = null, init))
    }

    override fun registerKeyframes(name: String, build: KeyframesBuilder.() -> Unit) {
        require(!keyframes.contains(name)) { "User is registering duplicate keyframe name: $name" }
        keyframes[name] = build
    }

    // This method is not part of the public API and should only be called by Silk itself at initialization time
    fun registerStylesAndKeyframesInto(siteStyleSheet: StyleSheet) {
        styles.forEach { componentStyle ->
            componentStyle.addStylesInto(siteStyleSheet, componentStyle.name)
        }

        keyframes.map { (name, build) ->
            val lightBuilder = KeyframesBuilder(ColorMode.LIGHT).apply(build)
            val darkBuilder = KeyframesBuilder(ColorMode.DARK).apply(build)

            if (lightBuilder == darkBuilder) {
                lightBuilder.addKeyframesIntoStylesheet(siteStyleSheet, name)
            } else {
                lightBuilder.addKeyframesIntoStylesheet(siteStyleSheet, name.suffixedWith(ColorMode.LIGHT))
                darkBuilder.addKeyframesIntoStylesheet(siteStyleSheet, name.suffixedWith(ColorMode.DARK))
            }
        }
    }
}
