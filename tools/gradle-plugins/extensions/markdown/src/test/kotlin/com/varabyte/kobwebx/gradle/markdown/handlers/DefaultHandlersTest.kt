@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.handlers

import com.varabyte.kobwebx.gradle.markdown.MarkdownRenderFixture
import com.varabyte.kobwebx.gradle.markdown.TRIPLE_QUOTE
import com.varabyte.kobwebx.gradle.markdown.assertContainsKotlin
import org.junit.Test

class DefaultHandlersTest {
    private val fixture = MarkdownRenderFixture()

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

    @Test
    fun `unwraps paragraphs only in tight lists`() {
        val tightOutput = fixture.renderMarkdown(
            """
            - One
            - Two
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )
        val looseOutput = fixture.renderMarkdown(
            """
            - One

            - Two
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        tightOutput.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Ul {
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
        looseOutput.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Ul {
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.P {
                            org.jetbrains.compose.web.dom.Text("One")
                        }
                    }
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.P {
                            org.jetbrains.compose.web.dom.Text("Two")
                        }
                    }
                }
            }
            """
        )
    }
}
