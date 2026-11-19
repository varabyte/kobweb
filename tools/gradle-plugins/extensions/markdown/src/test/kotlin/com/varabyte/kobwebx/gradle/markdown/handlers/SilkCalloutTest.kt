@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.handlers

import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobwebx.gradle.markdown.MarkdownRenderFixture
import com.varabyte.kobwebx.gradle.markdown.assertContainsKotlin
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

// Handler under tests: blockquote, which delegates to SilkCalloutBlockquoteHandler when Silk is available.
class SilkCalloutTest {
    @get:Rule
    val fixture = MarkdownRenderFixture()

    @Test
    fun `renders callout from type marker`() {
        val output = fixture.renderMarkdown(
            """
            > [!NOTE]
            > This is a note.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.NOTE, label = null, variant = com.varabyte.kobweb.silk.components.display.CalloutDefaults.Variant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("This is a note.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `callout text can start on the marker line`() {
        val output = fixture.renderMarkdown(
            "> [!NOTE] This is a note.",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.NOTE, label = null, variant = com.varabyte.kobweb.silk.components.display.CalloutDefaults.Variant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("This is a note.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `callout text can start with formatting`() {
        val output = fixture.renderMarkdown(
            """
            > [!NOTE]
            > *Important* note.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.NOTE, label = null, variant = com.varabyte.kobweb.silk.components.display.CalloutDefaults.Variant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Em {
                            org.jetbrains.compose.web.dom.Text("Important")
                        }
                        org.jetbrains.compose.web.dom.Text(" note.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `marker can set the label`() {
        val output = fixture.renderMarkdown(
            """
            > [!TIP "Pro tip"]
            > Use it.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.TIP, label = "Pro tip", variant = com.varabyte.kobweb.silk.components.display.CalloutDefaults.Variant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Use it.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `marker can set the variant`() {
        val output = fixture.renderMarkdown(
            """
            > [!WARNING {variant = com.example.OutlinedVariant}]
            > Careful.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.WARNING, label = null, variant = com.example.OutlinedVariant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Careful.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `marker variant can be a shortcut`() {
        val output = fixture.renderMarkdown(
            """
            > [!WARNING {variant = .components.widgets.OutlinedVariant}]
            > Careful.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.WARNING, label = null, variant = org.example.components.widgets.OutlinedVariant) {
            """
        )
    }

    @Test
    fun `ignores malformed marker options`() {
        val output = fixture.renderMarkdown(
            """
            > [!WARNING {variant}]
            > Careful.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.WARNING, label = null, variant = com.varabyte.kobweb.silk.components.display.CalloutDefaults.Variant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Careful.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `warns about unknown callout type and falls back to blockquote`() {
        val warnings = mutableListOf<String>()
        val output = fixture.renderMarkdown(
            """
            > [!BOGUS]
            > Unknown.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
            reporter = object : Reporter {
                override fun error(message: String) = throw AssertionError(message)
                override fun warn(message: String) {
                    warnings.add(message)
                }
            },
        )

        assertThat(warnings).containsExactly("Unknown Markdown callout type: BOGUS")
        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag("blockquote") {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("[!BOGUS] Unknown.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `renders plain blockquote when there is no marker`() {
        val output = fixture.renderMarkdown(
            "> Just a quote.",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { useSilk.set(true) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag("blockquote") {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Just a quote.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `leaves markers as text without Silk`() {
        val output = fixture.renderMarkdown(
            """
            > [!NOTE]
            > No Silk here.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.compose.dom.GenericTag("blockquote") {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("[!NOTE] No Silk here.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `supports custom callout types and default variant`() {
        val output = fixture.renderMarkdown(
            """
            > [!CUSTOM]
            > Custom.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = {
                useSilk.set(true)
                blockquote.set(
                    SilkCalloutBlockquoteHandler(
                        types = SilkCalloutTypes + mapOf("CUSTOM" to ".components.widgets.CustomCalloutType"),
                        variant = "org.example.components.widgets.CustomVariant",
                    )
                )
            },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = org.example.components.widgets.CustomCalloutType, label = null, variant = org.example.components.widgets.CustomVariant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Custom.")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `default variant can be a shortcut`() {
        val output = fixture.renderMarkdown(
            """
            > [!NOTE]
            > Note.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = {
                useSilk.set(true)
                blockquote.set(SilkCalloutBlockquoteHandler(variant = ".components.widgets.CustomVariant"))
            },
        )

        output.assertContainsKotlin(
            """
            com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.NOTE, label = null, variant = org.example.components.widgets.CustomVariant) {
            """
        )
    }

    @Test
    fun `supports custom default labels`() {
        val output = fixture.renderMarkdown(
            """
            > [!QUOTE]
            > Quoted.
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = {
                useSilk.set(true)
                blockquote.set(SilkCalloutBlockquoteHandler(labels = mapOf("QUOTE" to "")))
            },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                com.varabyte.kobweb.silk.components.display.Callout(type = com.varabyte.kobweb.silk.components.display.CalloutType.QUOTE, label = "", variant = com.varabyte.kobweb.silk.components.display.CalloutDefaults.Variant) {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Quoted.")
                    }
                }
            }
            """
        )
    }
}
