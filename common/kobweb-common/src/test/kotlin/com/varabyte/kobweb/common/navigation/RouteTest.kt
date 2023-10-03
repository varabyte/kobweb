package com.varabyte.kobweb.common.navigation

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class RouteTest {
    @Test
    fun checkExpectedResolveBehavior() {
        Route("unused").apply {
            // empty reference path not allowed
            assertThrows<IllegalArgumentException> { resolve("") }
            // folder reference path not allowed
            assertThrows<IllegalArgumentException> { resolve("a/") }
        }

        Route("").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/a/b/c/filename")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/a/b/c/")
        }

        Route("slug").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/a/b/c/slug")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/a/b/c/slug")
        }

        Route("index").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/a/b/c/")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/a/b/c/")
        }

        Route("d/e/f/slug").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/a/b/c/d/e/f/slug")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/a/b/c/d/e/f/slug")

        }

        Route("d/e/f/index").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/a/b/c/d/e/f/")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/a/b/c/d/e/f/")
        }

        Route("d/e/f/").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/a/b/c/d/e/f/filename")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/a/b/c/d/e/f/")
        }

        Route("/x/y/z/slug").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/x/y/z/slug")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/x/y/z/slug")
        }

        Route("/x/y/z/index").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/x/y/z/")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/x/y/z/")
        }

        Route("/x/y/z/").apply {
            assertThat(resolve("a/b/c/filename.kt")).isEqualTo("/x/y/z/filename")
            assertThat(resolve("a/b/c/index.kt")).isEqualTo("/x/y/z/")
        }
    }
}
