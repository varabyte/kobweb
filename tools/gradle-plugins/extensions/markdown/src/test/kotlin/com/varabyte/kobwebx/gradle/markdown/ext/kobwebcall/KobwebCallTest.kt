@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import com.varabyte.kobwebx.gradle.markdown.MarkdownRenderFixture
import com.varabyte.kobwebx.gradle.markdown.assertContainsKotlin
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class KobwebCallTest {
    @get:Rule
    val fixture = MarkdownRenderFixture()

    @Test
    fun `block call adds parentheses when missing`() {
        val output = fixture.renderMarkdown(
            "{{{ .components.widgets.Counter }}}",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.example.components.widgets.Counter()
            }
            """
        )
    }

    @Test
    fun `block call keeps its arguments`() {
        val output = fixture.renderMarkdown(
            """{{{ .components.widgets.Video(src = "intro.mp4") }}}""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.example.components.widgets.Video(src = "intro.mp4")
            }
            """
        )
    }

    @Test
    fun `block call wraps the markdown inside it`() {
        val output = fixture.renderMarkdown(
            """
            {{{ .components.layouts.Card

            Inside a *card*

            }}}
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.example.components.layouts.Card {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Inside a ")
                        org.jetbrains.compose.web.dom.Em {
                            org.jetbrains.compose.web.dom.Text("card")
                        }
                    }
                }
            }
            """
        )
    }

    @Test
    fun `block calls can nest`() {
        val output = fixture.renderMarkdown(
            """
            {{{ .components.layouts.Outer

            {{{ .components.layouts.Inner

            Deep

            }}}

            }}}
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.example.components.layouts.Outer {
                    org.example.components.layouts.Inner {
                        org.jetbrains.compose.web.dom.P {
                            org.jetbrains.compose.web.dom.Text("Deep")
                        }
                    }
                }
            }
            """
        )
    }

    @Test
    fun `inline call sits between surrounding text`() {
        val output = fixture.renderMarkdown(
            $$"""Hello ${.components.widgets.Fancy("World")}, welcome!""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello ")
                    org.example.components.widgets.Fancy("World")
                    org.jetbrains.compose.web.dom.Text(", welcome!")
                }
            }
            """
        )
    }

    @Test
    fun `inline call adds parentheses when missing`() {
        val output = fixture.renderMarkdown(
            $$"""${.components.widgets.Counter} clicks""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.example.components.widgets.Counter()
                    org.jetbrains.compose.web.dom.Text(" clicks")
                }
            }
            """
        )
    }

    @Test
    fun `leaves braces without a dollar sign as text`() {
        val output = fixture.renderMarkdown(
            "Plain {braces} stay",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Plain {braces} stay")
                }
            }
            """
        )
    }

    @Test
    fun `block call delimiters can be changed`() {
        val output = fixture.renderMarkdown(
            "[[[ .components.widgets.Counter ]]]",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureFeatures = { kobwebCallDelimiters.set('[' to ']') },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.example.components.widgets.Counter()
            }
            """
        )
    }

    @Test
    fun `calls can be turned off`() {
        val output = fixture.renderMarkdown(
            "{{{ .components.widgets.Counter }}}",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureFeatures = { kobwebCall.set(false) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("{{{ .components.widgets.Counter }}}")
                }
            }
            """
        )
    }

    @Test
    fun `toFqn leaves fully qualified names alone`() {
        assertThat(KobwebCall("com.example.Widget").toFqn("org.example")).isEqualTo("com.example.Widget()")
    }

    @Test
    fun `toFqn adds parentheses after type arguments`() {
        assertThat(KobwebCall(".widgets.Picker<String>").toFqn("org.example")).isEqualTo("org.example.widgets.Picker<String>()")
    }

    @Test
    fun `toFqn opens a block without adding parentheses`() {
        assertThat(KobwebCall(".layouts.Card", appendBrace = true).toFqn("org.example")).isEqualTo("org.example.layouts.Card {")
    }
}
