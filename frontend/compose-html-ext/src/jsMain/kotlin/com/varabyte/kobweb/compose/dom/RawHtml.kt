package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import com.varabyte.kobweb.framework.annotations.DelicateApi
import org.w3c.dom.Comment
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Text
import org.w3c.dom.asList
import org.w3c.dom.parsing.DOMParser
import org.jetbrains.compose.web.dom.Text as JbText

@Composable
private fun walk(node: Node) {
    when (node) {
        is HTMLElement -> {
            val attrsStr = node.attributes.asList().takeIf { it.isNotEmpty() }
                ?.joinToString(" ") { attr -> "${attr.name}=\"${attr.value}\"" }

            GenericTag(node.nodeName, attrsStr) {
                walk(node.childNodes)
            }
        }

        is Text -> {
            JbText(node.wholeText)
        }

        is Comment -> {
            // Do nothing
        }

        else -> {
            error("Please report: RawHtml missing handling for node \"${node.nodeName}\"")
        }
    }
}

@Composable
private fun walk(nodes: NodeList) {
    nodes.asList().forEach { node -> walk(node) }
}

/**
 * Generate Compose nodes for general HTML input text.
 *
 * This is a convenience method for generating compose nodes for raw HTML.
 *
 * For example, `"<div>Hello <i>World</i></div>"` will generate:
 *
 * ```
 * GenericTag("div") {
 *   Text("Hello ")
 *   GenericTag("i") {
 *     Text("World")
 *   }
 * }
 * ```
 *
 * **Important:** This method does *not* sanitize input. **Do not** use it with untrusted HTML, as it may introduce
 * security vulnerabilities, such as XSS (Cross-Site Scripting).
 */
@Composable
@DelicateApi("This method does *not* sanitize input and must only be called with HTML that you fully control. Using untrusted input may lead to security vulnerabilities.")
fun RawHtml(htmlString: String) {
    val parser = DOMParser()
    val doc = parser.parseFromString(htmlString, "text/html")

    doc.body?.childNodes?.let { children -> walk(children) }
}