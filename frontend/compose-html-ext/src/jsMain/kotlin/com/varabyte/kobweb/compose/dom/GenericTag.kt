package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.Element

/** A useful [ElementBuilder] for when you don't care about its specific type. */
class GenericElementBuilder private constructor(private val name: String) : ElementBuilder<Element> {
    companion object {
        private val cachedBuilders = mutableMapOf<String, GenericElementBuilder>()

        /** Create a new builder, caching it by type. */
        fun create(name: String): GenericElementBuilder {
            return cachedBuilders.getOrPut(name) { GenericElementBuilder(name) }
        }
    }

    private val element by lazy { document.createElement(name) }
    override fun create() = element.cloneNode() as Element
}

/**
 * A useful [ElementBuilder] for elements that need to use the `createElementNS` method for creation.
 *
 * For example, this can be useful for SVG elements.
 */
class GenericNamespacedElementBuilder private constructor(
    private val namespace: String,
    private val qualifiedName: String
) : ElementBuilder<Element> {
    companion object {
        private val cachedBuilders = mutableMapOf<Pair<String, String>, GenericNamespacedElementBuilder>()

        /** Create a new builder, caching it by type. */
        fun create(namespace: String, qualifiedName: String): GenericNamespacedElementBuilder {
            return cachedBuilders.getOrPut(namespace to qualifiedName) {
                GenericNamespacedElementBuilder(namespace, qualifiedName)
            }
        }
    }

    private val element by lazy { document.createElementNS(namespace, qualifiedName) }
    override fun create() = element.cloneNode() as Element
}

/**
 * A way to easily specify a generic tag and optional attributes.
 *
 * See also [TagElement], which this delegates to.
 *
 * @param name The name of the tag, e.g. "a"
 * @param attrsStr An (optional) list of attributes separated by spaces, e.g. "href=\"...\" target=\"_blank\""
 */
@Composable
fun GenericTag(
    name: String,
    attrsStr: String? = null,
    content: (@Composable ElementScope<Element>.() -> Unit)? = null,
) {
    val attrs: AttrBuilderContext<Element>? = attrsStr?.let {
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
            for (c in attrsStr) {
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
                check(!insideQuotes) { "Got invalid attributes with unclosed string: $attrsStr" }
                parseAttrAssignment(sb.toString())
            }
        }
    }

    GenericTag(
        name = name,
        namespace = null,
        attrs = attrs,
        content = content
    )
}

/**
 * Like the other [GenericTag] but allows richer typing as well as namespaced elements.
 *
 * The other [GenericTag] is useful for contexts like markdown, where you're parsing tags from a string and don't care
 * as much about Kotlin types. This version is a useful way to create composable elements in a type-safe way. Mostly,
 * this was created to allow users to create SVG elements.
 *
 * @param namespace If specified, a namespace for the current element, e.g. "http://www.w3.org/2000/svg"
 */
@Composable
fun <E : Element> GenericTag(
    name: String,
    namespace: String? = null,
    attrs: AttrBuilderContext<E>? = null,
    content: (@Composable ElementScope<E>.() -> Unit)? = null,
) {
    @Suppress("UNCHECKED_CAST")
    TagElement(
        elementBuilder = (namespace?.let { GenericNamespacedElementBuilder.create(it, name) }
            ?: GenericElementBuilder.create(name)) as ElementBuilder<E>,
        applyAttrs = attrs,
        content
    )
}
