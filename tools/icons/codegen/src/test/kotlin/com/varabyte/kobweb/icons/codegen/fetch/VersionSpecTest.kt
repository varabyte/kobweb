package com.varabyte.kobweb.icons.codegen.fetch

import com.varabyte.truthish.assertThat
import org.junit.Test

class VersionSpecTest {
    @Test
    fun latestPicksNewestRelease() {
        val tags = listOf("1.9.0", "1.10.0", "1.2.0")
        assertThat(VersionSpec("latest").pickFrom(tags)).isEqualTo("1.10.0")
    }

    @Test
    fun latestSkipsPrereleasesAndOtherTags() {
        val tags = listOf("1.9.0", "2.0.0-rc.0", "v3.0.0", "@lucide/lab@0.12.0")
        assertThat(VersionSpec("latest").pickFrom(tags)).isEqualTo("1.9.0")
    }

    @Test
    fun prefixPicksNewestMatchingRelease() {
        val tags = listOf("1.9.0", "1.10.0", "2.0.0")
        assertThat(VersionSpec("1.+").pickFrom(tags)).isEqualTo("1.10.0")
    }

    @Test
    fun longerPrefixPicksNewestMatchingRelease() {
        val tags = listOf("1.9.0", "1.9.3", "1.10.0")
        assertThat(VersionSpec("1.9.+").pickFrom(tags)).isEqualTo("1.9.3")
    }

    @Test
    fun releaseWithMoreSegmentsIsNewer() {
        val tags = listOf("1.9", "1.9.1")
        assertThat(VersionSpec("latest").pickFrom(tags)).isEqualTo("1.9.1")
    }

    @Test
    fun prefixWithNoMatchingReleasePicksNothing() {
        val tags = listOf("1.9.0", "2.0.0")
        assertThat(VersionSpec("3.+").pickFrom(tags)).isNull()
    }

    @Test
    fun exactSpecs() {
        assertThat(VersionSpec("1.70.0").isExact).isTrue()
        assertThat(VersionSpec("49d4db35df873165d6bd6ba09b063c7dafbac2f4").isExact).isTrue()
        assertThat(VersionSpec("1.+").isExact).isFalse()
        assertThat(VersionSpec("latest").isExact).isFalse()
    }
}
