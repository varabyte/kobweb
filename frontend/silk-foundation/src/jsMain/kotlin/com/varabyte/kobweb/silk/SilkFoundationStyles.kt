package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.KobwebComposeStyles
import com.varabyte.kobweb.compose.dom.GenericTag
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.style.breakpoint.SilkBreakpointDisplayStyles
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.stringPresentation
import org.jetbrains.compose.web.internal.runtime.ComposeWebInternalApi
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSStyleSheet

/**
 * Handle initialization so that the rest of your application can use Silk widgets.
 *
 * NOTE: If you are calling this method manually yourself (that is, you're not using `SilkApp` which handles this for
 * you), you may also want to call [KobwebComposeStyles] to enable support for compose-ish widgets like `Box`,
 * `Column`, `Row`, etc.
 */
@Composable
fun SilkFoundationStyles(initSilk: (InitSilkContext) -> Unit = {}) {
    key(Unit) {
        // Use (abuse?) key to run logic only first time SilkApp is called
        initSilk { ctx -> initSilk(ctx) }
    }

    CustomStyle(SilkStyleSheet)
    SilkBreakpointDisplayStyles()
}

// Workaround for the bug fixed by https://chromium-review.googlesource.com/c/chromium/src/+/6039155,
// which affects Chromium 118 (when `@scope` support is added) - Chromium 131.
// Although Chrome browsers usually auto-update, many other browsers are based on Chromium.
// At the time of writing this comment, Samsung's browser still uses Chromium 130 and is affected by this bug.
//
// Bug & workaround details:
// Compose HTML adds styles one rule at a time, effectively doing the following, which is broken until Chrome v132
// (the styles get added, but the `@scope` rule is ignored):
// ```js
// let index = sheet.insertRule('@scope (.a) {  }');
// let scope_rule = sheet.cssRules[index];
// scope_rule.insertRule('div { color: green; }');
// ```
// As a workaround, we add styles as a single complete string to avoid the bug, effectively doing:
// ```js
// sheet.insertRule('@scope (.a) { div { color: green; } }');
// ```
@Composable
private fun CustomStyle(styleSheet: CSSRulesHolder) {
    GenericTag<HTMLStyleElement>("style") {
        val cssRules = styleSheet.cssRules
        DisposableEffect(cssRules, cssRules.size) {
            val cssStylesheet = scopeElement.sheet as? CSSStyleSheet
            cssStylesheet?.setCSSRules(cssRules)
            onDispose {
                cssStylesheet?.clearCSSRules()
            }
        }
    }
}

// Based on https://github.com/JetBrains/compose-multiplatform/blob/master/html/core/src/jsMain/kotlin/org/jetbrains/compose/web/dom/Style.kt

private fun CSSStyleSheet.clearCSSRules() {
    repeat(cssRules.length) {
        deleteRule(0)
    }
}

private fun CSSStyleSheet.setCSSRules(cssRules: CSSRuleDeclarationList) {
    cssRules.forEach { cssRule ->
        addRule(cssRule.customStringPresentation())
    }
}

private fun CSSStyleSheet.addRule(cssRule: String): CSSRule? {
    val cssRuleIndex = this.insertRule(cssRule, this.cssRules.length)
    return this.cssRules.item(cssRuleIndex)
}

// A copy of JB's `stringPresentation` with different handling for the `CSSStyledRuleDeclaration` case (see comment).
@OptIn(ComposeWebInternalApi::class)
private fun CSSRuleDeclaration.customStringPresentation(
    baseIndent: String = "",
    indent: String = "    ",
    delimiter: String = "\n"
): String {
    val cssRuleDeclaration = this
    val strings = mutableListOf<String>()
    strings.add("$baseIndent${cssRuleDeclaration.header} {")
    when (cssRuleDeclaration) {
        is CSSStyledRuleDeclaration -> {
            // Register properties and variables on a dummy element to leverage the strictness of the `setProperty`
            // function and avoid accidental leakage due to malformed values.
            // For example, `property("color", "red; background: blue")` should register as an invalid value for the
            // `color` property, and NOT as two valid declarations for `color` and `background`.
            val style = document.createElement("div").unsafeCast<HTMLElement>().style
            cssRuleDeclaration.style.properties.forEach { (name, value) ->
                style.setProperty(name, value.toString())
            }
            cssRuleDeclaration.style.variables.forEach { (name, value) ->
                style.setProperty(name, value.toString())
            }
            strings.add("$baseIndent$indent${style.cssText}")
        }

        is CSSGroupingRuleDeclaration -> {
            cssRuleDeclaration.rules.forEach { childRuleDeclaration ->
                strings.add(childRuleDeclaration.stringPresentation(baseIndent + indent, indent, delimiter))
            }
        }

        is CSSKeyframesRuleDeclaration -> {
            cssRuleDeclaration.keys.forEach { childRuleDeclaration ->
                strings.add(childRuleDeclaration.stringPresentation(baseIndent + indent, indent, delimiter))
            }
        }
    }
    strings.add("$baseIndent}")
    return strings.joinToString(delimiter)
}
