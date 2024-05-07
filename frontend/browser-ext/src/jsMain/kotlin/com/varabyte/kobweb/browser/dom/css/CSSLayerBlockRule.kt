package com.varabyte.kobweb.browser.dom.css

import org.w3c.dom.css.CSSGroupingRule

/**
 * Exposes the JavaScript [CSSLayerBlockRule](https://developer.mozilla.org/docs/Web/API/CSSLayerBlockRule) to Kotlin
 */
sealed external class CSSLayerBlockRule : CSSGroupingRule {
    val name: String
}
