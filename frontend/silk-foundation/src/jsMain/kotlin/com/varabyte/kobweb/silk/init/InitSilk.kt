package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.browser.dom.css.CSSLayerBlockRule
import com.varabyte.kobweb.browser.util.invokeLater
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.asList
import org.w3c.dom.css.CSSStyleSheet

/**
 * An annotation which identifies a function as one which will be called when the page opens, before DOM nodes are
 * composed. The function should take an [InitSilkContext] as its only parameter.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class InitSilk

/**
 * Various classes passed to the user in a method annotated by `@InitSilk` which they can use to for initializing Silk
 * values.
 *
 * @param config A handful of settings which will be used for configuring Silk behavior at startup time.
 * @param stylesheet A handful of methods for registering styles, etc., against Silk's provided stylesheet.
 * @param theme A version of [SilkTheme] that is still mutable (before it has been frozen, essentially, at startup).
 *   Use this if you need to modify site global colors, shapes, typography, and/or styles.
 */
class InitSilkContext(val config: MutableSilkConfig, val stylesheet: SilkStylesheet, val theme: MutableSilkTheme)

// This is provided as a way to pass silk initialization down to the `SilkFoundationStyles` method if it
// is otherwise buried within an opaque API. If a user is using `silk-widgets` directly, they will likely set
// initialization directly there. In the case of Kobweb projects, where code gets automatically processed at compile
// time looking for `@InitSilk` methods, it is easier to generate code and then set it using this property.
var additionalSilkInitialization: (InitSilkContext) -> Unit = {}

// For iterating over stylesheets that we have created / populated locally
// This excludes stylesheets imported from external locations (i.e. from inside a <head> block)
private val Document.localStyleSheets: List<CSSStyleSheet>
    get() {
        return this.styleSheets.asList()
            .filterIsInstance<CSSStyleSheet>()
            // Trying to peek at external stylesheets causes a security exception so step over them
            .filter { it.href == null }
    }


fun initSilk(additionalInit: (InitSilkContext) -> Unit = {}) {
    val mutableTheme = MutableSilkTheme()
    val config = MutableSilkConfig()

    mutableTheme.registerStyle("silk-span-text", SpanTextStyle)

    val ctx = InitSilkContext(config, SilkStylesheetInstance, mutableTheme)
    additionalInit(ctx)
    additionalSilkInitialization(ctx)

    MutableSilkConfigInstance = config

    _SilkTheme = ImmutableSilkTheme(mutableTheme)
    SilkTheme.registerKeyframesInto(SilkStylesheetInstance)
    SilkStylesheetInstance.registerStylesAndKeyframesInto(SilkStyleSheet)
    SilkTheme.registerStylesInto(SilkStyleSheet)

    window.invokeLater { // invokeLater gives the engine time to register Silk styles into the stylesheet objects first
        run {
            // Warn if we detect layers that the user referenced without registering
            val registeredCssLayers = SilkStylesheetInstance.cssLayers.build().toSet()
            val referencedCssLayers = document.localStyleSheets.asSequence()
                .flatMap { it.cssRules.asList().asSequence() }
                .filterIsInstance<CSSLayerBlockRule>()
                .map { it.name }
                .toSet()

            val unregisteredLayers = referencedCssLayers.subtract(registeredCssLayers)

            if (unregisteredLayers.isNotEmpty()) {
                console.warn(
                    """
                        One or more CSS layer(s) were referenced in code but not registered.
                        
                        Please add initialization to your project like:
                        ```
                        @InitSilk
                        fun initSilk(ctx: InitSilkContext) {
                           ctx.stylesheet.cssLayers.add(${unregisteredLayers.sorted().joinToString { "\"$it\"" }})
                        }
                        ```
                        (but change the order of the layers to match your desired priority).
                        
                        If you are not the developer of this website, consider reporting this message to them.
                    """.trimIndent()
                )
            }
        }

        document.localStyleSheets.forEach { styleSheet ->
            val cssLayers = SilkStylesheetInstance.cssLayers.build()
            styleSheet.insertRule("@layer ${cssLayers.joinToString()};", 0)
        }
    }
}
