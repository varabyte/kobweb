package com.varabyte.kobwebx.gradle.markdown.frontmatter

import org.commonmark.node.CustomBlock

internal class FrontMatterBlock : CustomBlock() {
    val frontMatterNode get() = firstChild as FrontMatterNode
}
