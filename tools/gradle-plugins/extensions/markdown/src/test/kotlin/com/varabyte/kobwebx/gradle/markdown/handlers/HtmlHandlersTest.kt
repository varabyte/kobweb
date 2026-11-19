@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.handlers

import com.varabyte.kobwebx.gradle.markdown.MarkdownRenderFixture
import com.varabyte.kobwebx.gradle.markdown.TRIPLE_QUOTE
import com.varabyte.kobwebx.gradle.markdown.assertContainsKotlin
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class HtmlHandlersTest {
    @get:Rule
    val fixture = MarkdownRenderFixture()

    // Handler under test: html
    @Test
    fun `renders html block with nested elements and attributes`() {
        val output = fixture.renderMarkdown(
            """
            <div class="note">
            <p>Hi <b>there</b></p>
            </div>
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("div", attrs = {attr("class", "note")}) {
                    com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("p") {
                        org.jetbrains.compose.web.dom.Text($${TRIPLE_QUOTE}Hi $${TRIPLE_QUOTE})
                        com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("b") {
                            org.jetbrains.compose.web.dom.Text($${TRIPLE_QUOTE}there$${TRIPLE_QUOTE})
                        }
                    }
                }
            }
            """
        )
    }

    // Handler under test: html
    @Test
    fun `escapes text inside html block and drops its leading newline`() {
        val output = fixture.renderMarkdown(
            $$"""
            <pre>
            price: $5 $${TRIPLE_QUOTE}
            </pre>
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("pre") {
                    org.jetbrains.compose.web.dom.Text($${TRIPLE_QUOTE}price: ${'$'}5 ${"\"\"\""}
            $${TRIPLE_QUOTE})
                }
            }
            """
        )
    }

    // Handler under test: html
    @Test
    fun `renders html block element without children`() {
        val output = fixture.renderMarkdown(
            """<img src="a.png">""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("img", attrs = {attr("src", "a.png")})
            }
            """
        )
    }

    // Handler under test: html
    @Test
    fun `renders sibling html block elements on separate lines`() {
        val output = fixture.renderMarkdown(
            """<div></div><div></div>""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("div")
                com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("div")
            }
            """
        )
    }

    // Handler under test: html
    @Test
    fun `escapes html attribute values`() {
        val output = fixture.renderMarkdown(
            $$"""<div title='say "hi" $5'></div>""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("div", attrs = {attr("title", "say \"hi\" ${'$'}5")})
            }
            """
        )
    }

    // Handlers under test: inlineTag, rawTag
    @Test
    fun `renders inline html tag around its content`() {
        val output = fixture.renderMarkdown(
            """Hello <span id="x">there</span>!""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello ")
                    com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("span", attrs = {attr("id", "x")}) {
                        org.jetbrains.compose.web.dom.Text("there")
                    }
                    org.jetbrains.compose.web.dom.Text("!")
                }
            }
            """
        )
    }

    // Handlers under test: inlineTag, rawTag
    @Test
    fun `renders inline void and self-closing tags without content`() {
        val output = fixture.renderMarkdown(
            """a<br>b<img src="a.png"/>c""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("a")
                    com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("br", attrs = {})
                    org.jetbrains.compose.web.dom.Text("b")
                    com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("img", attrs = {attr("src", "a.png")})
                    org.jetbrains.compose.web.dom.Text("c")
                }
            }
            """
        )
    }

    // Handlers under test: inlineTag, rawTag
    @Test
    fun `renders inline void tags with attributes without content`() {
        val output = fixture.renderMarkdown(
            """a<img src="a.png">b<input type="checkbox">c""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("a")
                    com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("img", attrs = {attr("src", "a.png")})
                    org.jetbrains.compose.web.dom.Text("b")
                    com.varabyte.kobweb.compose.dom.GenericTag<org.w3c.dom.Element>("input", attrs = {attr("type", "checkbox")})
                    org.jetbrains.compose.web.dom.Text("c")
                }
            }
            """
        )
    }

    // Handler under test: rawTag
    @Test
    fun `raw tag handler receives the text inside each opening tag`() {
        val rawTags = mutableListOf<String>()
        fixture.renderMarkdown(
            """Hello <span id="x">there</span><br><img src="a.png" />!""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = {
                rawTag.set { tag ->
                    rawTags.add(tag)
                    "Tag()"
                }
            },
        )

        assertThat(rawTags).containsExactly("""span id="x"""", "br", """img src="a.png"""").inOrder()
    }
}
