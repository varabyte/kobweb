package com.varabyte.kobwebx.gradle.markdown

import org.commonmark.node.Node

fun Node.children(): Sequence<Node> {
    return sequence<Node> {
        var currNode = firstChild
        while (currNode != null) {
            yield(currNode)
            currNode = currNode.next
        }
    }
}