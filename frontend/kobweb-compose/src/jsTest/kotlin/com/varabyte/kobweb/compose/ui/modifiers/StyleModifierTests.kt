package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toStyles
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import kotlin.test.Test

class StyleModifierTests {
    // Convert all properties added by all modifier styles to the String that would ultimately get put into an HTML
    // style attribute. In other words, key / values will be split by a ':' and multiple properties by a ';'
    private fun modifierToText(produceModifier: () -> Modifier): String {
        // We don't care about comparing -- but it's an easy way to construct a style scope, as Compose HTML doesn't
        // give us an easy way otherwise.
        val styleScope = ComparableStyleScope()
        val modifier = produceModifier()
        modifier.toStyles().invoke(styleScope)

        return styleScope.properties.entries.joinToString("; ") { (key, value) -> "$key: $value" }
    }

    @Test
    fun verifyMarginInline() {
        assertThat(modifierToText { Modifier.marginInline(both = 10.px) }).isEqualTo("margin-inline: 10px")
        assertThat(modifierToText {
            Modifier.marginInline(start = 10.px, end = 20.px)
        }).isEqualTo("margin-inline: 10px 20px")
        assertThat(modifierToText { Modifier.marginInline(end = 20.px) }).isEqualTo("margin-inline: 0px 20px")

        assertThat(modifierToText {
            Modifier.marginInline {
                start(10.px)
                end(20.px)
            }
        }).isEqualTo("margin-inline-start: 10px; margin-inline-end: 20px")

        assertThat(modifierToText {
            Modifier.marginInline {
                end(20.px)
            }
        }).isEqualTo("margin-inline-end: 20px")
    }
}