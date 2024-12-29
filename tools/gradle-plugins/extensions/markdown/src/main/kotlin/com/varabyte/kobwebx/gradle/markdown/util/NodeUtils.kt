package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.kobwebx.gradle.markdown.children
import org.commonmark.node.Code
import org.commonmark.node.Node
import org.commonmark.node.Text

/**
 * Convert a [Node] to any literal value it contains.
 *
 * This is useful if you are pretty sure that a node contains some text but it might be deep down in its children. For
 * example, if a node represents a link, then the text value is contained as a child node.
 */
internal val Node.nestedLiteral: String get() {
    return when (this) {
        is Text -> this.literal
        is Code -> this.literal
        else -> this.children().joinToString { it.nestedLiteral }
    }
}
