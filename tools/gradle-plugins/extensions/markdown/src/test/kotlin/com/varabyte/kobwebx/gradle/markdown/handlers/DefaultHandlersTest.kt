@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.handlers

import com.varabyte.kobwebx.gradle.markdown.MarkdownRenderFixture
import com.varabyte.kobwebx.gradle.markdown.TRIPLE_QUOTE
import com.varabyte.kobwebx.gradle.markdown.assertContainsKotlin
import org.junit.Rule
import org.junit.Test

class DefaultHandlersTest {
    @get:Rule
    val fixture = MarkdownRenderFixture()

    // Handler under test: text
    @Test
    fun `escapes quotes, backslashes and dollars in text`() {
        val output = fixture.renderMarkdown(
            """Say "hi" to C:\Users and pay $5""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Say \"hi\" to C:\\Users and pay ${'$'}5")
                }
            }
            """
        )
    }

    // Handler under test: heading
    @Test
    fun `gives each heading a unique id`() {
        val output = fixture.renderMarkdown(
            """
            # Getting Started

            ## Setup

            ## Setup
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.H1(attrs = { id("getting-started") }) {
                    org.jetbrains.compose.web.dom.Text("Getting Started")
                }
                org.jetbrains.compose.web.dom.H2(attrs = { id("setup") }) {
                    org.jetbrains.compose.web.dom.Text("Setup")
                }
                org.jetbrains.compose.web.dom.H2(attrs = { id("setup-2") }) {
                    org.jetbrains.compose.web.dom.Text("Setup")
                }
            }
            """
        )
    }

