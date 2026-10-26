package com.varabyte.kobweb.project.common

import com.varabyte.truthish.assertThat
import org.junit.Test

class RouteSegmentsTest {
    @Test
    fun toRouteSegment() {
        assertThat("blog".toRouteSegment()).isEqualTo("blog")
        assertThat("ourTeam".toRouteSegment()).isEqualTo("our-team")
        assertThat("OurTeam".toRouteSegment()).isEqualTo("our-team")
        assertThat("our_team".toRouteSegment()).isEqualTo("our-team")
        assertThat("our__team".toRouteSegment()).isEqualTo("our-team")
        assertThat("our-team".toRouteSegment()).isEqualTo("our-team")
        assertThat("our--team".toRouteSegment()).isEqualTo("our-team")
    }

    @Test
    fun leadingAndTrailingSeparatorsAreRemoved() {
        assertThat("_2021".toRouteSegment()).isEqualTo("2021")
        assertThat("fun_".toRouteSegment()).isEqualTo("fun")
        assertThat("_draft".toRouteSegment()).isEqualTo("draft")
        assertThat("draft_".toRouteSegment()).isEqualTo("draft")
        assertThat("-draft-".toRouteSegment()).isEqualTo("draft")
        assertThat("draft_-".toRouteSegment()).isEqualTo("draft")
        assertThat("___forced__--example_--_".toRouteSegment()).isEqualTo("forced-example")
    }
}
