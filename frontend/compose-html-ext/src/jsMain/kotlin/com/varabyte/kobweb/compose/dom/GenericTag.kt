package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.Element

/** A useful [ElementBuilder] for when you don't care about its specific type. */
class GenericElementBuilder private constructor(private val name: String) : ElementBuilder<Element> {
    companion object {
        private val cachedBuilders = mutableMapOf<String, GenericElementBuilder>()
        /** Create a new builder, caching it by type */
        fun create(name: String): GenericElementBuilder {
            return cachedBuilders.getOrPut(name) { GenericElementBuilder(name) }
        }
    }
    private val element by lazy { document.createElement(name) }
    override fun create() = element.cloneNode() as Element
}

/**
 * A way to easily specify a generic tag and optional attributes.
 *
 * See also [TagElement], which this delegates to.
 *
 * @param name The name of the tag, e.g. "a"
 * @param attrs An (optional) list of attributes separated by spaces, e.g. "href=\"...\" target=\"_blank\""
 */
@Composable
fun GenericTag(
    name: String,
    attrs: String? = null,
    content: (@Composable ElementScope<Element>.() -> Unit)? = null,
) {
    TagElement(
        elementBuilder = GenericElementBuilder.create(name),
        applyAttrs = if (attrs != null) {
            {
                // Handle input like "key=\"value\""
                fun parseAttrAssignment(attrAssignment: String) {
                    val parts = attrAssignment.split('=', limit = 2)
                    val attr = parts[0]
                    val value = parts.getOrElse(1) { "" }
                    attr(attr, value.removeSurrounding("\""))
                }

                // Break into parts separated by spaces, but ignore spaces within quotes
                val sb = StringBuilder()
                var insideQuotes = false
                for (c in attrs) {
                    when (c) {
                        '"' -> {
                            insideQuotes = !insideQuotes
                            sb.append(c)
                        }
                        ' ' -> {
                            if (insideQuotes) {
                                sb.append(c)
                            } else {
                                parseAttrAssignment(sb.toString())
                                sb.clear()
                            }
                        }
                        else -> sb.append(c)
                    }
                }
                if (sb.isNotEmpty()) {
                    check(!insideQuotes) { "Got invalid attributes with unclosed string: $attrs" }
                    parseAttrAssignment(sb.toString())
                }
            }
        } else {
            null
        },
        content
    )
}

