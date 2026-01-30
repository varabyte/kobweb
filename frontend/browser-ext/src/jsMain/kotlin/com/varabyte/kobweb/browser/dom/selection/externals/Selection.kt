package com.varabyte.kobweb.browser.dom.selection.externals

import org.w3c.dom.Node
import org.w3c.dom.Range

/**
 * External declaration for the browser's Selection API.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Selection">Selection API</a>
 */
internal external class Selection {
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
    fun getRangeAt(index: Int): Range
    fun removeAllRanges()
    fun removeRange(range: Range)
    fun selectAllChildren(node: Node)
    fun setBaseAndExtent(anchorNode: Node, anchorOffset: Int, focusNode: Node, focusOffset: Int)
    fun setPosition(node: Node?, offset: Int = definedExternally)

    override fun toString(): String
}
