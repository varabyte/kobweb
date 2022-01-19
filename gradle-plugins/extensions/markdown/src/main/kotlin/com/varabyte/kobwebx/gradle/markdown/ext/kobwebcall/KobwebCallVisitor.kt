package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock

/** A visitor that should only be used with a [KobwebCallBlack], extracting relevant information from it. */
class KobwebCallVisitor : AbstractVisitor() {
    var call: KobwebCall? = null
        private set

    override fun visit(customBlock: CustomBlock) {
        require(customBlock is KobwebCallBlock)
        call = customBlock.firstChild as KobwebCall
    }
}