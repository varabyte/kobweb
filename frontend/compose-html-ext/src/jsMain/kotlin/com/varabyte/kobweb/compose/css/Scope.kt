package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// TODO: consider inlining and removing this property
val CSSScopeSupport: Boolean = js("typeof CSSScopeRule != 'undefined'")

// Dev note: This class was tweaked from the one for @media
// See also: org.jetbrains.compose.web.css.CSSMediaRuleDeclaration
@Suppress("EqualsOrHashCode")
class CSSScopeRuleDeclaration(
    val start: String?,
    val end: String?,
    override val rules: CSSRuleDeclarationList
) : CSSGroupingRuleDeclaration {
    override val header: String
        get() = buildString {
            append("@scope")
            if (start != null) {
                append(" ($start)")
            }
            if (end != null) {
                append(" to ($end)")
            }
        }

    override fun equals(other: Any?): Boolean {
        return if (other is CSSScopeRuleDeclaration) {
            start == other.start && end == other.end && rules == other.rules
        } else false
    }
}

/**
 * Wrap some styles with a `@scope` block.
 *
 * For example:
 * ```
 * object DefaultStyleSheet : StyleSheet() {
 *     init {
 *         scope(".dark", ".light") {
 *             "body" style {
 *                 backgroundColor(Black)
 *             }
 *         }
 *     }
 * }
 * ```
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.scope(
    start: String?,
    end: String?,
    rulesBuild: GenericStyleSheetBuilder<CSSStyleRuleBuilder>.() -> Unit
) {
    val rules = StyleSheet().apply(rulesBuild).cssRules
    add(CSSScopeRuleDeclaration(start, end, rules))
}
