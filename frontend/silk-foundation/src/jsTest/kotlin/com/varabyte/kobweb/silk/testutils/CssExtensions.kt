package com.varabyte.kobweb.silk.testutils

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.CSSSelector
import org.jetbrains.compose.web.internal.runtime.ComposeWebInternalApi

data class TestCssRule(
    val header: String,
    val selector: CSSSelector,
    val style: StyleHolder,
    val query: CSSMediaQuery? = null,
    val layer: String? = null,
) {
    override fun toString(): String {
        return buildString {
            if (layer != null) {
                append("@layer $layer { ")
            }
            if (query != null) {
                append("@media $query { ")
            }
            append("$selector { ")
            append(style.toProperties().entries.joinToString(separator = "") { (key, value) -> "$key:$value;" })
            append(" }")
            if (query != null) {
                append(" }")
            }
            if (layer != null) {
                append(" }")
            }

        }
    }

}

/** Return an [AttrsScope] whose contents can be queried. */
fun StyleSheet.flattenCssRules(): List<TestCssRule> {
    val ruleLayers = mutableMapOf<CSSStyleRuleDeclaration, String>()
    val ruleQueries = mutableMapOf<CSSStyleRuleDeclaration, CSSMediaQuery>()

    // Starting in v0.21.1, we started wrapping all styles in scopes for more modern color mode management. However,
    // for tests, let's peel these out of the way to make it easier to get to the inner data we care about.
    val unscoped = cssRules.flatMap { rule ->
        if (rule is CSSScopeRuleDeclaration) rule.rules else listOf(rule)
    }

    unscoped.filterIsInstance<CSSLayerRuleDeclaration>().forEach { layerRule ->
        layerRule.rules.filterIsInstance<CSSStyleRuleDeclaration>().forEach { nestedRule ->
            ruleLayers[nestedRule] = layerRule.name
        }
    }

    unscoped.filterIsInstance<CSSMediaRuleDeclaration>().forEach { mediaRule ->
        mediaRule.rules.filterIsInstance<CSSStyleRuleDeclaration>().forEach { nestedRule ->
            ruleQueries[nestedRule] = mediaRule.query
        }
    }

    val styleRules = unscoped.filterIsInstance<CSSStyleRuleDeclaration>().toSet() + ruleLayers.keys + ruleQueries.keys
    return styleRules.map { styleRule ->
        TestCssRule(
            styleRule.header,
            styleRule.selector,
            styleRule.style,
            ruleQueries[styleRule],
            ruleLayers[styleRule]
        )
    }
}

@OptIn(ComposeWebInternalApi::class)
fun StyleHolder.toProperties(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (property in this.properties) {
        map[property.name] = property.value.toString()
    }
    return map
}

@OptIn(ComposeWebInternalApi::class)
fun StyleHolder.toVariables(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (variable in this.variables) {
        map[variable.name] = variable.value.toString()
    }
    return map
}
