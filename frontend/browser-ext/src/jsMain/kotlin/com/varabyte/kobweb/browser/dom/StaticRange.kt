package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Node

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/StaticRange/StaticRange#rangespec">StaticRange init object API</a>
 */
internal external interface StaticRangeInit {
    var startContainer: Node
    var startOffset: Int
    var endContainer: Node
    var endOffset: Int
}

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/StaticRange/StaticRange#rangespec">StaticRange init object API</a>
 */
fun StaticRange(
    startContainer: Node,
    startOffset: Int,
    endContainer: Node,
    endOffset: Int,
): StaticRange {
    return StaticRange(js("{}").unsafeCast<StaticRangeInit>().apply {
        this.startContainer = startContainer
        this.startOffset = startOffset
        this.endContainer = endContainer
        this.endOffset = endOffset
    })
}

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/StaticRange">StaticRange API</a>
 */
// Note: This is supposed to subclass AbstractRange, but for now, we don't want to introduce that class
// since the stdlib Range class itself doesn't inherit from it
external class StaticRange internal constructor(init: StaticRangeInit) {
    val collapsed: Boolean
    val endContainer: Node
    val endOffset: Int
    val startContainer: Node
    val startOffset: Int
}
