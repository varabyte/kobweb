package com.varabyte.kobweb.browser.dom

import org.w3c.dom.DocumentFragment
import org.w3c.dom.Node

/**
 * Exposes the JavaScript [Range](https://developer.mozilla.org/en-US/docs/Web/API/Range) to Kotlin
 *
 * Represents a fragment of a document that can contain nodes and parts of text nodes.
 */
external class Range {
    val collapsed: Boolean
    val commonAncestorContainer: Node
    val endContainer: Node
    val endOffset: Int
    val startContainer: Node
    val startOffset: Int

    fun collapse(toStart: Boolean = definedExternally)
    fun compareBoundaryPoints(how: Short, sourceRange: Range): Short
    fun comparePoint(node: Node, offset: Int): Short
    fun cloneContents(): DocumentFragment
    fun cloneRange(): Range
    fun createContextualFragment(fragment: String): DocumentFragment
    fun deleteContents()
    fun detach()
    fun extractContents(): DocumentFragment
    fun getBoundingClientRect(): dynamic
    fun getClientRects(): dynamic
    fun insertNode(node: Node)
    fun intersectsNode(node: Node): Boolean
    fun isPointInRange(node: Node, offset: Int): Boolean
    fun selectNode(node: Node)
    fun selectNodeContents(node: Node)
    fun setEnd(node: Node, offset: Int)
    fun setEndAfter(node: Node)
    fun setEndBefore(node: Node)
    fun setStart(node: Node, offset: Int)
    fun setStartAfter(node: Node)
    fun setStartBefore(node: Node)
    fun surroundContents(newParent: Node)
    override fun toString(): String

    companion object {
        val START_TO_START: Short
        val START_TO_END: Short
        val END_TO_END: Short
        val END_TO_START: Short
    }
}
