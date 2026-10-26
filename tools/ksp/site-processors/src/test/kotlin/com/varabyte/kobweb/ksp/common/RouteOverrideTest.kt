package com.varabyte.kobweb.ksp.common

import com.varabyte.truthish.assertThat
import org.junit.Test

class RouteOverrideTest {
    @Test
    fun validOverridesHaveNoProblems() {
        listOf(
            "other",
            "AboutUs",
            "index",
            "/",
            "/d/e/f/",
            "d/e/f/other",
            "{}",
            "/d/{id}/f/{}",
            "{?}",
            "{...}",
            "{...?}",
            "{...rest}",
            "{...rest?}",
            "{id?}",
            "{user-id}",
        ).forEach { value ->
            assertThat(RouteOverride(value).findProblems()).isEmpty()
        }
    }

    @Test
    fun malformedDynamicSegmentsAreReported() {
        listOf(
            "{post",
            "post}",
            "a{b}c",
            "{a}{b}",
            "d/{id/e",
            "{a b}",
            "{a?b}",
            "{??}",
            // Dots in the wrong place or the wrong number of them
            "{rest...}",
            "{rest...?}",
            "{.....rest}",
            "{..rest}",
            "{......}",
            "{.rest}",
            "{...rest...}",
            "{.}",
            "{a.b}",
            // Characters that aren't allowed in names
            "{{a}}",
            "{...{a}}",
            "{a#b}",
        ).forEach { value ->
            assertThat(RouteOverride(value).findProblems()).hasSize(1)
        }
    }

    @Test
    fun emptySegmentsAreReported() {
        listOf("a//b", "//", "/a//", "//a").forEach { value ->
            assertThat(RouteOverride(value).findProblems()).hasSize(1)
        }
    }

    @Test
    fun nonPathCharactersAreReported() {
        listOf("posts?draft", "about#team", "about us").forEach { value ->
            assertThat(RouteOverride(value).findProblems()).hasSize(1)
        }
    }

    @Test
    fun eachProblemIsReported() {
        assertThat(RouteOverride("a//{b/c?d").findProblems()).hasSize(3)
    }
}
