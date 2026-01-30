package com.varabyte.kobweb.browser.dom.selection

import org.w3c.dom.DocumentFragment
import org.w3c.dom.Node
import org.w3c.dom.Range
import org.w3c.dom.Window

/**
 * A Selection object represents the range of text selected by the user or the current position of the caret.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Selection">Selection API</a>
 */
external class Selection {
    val anchorNode: Node?
    val anchorOffset: Int
    val focusNode: Node?
    val focusOffset: Int
    val isCollapsed: Boolean
    val rangeCount: Int
    val type: String
    val direction: String

    fun addRange(range: Range)
    fun collapse(node: Node?, offset: Int = definedExternally)
    fun collapseToEnd()
    fun collapseToStart()
    fun containsNode(node: Node, allowPartialContainment: Boolean = definedExternally): Boolean
    fun deleteFromDocument()
    fun empty()
    fun extend(node: Node, offset: Int = definedExternally)
    fun getComposedRanges(vararg shadowRoots: ShadowRoot): Array<StaticRange>
    fun getRangeAt(index: Int): Range
    fun removeAllRanges()
    fun removeRange(range: Range)
    fun selectAllChildren(node: Node)
    fun setBaseAndExtent(anchorNode: Node, anchorOffset: Int, focusNode: Node, focusOffset: Int)
    fun setPosition(node: Node?, offset: Int = definedExternally)

    override fun toString(): String
}

/**
 * A ShadowRoot is the root node of a DOM subtree that is rendered separately from a document's main DOM tree.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/ShadowRoot">ShadowRoot API</a>
 */
abstract external class ShadowRoot : DocumentFragment

/**
 * A StaticRange object represents a static range of content within the document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/StaticRange">StaticRange API</a>
 */
external class StaticRange {
    val startContainer: Node
    val startOffset: Int
    val endContainer: Node
    val endOffset: Int
    val collapsed: Boolean
}

/**
 * Returns a [Selection] object representing the range of text selected by the user or the current position of the
 * caret.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/getSelection">Window.getSelection()</a>
 */
fun Window.getSelection(): Selection? = asDynamic().getSelection() as? Selection
