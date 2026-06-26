package com.varabyte.kobweb.compose.css

import kotlinx.browser.document
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.StyleScope
import org.w3c.dom.HTMLElement

object CssTestUtils {
    /**
     * Convert all properties in a style to the String that would ultimately get put into an HTML style attribute.
     *
     * In other words, key / values will be split by a ':' and multiple properties by a ';'.
     */
    fun styleToText(block: StyleScope.() -> Unit): String {
        // We don't care about comparing -- but it's an easy way to construct a style scope, as Compose HTML doesn't
        // give us an easy way otherwise.
        val styleScope = ComparableStyleScope()
        block.invoke(styleScope)

        return styleScope.properties.joinToString("; ") { "${it.name}: ${it.value}" }
    }

    /**
     * Apply a style block to a temporary HTML element and return the inline style of the element, in the form
     * `key: value; key2: value2;`.
     *
     * The exact text of the values may be altered by the browser's internal normalization
     * (e.g. `property("margin", "10px 10px")` becomes `margin: 10px;`).
     *
     * This is useful for testing that the browser recognizes a style as valid CSS, because an invalid style will not
     * get applied and thus output an empty string.
     */
    fun styleToHtmlElementCssText(block: StyleScope.() -> Unit): String {
        val element = document.createElement("div").unsafeCast<HTMLElement>()
        object : StyleScope {
            override fun property(propertyName: String, value: StylePropertyValue) {
                element.style.setProperty(propertyName, value.toString())
            }

            override fun variable(variableName: String, value: StylePropertyValue) {
                element.style.setProperty(variableName, value.toString())
            }
        }.block()
        return element.style.cssText
            .also { element.remove() }
    }
}

/**
 * Convenience method that checks if the configured style generates valid CSS.
 */
fun CssTestUtils.isValidCss(block: StyleScope.() -> Unit): Boolean {
    // If the passed in CSS text is invalid, the browser will reject it and return a blank string
    return styleToHtmlElementCssText(block).isNotBlank()
}