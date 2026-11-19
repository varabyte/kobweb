@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.truthish.assertThat
import org.commonmark.parser.Parser
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class NodeCacheTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `finds markdown by path relative to a root`() {
        val markdownRoot = tempFolder.newFolder("markdown")
        markdownRoot.resolve("blog").mkdirs()
        markdownRoot.resolve("blog/Post.md").writeText("Hello")
        val nodeCache = NodeCache(Parser.builder().build(), setOf(markdownRoot.canonicalFile))

        assertThat(nodeCache.getRelative("blog/Post.md")).isNotNull()
    }

    @Test
    fun `returns null for missing markdown`() {
        val markdownRoot = tempFolder.newFolder("markdown")
        val nodeCache = NodeCache(Parser.builder().build(), setOf(markdownRoot.canonicalFile))

        assertThat(nodeCache.getRelative("Missing.md")).isNull()
    }

    @Test
    fun `ignores markdown outside of its roots`() {
        val markdownRoot = tempFolder.newFolder("markdown")
        tempFolder.newFile("Outside.md").writeText("Hello")
        val nodeCache = NodeCache(Parser.builder().build(), setOf(markdownRoot.canonicalFile))

        assertThat(nodeCache.getRelative("../Outside.md")).isNull()
    }
}
