@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Rule
import org.junit.Test

// Links to other Markdown files are rewritten into the routes of the pages generated from them.
class MarkdownLinkTest {
    @get:Rule
    val fixture = MarkdownRenderFixture()

    @Test
    fun `links to a sibling markdown page by its route`() {
        fixture.addMarkdownFile("Hello", filePath = "blog/Post.md", route = "/blog/post")
        val output = fixture.renderMarkdown(
            "[Read](Post.md)",
            filePath = "blog/Index.md",
            route = "/blog/",
            funName = "IndexPage",
        )

        output.assertContainsKotlin(
            """
            fun IndexPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("/blog/post") {
                        org.jetbrains.compose.web.dom.Text("Read")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `links to a markdown page in a parent folder`() {
        fixture.addMarkdownFile("Hello", filePath = "About.md", route = "/about")
        val output = fixture.renderMarkdown(
            "[About](../About.md)",
            filePath = "blog/Index.md",
            route = "/blog/",
            funName = "IndexPage",
        )

        output.assertContainsKotlin(
            """
            fun IndexPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("/about") {
                        org.jetbrains.compose.web.dom.Text("About")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `respects the target's relative route override`() {
        fixture.addMarkdownFile(
            """
            ---
            routeOverride: first-post
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
        )
        val output = fixture.renderMarkdown(
            "[Read](Post.md)",
            filePath = "blog/Index.md",
            route = "/blog/",
            funName = "IndexPage",
        )

        output.assertContainsKotlin(
            """
            fun IndexPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("/blog/first-post") {
                        org.jetbrains.compose.web.dom.Text("Read")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `respects the target's absolute route override`() {
        fixture.addMarkdownFile(
            """
            ---
            routeOverride: /articles/first-post
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
        )
        val output = fixture.renderMarkdown(
            "[Read](Post.md)",
            filePath = "blog/Index.md",
            route = "/blog/",
            funName = "IndexPage",
        )

        output.assertContainsKotlin(
            """
            fun IndexPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("/articles/first-post") {
                        org.jetbrains.compose.web.dom.Text("Read")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `leaves external markdown links alone`() {
        val output = fixture.renderMarkdown(
            "[Readme](https://example.com/README.md)",
            filePath = "blog/Index.md",
            route = "/blog/",
            funName = "IndexPage",
        )

        output.assertContainsKotlin(
            """
            fun IndexPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("https://example.com/README.md") {
                        org.jetbrains.compose.web.dom.Text("Readme")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `leaves links to missing markdown files alone`() {
        val output = fixture.renderMarkdown(
            "[Missing](Missing.md)",
            filePath = "blog/Index.md",
            route = "/blog/",
            funName = "IndexPage",
        )

        output.assertContainsKotlin(
            """
            fun IndexPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("Missing.md") {
                        org.jetbrains.compose.web.dom.Text("Missing")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `fails on a link to markdown that is not a page`() {
        fixture.addMarkdownFile("Hello", filePath = "blog/Widget.md", route = null)

        val e = assertThrows<IllegalStateException> {
            fixture.renderMarkdown(
                "[Widget](Widget.md)",
                filePath = "blog/Index.md",
                route = "/blog/",
                funName = "IndexPage",
            )
        }

        assertThat(e.message).isEqualTo(
            "Markdown file link 'Widget.md' in 'blog/Index.md' links to a Markdown file which is not a page (its package is not under the pages package), so it has no route to link to."
        )
    }

    @Test
    fun `fails on a link to a page with a dynamic route`() {
        fixture.addMarkdownFile(
            """
            ---
            routeOverride: /posts/{id}
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
        )

        val e = assertThrows<IllegalStateException> {
            fixture.renderMarkdown(
                "[Read](Post.md)",
                filePath = "blog/Index.md",
                route = "/blog/",
                funName = "IndexPage",
            )
        }

        assertThat(e.message).isEqualTo(
            "Markdown file link 'Post.md' links to file with dynamic route override. This is not supported!"
        )
    }
}
