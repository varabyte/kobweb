package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Node

/**
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/StaticRange/StaticRange#rangespec)
 */
external interface StaticRangeInit {
    var endContainer: Node
    var endOffset: Int
    var startContainer: Node
    var startOffset: Int
}

fun StaticRangeInit(
    endContainer: Node? = null,
    endOffset: Int? = null,
    startContainer: Node? = null,
    startOffset: Int? = null,
): StaticRangeInit {
    return js("{}").unsafeCast<StaticRangeInit>().apply {
        if (endContainer != null) this.endContainer = endContainer
        if (endOffset != null) this.endOffset = endOffset
        if (startContainer != null) this.startContainer = startContainer
        if (startOffset != null) this.startOffset = startOffset
    }
}

/**
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/StaticRange)
 */
// Note: This is supposed to subclass AbstractRange, but for now, we don't want to introduce that class
// since the stdlib Range class itself doesn't inherit from it
external class StaticRange(init: StaticRangeInit) {
    val collapsed: Boolean
    val endContainer: Node
    val endOffset: Int
    val startContainer: Node
    val startOffset: Int
}
