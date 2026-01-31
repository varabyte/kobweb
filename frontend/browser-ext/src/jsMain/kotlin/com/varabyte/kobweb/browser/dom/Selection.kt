package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Node
import org.w3c.dom.Range
import org.w3c.dom.ShadowRoot

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Selection/getComposedRanges#options">getComposedRanges options</a>
 */
external interface GetComposedRangesOptions {
    var shadowRoots: Array<ShadowRoot>?
}

fun GetComposedRangesOptions(shadowRoots: Array<ShadowRoot>? = null): GetComposedRangesOptions {
    return js("{}").unsafeCast<GetComposedRangesOptions>().apply {
        if (shadowRoots != null) {
            this.shadowRoots = shadowRoots
        }
    }
}

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Selection">Selection API</a>
 */
external class Selection private constructor() {
    val anchorNode: Node?
    val anchorOffset: Int
    val direction: String
    val focusNode: Node?
    val focusOffset: Int
    val isCollapsed: Boolean
    val rangeCount: Int
    val type: String
    fun addRange(range: Range)
    fun collapse(node: Node?, offset: Int = definedExternally)
    fun collapseToEnd()
    fun collapseToStart()
    fun containsNode(node: Node, allowPartialContainment: Boolean = definedExternally): Boolean
    fun deleteFromDocument()
    fun empty()
    fun extend(node: Node, offset: Int = definedExternally)
    fun getComposedRanges(options: GetComposedRangesOptions = definedExternally): Array<StaticRange>
    fun getRangeAt(index: Int): Range
    fun modify(
        alter: String = definedExternally,
        direction: String = definedExternally,
        granularity: String = definedExternally,
    )
    fun removeAllRanges()
    fun removeRange(range: Range)
    fun selectAllChildren(node: Node)
    fun setBaseAndExtent(anchorNode: Node, anchorOffset: Int, focusNode: Node, focusOffset: Int)
    fun setPosition(node: Node?, offset: Int = definedExternally)
}
