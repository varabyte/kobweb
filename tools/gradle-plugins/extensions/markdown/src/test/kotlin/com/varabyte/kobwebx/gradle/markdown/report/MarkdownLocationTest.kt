@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.report

import com.varabyte.truthish.assertThat
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

class MarkdownLocationTest {
    @Test
    fun `formats location as an absolute file uri`() {
        val location = MarkdownLocation(
            File("/site/markdown/blog/Post.md"),
            Path("blog/Post.md"),
            MarkdownLocation.Position(line = 12, column = 9),
        )

        assertThat(location.toAbsoluteString()).isEqualTo("file:///site/markdown/blog/Post.md:12:9")
    }

    @Test
    fun `formats location relative to its markdown root`() {
        val location = MarkdownLocation(
            File("/site/markdown/blog/Post.md"),
            Path("blog/Post.md"),
            MarkdownLocation.Position(line = 12, column = 9),
        )

        assertThat(location.toRelativeString()).isEqualTo("blog/Post.md:12:9")
    }

    @Test
    fun `formats location without a position`() {
        val location = MarkdownLocation(File("/site/markdown/blog/Post.md"), Path("blog/Post.md"))

        assertThat(location.toAbsoluteString()).isEqualTo("file:///site/markdown/blog/Post.md")
        assertThat(location.toRelativeString()).isEqualTo("blog/Post.md")
    }
}
