package com.varabyte.kobweb.navigation

import com.varabyte.truthish.assertThat
import kotlin.test.Test

class RouteTest {
    @Test
    fun testPartition() {
        val beforeAfter = mutableMapOf(
            "path" to ("path" to ""),
            "path?key=value" to ("path" to "?key=value"),
            "path#frag" to ("path" to "#frag"),
            "path?key=value#frag" to ("path" to "?key=value#frag"),
            "path#frag?key=value" to ("path" to "#frag?key=value"),
        )

        beforeAfter.forEach { (before, after) ->
            assertThat(Route.partition(before)).isEqualTo(after)
        }
    }

    @Test
    fun testNormalizeSlashes() {
        val beforeAfter = mutableMapOf(
            // Valid cases
            "https://example.com" to "https://example.com",
            "https://example.com/a/b/c" to "https://example.com/a/b/c",
            "https://example.com/a/b/c?key=value#frag" to "https://example.com/a/b/c?key=value#frag",
            "https://example.com/a/b/c#frag?key=value" to "https://example.com/a/b/c#frag?key=value",
            "/a/b/c?key=value#frag" to "/a/b/c?key=value#frag",
            "/a/b/c#frag?key=value" to "/a/b/c#frag?key=value",
            "https://example.com/?key=value" to "https://example.com/?key=value",

            // Needs normalization
            "https://////example.com" to "https://example.com",
            "https://////example.com///?key=value" to "https://example.com/?key=value",
            "https://example.com/////a///b//c" to "https://example.com/a/b/c",
            "https://example.com///a/b//////c//////" to "https://example.com/a/b/c/",
            "//////a////b///c//" to "/a/b/c/",

            // Leave query params / fragments alone!
            "https://example.com/a/b/c?arg=with//slashes#with//slashes" to "https://example.com/a/b/c?arg=with//slashes#with//slashes",
            "https:////example.com//a//b//c//?arg=with//slashes#with//slashes" to "https://example.com/a/b/c/?arg=with//slashes#with//slashes",
        )

        beforeAfter.forEach { (before, after) ->
            assertThat(Route.normalizeSlashes(before)).isEqualTo(after)
        }
    }
}