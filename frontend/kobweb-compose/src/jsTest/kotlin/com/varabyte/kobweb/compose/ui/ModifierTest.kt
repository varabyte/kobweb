package com.varabyte.kobweb.compose.ui

import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import kotlin.test.Test

class ModifierTest {
    @Test
    fun modifierEqualityWorks() {
        assertThat(Modifier.id("id")).isEqualTo(Modifier.id("id"))
        assertThat(Modifier.id("id1")).isNotEqualTo(Modifier.id("id2"))

        assertThat(Modifier.width(100.px)).isEqualTo(Modifier.width(100.px))
        assertThat(Modifier.width(100.px)).isNotEqualTo(Modifier.width(200.px))

        assertThat(Modifier.width(100.px).height(200.px)).isEqualTo(Modifier.width(100.px).height(200.px))
        // Attribute and style modifiers are both tested for equality
        assertThat(Modifier.id("id").width(100.px).height(200.px)).isEqualTo(Modifier.id("id").width(100.px).height(200.px))

        assertThat(Modifier.width(100.px)).isNotEqualTo(Modifier.width(100.px).height(200.px))
        assertThat(Modifier.id("id1").width(100.px).height(200.px)).isNotEqualTo(Modifier.id("id2").width(100.px).height(200.px))

        // Order is important
        // NOTE: In theory, we might revisit this later. Compose HTML doesn't care about ordering so maybe we shouldn't
        // require it for equality testing. However, in practice, it's easier to implement this way, and it's not likely
        // that users will pass in modifiers of one order in one recomposition step and then have the same properties in
        // a different order in a followup step.
        assertThat(Modifier.width(100.px).height(200.px)).isNotEqualTo(Modifier.height(200.px).width(100.px))
    }
}
