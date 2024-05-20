package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.browser.dom.css.CSSLayerBlockRule
import com.varabyte.kobweb.browser.util.invokeLater
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayIfAtLeastLgStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayIfAtLeastMdStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayIfAtLeastSmStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayIfAtLeastXlStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayIfAtLeastZeroStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayUntilLgStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayUntilMdStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayUntilSmStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayUntilXlStyle
import com.varabyte.kobweb.silk.style.breakpoint.DisplayUntilZeroStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.asList
import org.w3c.dom.css.CSSMediaRule
import org.w3c.dom.css.CSSStyleRule
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

// This is provided as a way to pass silk initialization down to the `prepareSilkFoundation` method if it
// is otherwise buried within an opaque API. If a user is using `silk-widgets` directly, they will likely set
// initialization directly there. In the case of Kobweb projects, where code gets automatically processed at compile
// time looking for `@InitSilk` methods, it is easier to generate code and then set it using this property.
var additionalSilkInitialization: (InitSilkContext) -> Unit = {}

// Precedence for styles, from lowest to highest:
// component styles < component variants < restricted styles < general styles
//
// Imagine code like this:
// ```
// interface WidgetKind
// val WidgetStyle = CssStyle<WidgetKind> { ... }
// class SomeParam(...) : CssStyle.Restricted(...)
// fun Widget(modifier: Modifier, variant: CssStyleVariant<WidgetKind>, someParam: SomeParam) {
//   val finalModifier = WidgetStyle.toModifier(variant)
//      .then(someParam.toModifier())
//      .then(modifier)
// }
// ```
//
// Called like this:
// ```
// val MyStyle = CssStyle { ... }
// val MyWidgetVariant = WidgetStyle.addVariant { ... }
// Widget(MyStyle.toModifier(), MyWidgetVariant, SomeParam.Value)
// ```
// Here, we would expect any variant to override the style, any parameter to override the variant, and any
// user style passed into the modifier value to override everything else.
//
// The reset layer is reserved for global default styles (like changing the box-sizing property to border-box).
//
// The base layer is intended for global styles, often defined in `@InitSilk` blocks with code like
// `ctx.stylesheet.registerStyle("a") { ... }` which apply globally to the whole site but which should get overridden if
// any other style also tries to set it.
internal object SilkCssLayerNames {
    const val RESET = "reset"
    const val BASE = "base"
    const val COMPONENT_STYLES = "component-styles"
    const val COMPONENT_VARIANTS = "component-variants"
    const val RESTRICTED_STYLES = "restricted-styles"
    const val GENERAL_STYLES = "general-styles"
}

fun initSilk(additionalInit: (InitSilkContext) -> Unit = {}) {
    val mutableTheme = MutableSilkTheme()
    val config = MutableSilkConfig()

    mutableTheme.registerStyle("silk-span-text", SpanTextStyle)

    val ctx = InitSilkContext(config, SilkStylesheetInstance, mutableTheme)
    additionalInit(ctx)
    additionalSilkInitialization(ctx)

    // Hack alert: Compose HTML does NOT support setting the !important flag on styles, which is in general a good thing
    // However, we really want to make an exception for display styles, because if someone uses a method like
    // "displayIfAtLeast(MD)" then we want the display to really be none even if inline styles are present.
    // Without !important, this code would not work, which isn't expected:
    //
    // Div(
    //   Modifier
    //     .displayIfAtLeast(MD)
    //     .grid { row(1.fr); column(1.fr) }
    // )
    //
    // `grid` sets the display type to "grid", which overrides the `display: none` from `displayIfAtLeast`
    // See below for where we find these styles and update them to use !important.
    val displayStyles = listOf(
        DisplayIfAtLeastZeroStyle to "silk-display-if-at-least-zero",
        DisplayIfAtLeastSmStyle to "silk-display-if-at-least-sm",
        DisplayIfAtLeastMdStyle to "silk-display-if-at-least-md",
        DisplayIfAtLeastLgStyle to "silk-display-if-at-least-lg",
        DisplayIfAtLeastXlStyle to "silk-display-if-at-least-xl",
        DisplayUntilZeroStyle to "silk-display-until-zero",
        DisplayUntilSmStyle to "silk-display-until-sm",
        DisplayUntilMdStyle to "silk-display-until-md",
        DisplayUntilLgStyle to "silk-display-until-lg",
        DisplayUntilXlStyle to "silk-display-until-xl",
    )

    displayStyles.forEach { (style, name) ->
        mutableTheme.registerStyle(name, style)
    }

    MutableSilkConfigInstance = config

    _SilkTheme = ImmutableSilkTheme(mutableTheme)
    SilkTheme.registerKeyframesInto(SilkStylesheetInstance)
    SilkStylesheetInstance.registerStylesAndKeyframesInto(SilkStyleSheet)
    SilkTheme.registerStylesInto(SilkStyleSheet)

    window.invokeLater { // invokeLater gives the engine time to register Silk styles into the stylesheet objects first
        run {
            // Run through all styles in the stylesheet and update the ones associated with our display styles, making
            // them important. This means that responsive designs will always work -- that another style that sets the
            // `display` property will never accidentally overrule it.
            // Note that a real solution would be if the Compose HTML APIs allowed us to identify a style as important,
            // but currently, as you can see with their code here:
            // https://github.com/JetBrains/compose-multiplatform/blob/9e25001e9e3a6be96668e38c7f0bd222c54d1388/html/core/src/jsMain/kotlin/org/jetbrains/compose/web/elements/Style.kt#L116
            // they don't support it. (It would have been nice to be a version of the API that takes an additional
            // priority parameter, as in `setProperty("x", "y", "important")`)
            val displayStyleSelectorNames = displayStyles.map { (_, name) -> ".${name}" }.toSet()
            document.styleSheets.asList()
                .filterIsInstance<CSSStyleSheet>()
                // Trying to peek at external stylesheets causes a security exception so step over them
                .filter { it.href == null }
                .flatMap { styleSheet ->
                    // Note: We know all display styles use media rules & layers blocks, but if we ever want to support
                    // "important" more generally, we'd have to handle at least rules at all levels.
                    styleSheet.cssRules.asList()
                        .filterIsInstance<CSSMediaRule>()
                        .flatMap { it.cssRules.asList() }
                        .mapNotNull { rule ->
                            (rule as? CSSLayerBlockRule)?.takeIf { it.name == SilkCssLayerNames.GENERAL_STYLES }
                        }.flatMap { it.cssRules.asList().filterIsInstance<CSSStyleRule>() }
                }.forEach { rule ->
                    val selectorText = rule.selectorText
                    val innerStyle = rule.style
                    if (selectorText in displayStyleSelectorNames) {
                        val displayValue = innerStyle.getPropertyValue("display")
                        innerStyle.setProperty("display", displayValue, "important")
                    }
                }
        }

        document.styleSheets.asList()
            .filterIsInstance<CSSStyleSheet>()
            // Trying to peek at external stylesheets causes a security exception so step over them
            .filter { it.href == null }
            .forEach { styleSheet ->
                val cssLayers = (listOf(
                    SilkCssLayerNames.RESET,
                    SilkCssLayerNames.BASE,
                    SilkCssLayerNames.COMPONENT_STYLES,
                    SilkCssLayerNames.COMPONENT_VARIANTS,
                    SilkCssLayerNames.RESTRICTED_STYLES,
                    SilkCssLayerNames.GENERAL_STYLES
                ) + SilkStylesheetInstance.cssLayers)
                styleSheet.insertRule("@layer ${cssLayers.joinToString()};", 0)
            }
    }
}
