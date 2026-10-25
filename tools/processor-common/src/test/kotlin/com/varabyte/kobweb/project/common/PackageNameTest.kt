package com.varabyte.kobweb.project.common

import com.varabyte.truthish.assertThat
import org.junit.Test

class PackageNameTest {
    private fun String.isSameOrSubpackageOf(other: String) = PackageName(this).isSameOrSubpackageOf(PackageName(other))

    @Test
    fun isSameOrSubpackageOf() {
        assertThat("a.b".isSameOrSubpackageOf("a.b")).isTrue()
        assertThat("a.b.c".isSameOrSubpackageOf("a.b")).isTrue()
        assertThat("a.b.c.d".isSameOrSubpackageOf("a.b")).isTrue()

        // Sharing a string prefix is not enough; whole segments must match
        assertThat("a.bc".isSameOrSubpackageOf("a.b")).isFalse()
        assertThat("a.b_old".isSameOrSubpackageOf("a.b")).isFalse()
        assertThat("a.b_old.c".isSameOrSubpackageOf("a.b")).isFalse()

        assertThat("a".isSameOrSubpackageOf("a.b")).isFalse()
        assertThat("x.b".isSameOrSubpackageOf("a.b")).isFalse()

        // Everything lives under the root package
        assertThat("a.b".isSameOrSubpackageOf("")).isTrue()
        assertThat("".isSameOrSubpackageOf("")).isTrue()
        assertThat("".isSameOrSubpackageOf("a")).isFalse()
    }
}
