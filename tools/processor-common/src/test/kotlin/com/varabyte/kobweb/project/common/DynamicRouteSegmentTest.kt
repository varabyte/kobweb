package com.varabyte.kobweb.project.common

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class DynamicRouteSegmentTest {
    @Test
    fun parseDynamicSegments() {
        DynamicRouteSegment("{id}").apply {
            assertThat(name).isEqualTo("id")
            assertThat(isInferred).isFalse()
            assertThat(isCatchAll).isFalse()
            assertThat(isOptional).isFalse()
        }

        DynamicRouteSegment("{}").apply {
            assertThat(name).isEqualTo("")
            assertThat(isInferred).isTrue()
            assertThat(isCatchAll).isFalse()
            assertThat(isOptional).isFalse()
        }

        DynamicRouteSegment("{id?}").apply {
            assertThat(name).isEqualTo("id")
            assertThat(isCatchAll).isFalse()
            assertThat(isOptional).isTrue()
        }

        DynamicRouteSegment("{...rest}").apply {
            assertThat(name).isEqualTo("rest")
            assertThat(isCatchAll).isTrue()
            assertThat(isOptional).isFalse()
        }

        DynamicRouteSegment("{...rest?}").apply {
            assertThat(name).isEqualTo("rest")
            assertThat(isCatchAll).isTrue()
            assertThat(isOptional).isTrue()
        }

        DynamicRouteSegment("{...}").apply {
            assertThat(isInferred).isTrue()
            assertThat(isCatchAll).isTrue()
        }
    }

    @Test
    fun nonDynamicSegmentsAreRejected() {
        assertThrows<IllegalArgumentException> { DynamicRouteSegment("id") }
        assertThat(DynamicRouteSegment.tryCreate("id")).isNull()
        assertThat(DynamicRouteSegment.tryCreate("{id")).isNull()
        assertThat(DynamicRouteSegment.tryCreate("id}")).isNull()
        assertThat(DynamicRouteSegment.tryCreate("")).isNull()
    }

    @Test
    fun withReplacedNameKeepsModifiers() {
        assertThat(DynamicRouteSegment("{}").withReplacedName("id").rawValue).isEqualTo("{id}")
        assertThat(DynamicRouteSegment("{?}").withReplacedName("id").rawValue).isEqualTo("{id?}")
        assertThat(DynamicRouteSegment("{...}").withReplacedName("rest").rawValue).isEqualTo("{...rest}")
        assertThat(DynamicRouteSegment("{...?}").withReplacedName("rest").rawValue).isEqualTo("{...rest?}")
        assertThat(DynamicRouteSegment("{old}").withReplacedName("new").rawValue).isEqualTo("{new}")
    }
}
