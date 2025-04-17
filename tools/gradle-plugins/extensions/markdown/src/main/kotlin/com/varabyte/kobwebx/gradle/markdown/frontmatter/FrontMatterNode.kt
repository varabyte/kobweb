package com.varabyte.kobwebx.gradle.markdown.frontmatter

import com.varabyte.kobwebx.frontmatter.FrontMatterElement
import org.commonmark.node.CustomNode

internal class FrontMatterNode(val element: FrontMatterElement.ValueMap) : CustomNode()
