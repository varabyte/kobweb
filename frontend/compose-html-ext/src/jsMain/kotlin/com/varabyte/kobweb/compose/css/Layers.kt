package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// Dev note: This class was tweaked from the one for @media
// See also: org.jetbrains.compose.web.css.CSSMediaRuleDeclaration
@Suppress("EqualsOrHashCode")
class CSSLayerRuleDeclaration(
    val name: String,
    override val rules: CSSRuleDeclarationList
) : CSSGroupingRuleDeclaration {
    override val header: String
        get() = "@layer $name"

    override fun equals(other: Any?): Boolean {
        return if (other is CSSLayerRuleDeclaration) {
            name == other.name && rules == other.rules
        } else false
    }
}

/**
 * Wrap some styles with a `@layer` block.
 *
 * For example:
 *
 *
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.layer(
    name: String,
    rulesBuild: GenericStyleSheetBuilder<CSSStyleRuleBuilder>.() -> Unit
) {
    val rules = StyleSheet().apply(rulesBuild).cssRules
    add(CSSLayerRuleDeclaration(name, rules))
}
