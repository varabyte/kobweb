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

        // Order is important (for CSS properties)
        // Setting a longhand property after a shorthand property only overrides the specific longhand, but setting a
        // shorthand property after a longhand property overrides all the longhand properties.
        assertThat(Modifier.margin { top(50.px) }.margin(10.px)).isNotEqualTo(
            Modifier.margin(10.px).margin { top(50.px) })
        // A property may be set multiple times, in which case all values must match for equality to hold.
        // This is important as CSS may apply an earlier value if a later value is invalid.
        assertThat(Modifier.width(100.px).width(50.px)).isEqualTo(Modifier.width(100.px).width(50.px))
        assertThat(Modifier.width(100.px).width(50.px)).isNotEqualTo(Modifier.width(25.px).width(50.px))
        // For many properties, CSS does not care about order, but for consistency our implementation always does.
        assertThat(Modifier.width(100.px).height(200.px)).isNotEqualTo(Modifier.height(200.px).width(100.px))

        // Order is important (for HTML attributes)
        // NOTE: In theory, we might revisit this later (ONLY for attributes, NOT for CSS properties). HTML doesn't care
        // about attribute ordering, so maybe we shouldn't require it for equality testing. However, in practice, it's
        // easier to implement this way, and it's not likely that users will pass in modifiers of one order in one
        // recomposition step and then have the same attributes in a different order in a followup step.
        assertThat(Modifier.id("id").title("title")).isNotEqualTo(Modifier.title("title").id("id"))
    }
}
