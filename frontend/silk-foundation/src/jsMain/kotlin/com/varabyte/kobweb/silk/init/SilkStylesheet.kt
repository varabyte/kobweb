package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.SimpleCssStyle
import com.varabyte.kobweb.silk.style.StyleScope
import com.varabyte.kobweb.silk.style.animation.KeyframesBuilder
import com.varabyte.kobweb.silk.style.assertNoAttributes
import com.varabyte.kobweb.silk.style.layer.LayerListBuilder
import com.varabyte.kobweb.silk.style.layer.SilkLayer
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.suffixedWith
import org.jetbrains.compose.web.css.*

interface CssStyleRegistrar {
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
    fun registerStyle(cssSelector: String, init: StyleScope.() -> Unit)
}

/**
 * Access to useful methods that can append CSS styles and keyframes to the global stylesheet provided by Silk.
 *
 * You can use this as a replacement for defining your own stylesheet using Compose HTML. In addition to being fewer
 * lines of code, this provides an API that lets you work with [Modifier]s for providing styles.
 */
interface SilkStylesheet : CssStyleRegistrar {
    /**
     * Users can specify custom CSS layers here, to extend the initial set provided by Silk.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/@layer">the official @layer docs</a>
     *
     * @see SilkLayer
     */
    val cssLayers: LayerListBuilder

    /**
     * Create a layer which then wraps a collection of CSS styles.
     */
    fun layer(name: String, block: CssStyleRegistrar.() -> Unit)
    fun registerKeyframes(name: String, build: KeyframesBuilder.() -> Unit)
}

fun SilkStylesheet.layer(layer: SilkLayer, block: CssStyleRegistrar.() -> Unit) = layer(layer.layerName, block)

private class CssStyleRegistrarImpl : CssStyleRegistrar {
    class Entry(val cssSelector: String, val init: StyleScope.() -> Unit)

    private val _entries = mutableListOf<Entry>()
    val entries: List<Entry> = _entries

    override fun registerStyle(
        cssSelector: String,
        init: StyleScope.() -> Unit
    ) {
        _entries.add(Entry(cssSelector, init))
    }
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
fun CssStyleRegistrar.registerStyleBase(
    cssSelector: String,
    init: () -> Modifier
) {
    registerStyle(cssSelector) {
        base {
            init()
        }
    }
}

internal object SilkStylesheetInstance : SilkStylesheet {
    private val styles = mutableListOf<SimpleCssStyle>()
    private val keyframes = mutableMapOf<String, KeyframesBuilder.() -> Unit>()

    override val cssLayers = LayerListBuilder()

    private fun (StyleScope.() -> Unit).assertNoAttributeModifiers(selectorName: String) {
        val simpleStyleScope = object : StyleScope() {}
        this.invoke(simpleStyleScope)

        simpleStyleScope.cssModifiers.forEach { cssModifier ->
            cssModifier.assertNoAttributes(
                selectorName,
                extraContext = "Please search your `@InitSilk` code for a line like `ctx.stylesheet.registerStyle(\"$selectorName\")` and remove the offending attribute(s)."
            )
        }
    }

    override fun registerStyle(
        cssSelector: String,
        init: StyleScope.() -> Unit
    ) {
        init.assertNoAttributeModifiers(cssSelector)
        styles.add(SimpleCssStyle(cssSelector, init, { Modifier }, layer = null))
    }

    override fun layer(name: String, block: CssStyleRegistrar.() -> Unit) {
        val styleEntries = CssStyleRegistrarImpl().apply(block).entries
        styleEntries.forEach { entry ->
            entry.init.assertNoAttributeModifiers(entry.cssSelector)
            styles.add(
                SimpleCssStyle(
                    entry.cssSelector,
                    entry.init,
                    { Modifier },
                    layer = name.takeIf { it.isNotEmpty() }
                )
            )
        }
    }

    override fun registerKeyframes(name: String, build: KeyframesBuilder.() -> Unit) {
        require(!keyframes.contains(name)) { "User is registering duplicate keyframe name: $name" }
        keyframes[name] = build
    }

    // This method is not part of the public API and should only be called by Silk itself at initialization time
    fun registerStylesAndKeyframesInto(siteStyleSheet: StyleSheet) {
        styles.forEach { cssStyle -> cssStyle.addStylesInto(siteStyleSheet) }

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
