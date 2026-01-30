package com.varabyte.kobweb.browser.dom.selection

import org.w3c.dom.Node
import org.w3c.dom.Range
import org.w3c.dom.Window
import com.varabyte.kobweb.browser.dom.selection.externals.Selection as ActualSelection

/**
 * Returns a [Selection] object representing the range of text selected by the user or the current position of the
 * caret.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/getSelection">Window.getSelection()</a>
 */
fun Window.getSelection(): Selection? {
    val actualSelection = this.asDynamic().getSelection() as? ActualSelection
    return actualSelection?.let { Selection(it) }
}

/**
 * The direction of a selection.
 */
enum class SelectionDirection {
    /** The direction is not yet set. */
    None,
    /** The selection was made forward (from left to right in a left-to-right locale). */
    Forward,
    /** The selection was made backward (from right to left in a left-to-right locale). */
    Backward;

    companion object {
        internal fun from(value: String): SelectionDirection = when (value) {
            "forward" -> Forward
            "backward" -> Backward
            else -> None
        }
    }
}

/**
 * The type of a selection.
 */
enum class SelectionType {
    /** No selection is currently made. */
    None,
    /** The selection is collapsed (i.e. the caret is placed on some text, but no range is selected). */
    Caret,
    /** A range is selected. */
    Range;

    companion object {
        internal fun from(value: String): SelectionType = when (value) {
            "Caret" -> Caret
            "Range" -> Range
            else -> None
        }
    }
}

/**
 * A Kotlin-friendly wrapper around the browser's Selection API.
 *
 * A Selection object represents the range of text selected by the user or the current position of the caret.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Selection">Selection API</a>
 */
class Selection internal constructor(private val actualSelection: ActualSelection) {
    /**
     * Returns the [Node] in which the selection begins.
     *
     * Can return `null` if selection never existed in the document (e.g., an iframe that was never clicked on).
     */
    val anchorNode: Node? get() = actualSelection.anchorNode

    /**
     * Returns a number representing the offset of the selection's anchor within the [anchorNode].
     *
     * If [anchorNode] is a text node, this is the number of characters within anchorNode preceding the anchor.
     * If [anchorNode] is an element, this is the number of child nodes of the anchorNode preceding the anchor.
     */
    val anchorOffset: Int get() = actualSelection.anchorOffset

    /**
     * Returns the [Node] in which the selection ends.
     *
     * Can return `null` if selection never existed in the document (e.g., an iframe that was never clicked on).
     */
    val focusNode: Node? get() = actualSelection.focusNode

    /**
     * Returns a number representing the offset of the selection's focus within the [focusNode].
     *
     * If [focusNode] is a text node, this is the number of characters within focusNode preceding the focus.
     * If [focusNode] is an element, this is the number of child nodes of the focusNode preceding the focus.
     */
    val focusOffset: Int get() = actualSelection.focusOffset

    /**
     * Returns `true` if the selection's start and end points are at the same position.
     */
    val isCollapsed: Boolean get() = actualSelection.isCollapsed

    /**
     * Returns the number of ranges in the selection.
     */
    val rangeCount: Int get() = actualSelection.rangeCount

    /**
     * Returns the type of the current selection.
     */
    val type: SelectionType get() = SelectionType.from(actualSelection.type)

    /**
     * Returns the direction of the current selection.
     */
    val direction: SelectionDirection get() = SelectionDirection.from(actualSelection.direction)

    /**
     * Adds a [Range] to the selection.
     *
     * @param range A [Range] object that will be added to the selection.
     */
    fun addRange(range: Range) = actualSelection.addRange(range)

    /**
     * Collapses the current selection to a single point.
     *
     * @param node The caret location will be within this node. This value can also be set to null — if null is
     *   specified, the method will behave like [removeAllRanges], i.e. all ranges will be removed from the selection.
     * @param offset The offset in [node] to which the selection will be collapsed. If not specified, the default
     *   value 0 is used.
     */
    fun collapse(node: Node?, offset: Int = 0) = actualSelection.collapse(node, offset)

    /**
     * Collapses the selection to the end of the last range in the selection.
     */
    fun collapseToEnd() = actualSelection.collapseToEnd()

    /**
     * Collapses the selection to the start of the first range in the selection.
     */
    fun collapseToStart() = actualSelection.collapseToStart()

    /**
     * Indicates whether a specified node is part of the selection.
     *
     * @param node The node that is being looked for in the selection.
     * @param allowPartialContainment When true, this method returns true when a part of the node is part of the
     *   selection. When false, this method only returns true when the entire node is part of the selection.
     *   Defaults to false.
     */
    fun containsNode(node: Node, allowPartialContainment: Boolean = false): Boolean =
        actualSelection.containsNode(node, allowPartialContainment)

    /**
     * Deletes the selection's content from the document.
     */
    fun deleteFromDocument() = actualSelection.deleteFromDocument()

    /**
     * Removes all ranges from the selection.
     *
     * This is an alias for [removeAllRanges].
     */
    fun empty() = actualSelection.empty()

    /**
     * Moves the focus of the selection to a specified point.
     *
     * @param node The node within which the focus will be moved.
     * @param offset The offset position within [node] where the focus will be moved to. If not specified, the
     *   default value 0 is used.
     */
    fun extend(node: Node, offset: Int = 0) = actualSelection.extend(node, offset)

    /**
     * Returns a [Range] object representing one of the ranges currently selected.
     *
     * @param index The zero-based index of the range to return.
     * @return The specified [Range] object.
     */
    fun getRangeAt(index: Int): Range = actualSelection.getRangeAt(index)

    /**
     * Removes all ranges from the selection, leaving the [anchorNode] and [focusNode] properties equal to null and
     * nothing selected.
     */
    fun removeAllRanges() = actualSelection.removeAllRanges()

    /**
     * Removes a range from the selection.
     *
     * @param range A range object that will be removed from the selection.
     */
    fun removeRange(range: Range) = actualSelection.removeRange(range)

    /**
     * Adds all the children of the specified node to the selection.
     *
     * @param node All children of [node] will be selected. [node] itself is not part of the selection.
     */
    fun selectAllChildren(node: Node) = actualSelection.selectAllChildren(node)

    /**
     * Sets the selection to be a range including all or parts of two specified DOM nodes, and any content located
     * between them.
     *
     * @param anchorNode The node at the start of the selection.
     * @param anchorOffset The number of child nodes from the start of [anchorNode] that should be excluded from the
     *   selection. So for example, if the value is 0 the whole node is included. If the value is 1, the whole node
     *   minus the first child node is included. And so on.
     * @param focusNode The node at the end of the selection.
     * @param focusOffset The number of child nodes from the start of [focusNode] that should be included in the
     *   selection. So for example, if the value is 0 the whole node is excluded. If the value is 1, the first child
     *   node is included. And so on.
     */
    fun setBaseAndExtent(anchorNode: Node, anchorOffset: Int, focusNode: Node, focusOffset: Int) =
        actualSelection.setBaseAndExtent(anchorNode, anchorOffset, focusNode, focusOffset)

    /**
     * Collapses the current selection to a single point.
     *
     * This is an alias for [collapse].
     *
     * @param node The caret location will be within this node. This value can also be set to null — if null is
     *   specified, the method will behave like [removeAllRanges], i.e. all ranges will be removed from the selection.
     * @param offset The offset in [node] to which the selection will be collapsed. If not specified, the default
     *   value 0 is used.
     */
    fun setPosition(node: Node?, offset: Int = 0) = actualSelection.setPosition(node, offset)

    /**
     * Returns the text content of the current selection.
     */
    override fun toString(): String = actualSelection.toString()
}
