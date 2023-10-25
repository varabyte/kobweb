package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.compose.util.invokeLater
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastLgStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastMdStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastSmStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastXlStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayIfAtLeastZeroStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilLgStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilMdStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilSmStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilXlStyle
import com.varabyte.kobweb.silk.components.layout.breakpoint.DisplayUntilZeroStyle
import com.varabyte.kobweb.silk.components.text.SpanTextStyle
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.css.CSSGroupingRule
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSStyleRule
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.css.get

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

fun initSilk(additionalInit: (InitSilkContext) -> Unit = {}) {
    val mutableTheme = MutableSilkTheme()
    val config = MutableSilkConfig()

    mutableTheme.registerComponentStyle(SpanTextStyle)

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
        DisplayIfAtLeastZeroStyle,
        DisplayIfAtLeastSmStyle,
        DisplayIfAtLeastMdStyle,
        DisplayIfAtLeastLgStyle,
        DisplayIfAtLeastXlStyle,
        DisplayUntilZeroStyle,
        DisplayUntilSmStyle,
        DisplayUntilMdStyle,
        DisplayUntilLgStyle,
        DisplayUntilXlStyle,
    )
    displayStyles.forEach { mutableTheme.registerComponentStyle(it) }

    MutableSilkConfigInstance = config

    _SilkTheme = ImmutableSilkTheme(mutableTheme)
    SilkStylesheetInstance.registerStylesAndKeyframesInto(SilkStyleSheet)
    SilkTheme.registerStyles(SilkStyleSheet)

    // Hack alert part 2: Here is where we run through all styles in the stylesheet and update the ones associated with
    // our display styles.
    // Note that a real solution would be if the Compose HTML APIs allowed us to identify a style as important, but
    // currently, as you can see with their code here: https://github.com/JetBrains/compose-multiplatform/blob/9e25001e9e3a6be96668e38c7f0bd222c54d1388/html/core/src/jsMain/kotlin/org/jetbrains/compose/web/elements/Style.kt#L116
    // they don't support it. (There would have to be a version of the API that takes an additional priority parameter,
    // as in `setProperty("x", "y", "important")`)
    window.invokeLater { // invokeLater gives the engine a chance to register Silk styles into the stylesheet objects
        val displayStyleSelectorNames = displayStyles.map { ".${it.name}" }.toSet()
        for (styleSheetIndex in 0..<document.styleSheets.length) {
            val styleSheet = (document.styleSheets[styleSheetIndex] as? CSSStyleSheet)
                // Trying to peek at external stylesheets causes a security exception so step over them
                ?.takeIf { styleSheet -> styleSheet.href == null }
                ?: continue
            for (ruleIndex in 0..<styleSheet.cssRules.length) {
                val rule = styleSheet.cssRules[ruleIndex] as? CSSGroupingRule ?: continue
                // Note: We know all display rules are media rules, but if we ever want to support
                // "important" more generally, we'd have to handle at least STYLE_RULE as well
                if (rule.type != CSSRule.MEDIA_RULE)
                    continue
                for (innerRuleIndex in 0..<rule.cssRules.length) {
                    val innerRule = rule.cssRules[innerRuleIndex] as? CSSStyleRule ?: continue
                    val selectorText = innerRule.selectorText
                    val innerStyle = innerRule.style
                    if (selectorText in displayStyleSelectorNames) {
                        val displayValue = innerStyle.getPropertyValue("display")
                        innerStyle.setProperty("display", displayValue, "important")
                    }
                }
            }
        }
    }
}
