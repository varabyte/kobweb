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
            "{user_id}",
            "{userId2}",
            // Dynamic segment names never appear in the URL, so they aren't limited to ASCII
            "{café}",
        ).forEach { value ->
            assertThat(RouteOverride(value).findProblems()).isEmpty()
        }
    }

    @Test
    fun strayCurlyBracesAreReported() {
        listOf("{post", "post}", "a{b}c", "d/{id/e").forEach { value ->
            assertThat(RouteOverride(value).findProblems()).hasSize(1)
        }
    }

    @Test
    fun malformedDynamicSegmentsAreReported() {
        listOf(
            "{a}{b}",
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
            "{a+b}",
            "{a:b}",
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
    fun dotSegmentsAreReported() {
        listOf(".", "..", "a/./b", "a/../b", "./b", "a/..", "a/../", "%2e", "%2E%2e", ".%2E", "%2e.").forEach { value ->
            assertThat(RouteOverride(value).findProblems()).hasSize(1)
        }
    }

    @Test
    fun segmentsWithDotsThatArentDotSegmentsAreAllowed() {
        listOf("...", ".a", "a.", "a.b", "%2e%2e%2e", ".well-known").forEach { value ->
            assertThat(RouteOverride(value).findProblems()).isEmpty()
        }
    }

    @Test
    fun nonPathCharactersAreReported() {
        listOf(
            "posts?draft",
            "about#team",
            "about us",
            "/d/about us/",
            "about\tus",
            "a<b>",
            "a\"b",
            "a`b",
            // Browsers treat a backslash as a slash in http and https URLs
            "a\\b",
            "café",
            "😀",
        ).forEach { value ->
            assertThat(RouteOverride(value).findProblems()).hasSize(1)
        }
    }

    @Test
    fun percentEncodedCharactersAreAllowed() {
        listOf("about%20us", "%7Bx%7D", "caf%C3%A9", "a-b_c.d~e!$&'()*+,;=:@").forEach { value ->
            assertThat(RouteOverride(value).findProblems()).isEmpty()
        }
    }

    @Test
    fun invalidCharactersSuggestTheirEncoding() {
        val problem = RouteOverride("about usé😀").findProblems().single()
        assertThat(problem).contains("a space (`%20`)")
        assertThat(problem).contains("`é` (`%C3%A9`)")
        assertThat(problem).contains("(`%F0%9F%98%80`)")

        assertThat(RouteOverride("a\tb").findProblems().single()).contains("U+0009 (`%09`)")
        assertThat(RouteOverride("a\\b").findProblems().single()).contains("`\\` (`%5C`)")
    }

    @Test
    fun eachProblemIsReported() {
        // Empty segment, `{b`, `c?d`, `e f`, `{a.b}`
        assertThat(RouteOverride("a//{b/c?d/e f/{a.b}").findProblems()).hasSize(5)

        // Stray braces and other invalid characters in a single segment are reported separately
        assertThat(RouteOverride("{a} b").findProblems()).hasSize(2)
    }
}
