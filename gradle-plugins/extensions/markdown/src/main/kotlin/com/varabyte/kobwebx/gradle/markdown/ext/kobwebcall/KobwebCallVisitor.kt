package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.commonmark.node.Text

/** A visitor that should only be used with a [KobwebCallBlack], extracting relevant information from it. */
class KobwebCallVisitor : AbstractVisitor() {
    var text: String = ""
        private set

    override fun visit(customBlock: CustomBlock) {
        require(customBlock is KobwebCallBlock)
        text = (customBlock.firstChild as Text).literal
    }
}