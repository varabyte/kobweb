package com.varabyte.kobweb.browser.dom.selection

import com.varabyte.kobweb.browser.dom.selection.externals.Selection as ExternalSelection
import org.w3c.dom.Node
import org.w3c.dom.Window

/**
 * Returns a [Selection] object representing the range of text selected by the user or the current position of the caret.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/getSelection">Window.getSelection()</a>
 */
fun Window.getSelection(): Selection? {
    val externalSelection = asDynamic().getSelection() as ExternalSelection?
    return externalSelection?.let { Selection(it) }
}

/**
 * Represents a user's text selection or the current position of the caret.
 *
 * This is a Kotlin-friendly wrapper around the browser's Selection API.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Selection">Selection API</a>
 */
class Selection internal constructor(private val external: ExternalSelection) {
    /**
     * Returns the [Node] in which the selection begins.
     */
    val anchorNode: Node? get() = external.anchorNode

    /**
     * Returns the offset of the selection's anchor within the [anchorNode].
     */
    val anchorOffset: Int get() = external.anchorOffset

    /**
     * Returns the [Node] in which the selection ends.
     */
    val focusNode: Node? get() = external.focusNode

    /**
     * Returns the offset of the selection's focus within the [focusNode].
     */
    val focusOffset: Int get() = external.focusOffset

    /**
     * Returns whether the selection's start and end points are at the same position.
     */
    val isCollapsed: Boolean get() = external.isCollapsed

    /**
     * Returns the number of ranges in the selection.
     */
    val rangeCount: Int get() = external.rangeCount

    /**
     * Returns a string describing the type of the current selection.
     *
     * This can be:
     * - "None": No selection has been made.
     * - "Caret": The selection is collapsed (i.e., the caret).
     * - "Range": A range has been selected.
     */
    val type: String get() = external.type

    /**
     * Adds a Range object to the selection.
     */
    fun addRange(range: dynamic) = external.addRange(range)

    /**
     * Collapses the selection to the start of the node's child nodes at the specified offset.
     */
    fun collapse(node: Node?, offset: Int = 0) = external.collapse(node, offset)

    /**
     * Collapses the selection to the end of the last range in the selection.
     */
    fun collapseToEnd() = external.collapseToEnd()

    /**
     * Collapses the selection to the start of the first range in the selection.
     */
    fun collapseToStart() = external.collapseToStart()

    /**
     * Returns whether a given node is part of the selection.
     *
     * @param node The node to check.
     * @param allowPartialContainment When true, partially contained nodes are considered contained.
     */
    fun containsNode(node: Node, allowPartialContainment: Boolean = false): Boolean =
        external.containsNode(node, allowPartialContainment)

    /**
     * Removes the contents of all ranges from the document.
     */
    fun deleteFromDocument() = external.deleteFromDocument()

    /**
     * Removes all ranges from the selection (same as [removeAllRanges]).
     */
    fun empty() = external.empty()

    /**
     * Moves the focus of the selection to a specified point.
     */
    fun extend(node: Node, offset: Int = 0) = external.extend(node, offset)

    /**
     * Returns a Range object representing one of the ranges currently selected.
     */
    fun getRangeAt(index: Int): dynamic = external.getRangeAt(index)

    /**
     * Removes all ranges from the selection.
     */
    fun removeAllRanges() = external.removeAllRanges()

    /**
     * Removes a range from the selection.
     */
    fun removeRange(range: dynamic) = external.removeRange(range)

    /**
     * Adds all the children of the specified node to the selection.
     */
    fun selectAllChildren(node: Node) = external.selectAllChildren(node)

    /**
     * Sets the selection to be a range including all or parts of two specified DOM nodes and any content between them.
     */
    fun setBaseAndExtent(anchorNode: Node, anchorOffset: Int, focusNode: Node, focusOffset: Int) =
        external.setBaseAndExtent(anchorNode, anchorOffset, focusNode, focusOffset)

    /**
     * Collapses the selection to the start of the node's child nodes at the specified offset (same as [collapse]).
     */
    fun setPosition(node: Node?, offset: Int = 0) = external.setPosition(node, offset)

    /**
     * Returns a string currently being represented by the selection object.
     */
    override fun toString(): String = external.toString()
}
