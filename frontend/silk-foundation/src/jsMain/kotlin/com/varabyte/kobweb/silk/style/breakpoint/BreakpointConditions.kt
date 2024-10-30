package com.varabyte.kobweb.silk.style.breakpoint

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.GenericTag
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.theme.breakpoint.toMinWidthQuery
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSStyleSheet

private fun CSSMediaQuery.invert(): CSSMediaQuery {
    // Note: In CSS media queries, both min-width and max-width values are inclusive. However, we want
    // `min <= width < max`. This is possible since 2023 using CSS media range queries, but for older browsers, we
    // instead use query inversion instead. In other words, if we want "x < width", we can also do "not (x >= width)".
    // In other words, inverting an inclusive comparison gives us an exclusive check.
    // Note 2: It seems like you have to include the "not all" for some technical reason; otherwise, this would just be
    // "Not(this)"
    // See also: https://stackoverflow.com/a/13611538
    return CSSMediaQuery.Raw("not all and $this")
}

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
//
// Note that a real solution would be if the Compose HTML APIs allowed us to identify a style as important,
// but currently, as you can see with their code here:
// https://github.com/JetBrains/compose-multiplatform/blob/9e25001e9e3a6be96668e38c7f0bd222c54d1388/html/core/src/jsMain/kotlin/org/jetbrains/compose/web/elements/Style.kt#L116
// they don't support it. (It would have been nice to be a version of the API that takes an additional
// priority parameter, as in `setProperty("x", "y", "important")`)
//
// Below, we recreate Compose HTML's `Style()` composable, but directly set the styles we want to mark `!important`.
// https://github.com/JetBrains/compose-multiplatform/blob/9e25001e9e3a6be96668e38c7f0bd222c54d1388/html/core/src/jsMain/kotlin/org/jetbrains/compose/web/elements/Elements.kt#L979
@Composable
internal fun SilkBreakpointDisplayStyles() {
    GenericTag("style") {
        DisposableEffect(Unit) {
            val cssStylesheet = scopeElement.unsafeCast<HTMLStyleElement>().sheet as? CSSStyleSheet
            Breakpoint.entries.forEach { breakpoint ->
                // `display-if-at-least-zero` and `display-until-zero` are unnecessary, but provided for completeness
                val minWidthQuery = CSSMediaRuleDeclaration(breakpoint.toMinWidthQuery(), emptyList()).header
                val invertMinWidthQuery =
                    CSSMediaRuleDeclaration(breakpoint.toMinWidthQuery().invert(), emptyList()).header
                cssStylesheet
                    ?.addRule("$invertMinWidthQuery { .silk-display-if-at-least-${breakpoint.name.lowercase()} { display: none !important; } }")
                cssStylesheet
                    ?.addRule("$minWidthQuery { .silk-display-until-${breakpoint.name.lowercase()} { display: none !important; } }")
            }
            onDispose {
                cssStylesheet?.clearCSSRules()
            }
        }
    }
}

private fun CSSStyleSheet.addRule(cssRule: String): CSSRule? {
    val cssRuleIndex = this.insertRule(cssRule, this.cssRules.length)
    return this.cssRules.item(cssRuleIndex)
}

private fun CSSStyleSheet.clearCSSRules() {
    repeat(cssRules.length) {
        deleteRule(0)
    }
}

fun Modifier.displayIfAtLeast(breakpoint: Breakpoint) =
    this.classNames("silk-display-if-at-least-${breakpoint.name.lowercase()}")

fun Modifier.displayUntil(breakpoint: Breakpoint) = this.classNames("silk-display-until-${breakpoint.name.lowercase()}")
fun Modifier.displayBetween(breakpointLower: Breakpoint, breakpointUpper: Breakpoint): Modifier {
    require(breakpointLower.ordinal < breakpointUpper.ordinal) { "displayBetween breakpoints passed in wrong order: $breakpointLower should be smaller than $breakpointUpper" }

    return this.classNames(
        "silk-display-if-${breakpointLower.name.lowercase()}",
        "silk-display-until-${breakpointUpper.name.lowercase()}"
    )
}
