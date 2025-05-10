package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.testutils.flattenCssRules
import com.varabyte.kobweb.silk.testutils.toProperties
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.jetbrains.compose.web.css.*
import kotlin.test.AfterTest
import kotlin.test.Test

class SilkStylesheetTest {
    @AfterTest
    fun tearDown() {
        _SilkTheme = null
    }

    @Test
    fun canRegisterStylesheetStyles() {
        _SilkTheme = ImmutableSilkTheme(MutableSilkTheme())

        SilkStylesheetInstance.registerStyle("div") {
            base { Modifier.color(Colors.Red) }
            Breakpoint.MD { Modifier.color(Colors.Blue) }
        }

        SilkStylesheetInstance.layer("test-layer") {
            registerStyleBase("span") { Modifier.color(Colors.Green) }
            registerStyleBase("div") { Modifier.color(Colors.Cyan) }
        }

        val stylesheet = StyleSheet()
        SilkStylesheetInstance.registerStylesAndKeyframesInto(stylesheet)

        val rules = stylesheet.flattenCssRules()

        assertThat(rules.groupingBy { it.layer }.aggregate { _, accumulator: MutableSet<String>?, element, _ ->
            accumulator?.apply { add(element.header) } ?: mutableSetOf(element.header)
        } as Map<String?, Set<String>>)
            .containsExactly(
                null to setOf("div"),
                "test-layer" to setOf("span", "div"),
            )

        assertThat(rules.map { it.toString() })
            .containsExactly(
                "div { color:red; }",
                "@media (min-width: 48rem) { div { color:blue; } }",
                "@layer test-layer { span { color:green; } }",
                "@layer test-layer { div { color:cyan; } }"
            )
    }

    @Test
    fun attributeModifiersInStylesWillCauseAnAssertion() {
        _SilkTheme = ImmutableSilkTheme(MutableSilkTheme())

        SilkStylesheetInstance.registerStyleBase("div") {
            Modifier.id("invalid-modifier")
        }

        val stylesheet = StyleSheet()

        assertThrows<IllegalStateException> {
            SilkStylesheetInstance.registerStylesAndKeyframesInto(stylesheet)
        }.also { ex ->
            assertThat(ex.message!!.contains("invalid-modifier"))
        }
    }
}