    // Handler under test: heading
    @Test
    fun `heading ids include text from inline formatting`() {
        val output = fixture.renderMarkdown(
            "# Using `Modifier`s",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            org.jetbrains.compose.web.dom.H1(attrs = { id("using-modifiers") }) {
            """
        )
    }

    // Handler under test: heading
    @Test
    fun `heading ids can be turned off`() {
        val output = fixture.renderMarkdown(
            "# Getting Started",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { generateHeaderIds.set(false) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.H1 {
                    org.jetbrains.compose.web.dom.Text("Getting Started")
                }
            }
            """
        )
    }

    // Handler under test: heading
    @Test
    fun `heading ids can use a custom generator`() {
        val output = fixture.renderMarkdown(
            "# Getting Started",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { idGenerator.set { text -> text.uppercase().replace(' ', '_') } },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.H1(attrs = { id("GETTING_STARTED") }) {
                    org.jetbrains.compose.web.dom.Text("Getting Started")
                }
            }
            """
        )
    }

    // Handler under test: a
    @Test
    fun `renders links with Silk only when it is available`() {
        fun renderLink(useSilk: Boolean) = fixture.renderMarkdown(
            "[Kobweb](https://kobweb.varabyte.com)",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { this.useSilk.set(useSilk) },
        )

        renderLink(useSilk = true).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    com.varabyte.kobweb.silk.components.navigation.Link("https://kobweb.varabyte.com") {
                        org.jetbrains.compose.web.dom.Text("Kobweb")
                    }
                }
            }
            """
        )
        renderLink(useSilk = false).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("https://kobweb.varabyte.com") {
                        org.jetbrains.compose.web.dom.Text("Kobweb")
                    }
                }
            }
            """
        )
    }

    // Handler under test: code
    @Test
    fun `renders code block as raw string`() {
        val output = fixture.renderMarkdown(
            $$"""
            ```kotlin
            val price = "$5"
            val quotes = $${TRIPLE_QUOTE}
            ```
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                org.jetbrains.compose.web.dom.Pre { org.jetbrains.compose.web.dom.Code { org.jetbrains.compose.web.dom.Text($${TRIPLE_QUOTE}val price = "${'$'}5"
            val quotes = ${"\"\"\""}
            $${TRIPLE_QUOTE}) } }
            }
            """
        )
    }

    // Handlers under test: em, strong
    @Test
    fun `renders emphasis and strong text`() {
        val output = fixture.renderMarkdown(
            "*soft* and **loud**",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Em {
                        org.jetbrains.compose.web.dom.Text("soft")
                    }
                    org.jetbrains.compose.web.dom.Text(" and ")
                    org.jetbrains.compose.web.dom.B {
                        org.jetbrains.compose.web.dom.Text("loud")
                    }
                }
            }
            """
        )
    }

    // Handler under test: inlineCode
    @Test
    fun `escapes inline code like text`() {
        val output = fixture.renderMarkdown(
            $$"""Run `echo "$HOME"` now""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Run ")
                    org.jetbrains.compose.web.dom.Code {
                        org.jetbrains.compose.web.dom.Text("echo \"${'$'}HOME\"")
                    }
                    org.jetbrains.compose.web.dom.Text(" now")
                }
            }
            """
        )
    }

    // Handler under test: br
    @Test
    fun `renders hard line break`() {
        val output = fixture.renderMarkdown(
            """
            one\
            two
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("one")
                    org.jetbrains.compose.web.dom.Br()
                    org.jetbrains.compose.web.dom.Text("two")
                }
            }
            """
        )
    }

    // Handler under test: hr
    @Test
    fun `renders horizontal rule with Silk only when it is available`() {
        fun renderRule(useSilk: Boolean) = fixture.renderMarkdown(
            "***",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { this.useSilk.set(useSilk) },
        )

        renderRule(useSilk = true).assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.layout.HorizontalDivider()
            }
            """
        )
        renderRule(useSilk = false).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Hr()
            }
            """
        )
    }

    // Handler under test: img
    @Test
    fun `renders images with Silk only when it is available`() {
        fun renderImage(useSilk: Boolean) = fixture.renderMarkdown(
            "![A cat](cat.png)",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { this.useSilk.set(useSilk) },
        )

        renderImage(useSilk = true).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    com.varabyte.kobweb.silk.components.graphics.Image("cat.png", "A cat")
                }
            }
            """
        )
        renderImage(useSilk = false).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Img("cat.png", "A cat")
                }
            }
            """
        )
    }

    // Handlers under test: ol, li
    @Test
    fun `renders ordered list`() {
        val output = fixture.renderMarkdown(
            """
            1. One
            2. Two
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Ol {
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.Text("One")
                    }
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.Text("Two")
                    }
                }
            }
            """
        )
    }

    // Handlers under test: table, thead, tbody, tr, th, td
    @Test
    fun `aligns table cells`() {
        val output = fixture.renderMarkdown(
            """
            | Left | Center | Right | None |
            |:-----|:------:|------:|------|
            | a    | b      | c     | d    |
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Table {
                    org.jetbrains.compose.web.dom.Thead {
                        org.jetbrains.compose.web.dom.Tr {
                            org.jetbrains.compose.web.dom.Th(attrs = { style {property("text-align", "left")} }) {
                                org.jetbrains.compose.web.dom.Text("Left")
                            }
                            org.jetbrains.compose.web.dom.Th(attrs = { style {property("text-align", "center")} }) {
                                org.jetbrains.compose.web.dom.Text("Center")
                            }
                            org.jetbrains.compose.web.dom.Th(attrs = { style {property("text-align", "right")} }) {
                                org.jetbrains.compose.web.dom.Text("Right")
                            }
                            org.jetbrains.compose.web.dom.Th {
                                org.jetbrains.compose.web.dom.Text("None")
                            }
                        }
                    }
                    org.jetbrains.compose.web.dom.Tbody {
                        org.jetbrains.compose.web.dom.Tr {
                            org.jetbrains.compose.web.dom.Td(attrs = { style {property("text-align", "left")} }) {
                                org.jetbrains.compose.web.dom.Text("a")
                            }
                            org.jetbrains.compose.web.dom.Td(attrs = { style {property("text-align", "center")} }) {
                                org.jetbrains.compose.web.dom.Text("b")
                            }
                            org.jetbrains.compose.web.dom.Td(attrs = { style {property("text-align", "right")} }) {
                                org.jetbrains.compose.web.dom.Text("c")
                            }
                            org.jetbrains.compose.web.dom.Td {
                                org.jetbrains.compose.web.dom.Text("d")
                            }
                        }
                    }
                }
            }
            """
        )
    }

    // Handler under test: hr, replaced by the user
    @Test
    fun `user can override a handler`() {
        val output = fixture.renderMarkdown(
            "***",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { hr.set { "org.example.components.widgets.FancyRule" } },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.example.components.widgets.FancyRule()
            }
            """
        )
    }
}
