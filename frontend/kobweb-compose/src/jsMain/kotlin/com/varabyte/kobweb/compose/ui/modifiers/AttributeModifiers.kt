package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.browser.util.camelCaseToKebabCase
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import org.w3c.dom.HTMLElement

/**
 * A convenience method for defining a single attribute key/value pair.
 *
 * @see attrs
 */
fun Modifier.attr(attr: String, value: String) = attrs(attr to value)

/**
 * A convenience method for defining multiple, general attribute key/value pairs.
 *
 * For example:
 *
 * ```kotlin
 * Modifier.attrs("src" to "cat.jpg", "alt" to "A close-up of the face of a tabby cat.")
 * ```
 */
fun Modifier.attrs(vararg attrValues: Pair<String, String>) = attrsModifier {
    attrValues.forEach { (attr, value) ->
        attr(attr, value)
    }
}

/**
 * Define a single [HTML data attribute](https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes).
 *
 * This is a convenience method for creating a data attribute, which is identified by its "data-*" prefix.
 *
 * For example, migrating the example at
 *   https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes#html_syntax
 *
 * ```kotlin
 * Modifier
 *   .dataAttr("columns", "3") // registers "data-columns"
 *   .dataAttr("index-number", "12314") // registers "data-index-number"
 *   .dataAttr("parent", "cars") // registers "data-parent"
 * ```
 *
 * To query a data value later through code, use the [HTMLElement.dataset] property (and note that names passed in using
 * kebab case need to be queried using camel case).
 *
 * ```kotlin
 * element.dataset["columns"] // "3"
 * element.dataset["indexNumber"] // "12314"
 * element.dataset["parent"] // "cars"
 * ```
 *
 * Because data attributes can be queried this way, this method also supports taking in camel case names, which it
 * converts to kebab case under the hood:
 *
 * ```kotlin
 * Modifier.dataAttr("indexNumber", "12314") // same as registering "index-number"
 * ```
 *
 * @see dataAttrs
 */
fun Modifier.dataAttr(name: String, value: String) = dataAttrs(name to value)

/**
 * Declare one or more [HTML data attributes](https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes)
 *
 * For example, migrating the example at
 *   https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes#html_syntax
 *
 * ```kotlin
 * Modifier
 *   .dataAttrs("columns" to "3", "index-number" to "12314", "parent" to "cars")
 * ```
 *
 * To query a data value later through code, use the [HTMLElement.dataset] property (and note that names passed in using
 * kebab case need to be queried using camel case).
 *
 * ```kotlin
 * element.dataset["columns"] // "3"
 * element.dataset["indexNumber"] // "12314"
 * element.dataset["parent"] // "cars"
 * ```
 *
 * Because data attributes can be queried this way, this method also supports taking in camel case names, which it
 * converts to kebab case under the hood:
 *
 * ```kotlin
 * Modifier.dataAttrs("indexNumber" to "12314") // same as registering "index-number"
 * ```
 */
fun Modifier.dataAttrs(vararg nameValues: Pair<String, String>) = attrsModifier {
    nameValues.forEach { (name, value) ->
        attr("data-${name.camelCaseToKebabCase()}", value)
    }
}



