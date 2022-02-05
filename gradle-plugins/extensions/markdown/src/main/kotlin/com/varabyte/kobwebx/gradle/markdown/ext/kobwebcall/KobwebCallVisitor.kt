package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import com.varabyte.kobwebx.gradle.markdown.children
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.commonmark.node.Node

/** A visitor that should only be used with a [KobwebCallBlack], extracting relevant information from it. */
class KobwebCallVisitor : AbstractVisitor() {
    var call: KobwebCall? = null
        private set

    /**
     * A list of one or more nodes that should be treated as indented children of the call.
     *
     * Will either be null OR non-empty.
     */
    var childrenNodes: List<Node>? = null
        private set

    override fun visit(customBlock: CustomBlock) {
        require(customBlock is KobwebCallBlock)
        call = customBlock.firstChild as? KobwebCall
        childrenNodes = customBlock.children().drop(1).toList().takeUnless { it.isEmpty() }
    }
}