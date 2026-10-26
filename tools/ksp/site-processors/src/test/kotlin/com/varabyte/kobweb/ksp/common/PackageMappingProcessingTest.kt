package com.varabyte.kobweb.ksp.common

import com.varabyte.kobweb.ksp.testutil.RecordingLogger
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.truthish.assertThat
import org.junit.Test

class PackageMappingProcessingTest {
    @Test
    fun validMappings() {
        val pkg = PackageName("site.pages.users.userId")
        listOf(
            "members" to "members",
            "{user}" to "{user}",
            "{}" to "{user-id}",
        ).forEach { (value, expected) ->
            val logger = RecordingLogger()
            assertThat(pkg.toMappedRouteSegment(value, logger)).isEqualTo(expected)
            assertThat(logger.warnings).isEmpty()
            assertThat(logger.errors).isEmpty()
        }
    }

    @Test
    fun invalidMappings() {
        val pkg = PackageName("site.pages.users.user")
        listOf(
            "", " ", "{...}", "{...rest}", "{?}", "{id?}", "{...rest?}", "a?b", "a#b", "a b", "{user", "{a.b}", "a//b", "..", "%2e",
            // Every segment of a mapping with a slash is checked, not just the whole value
            "a/{...rest}", "a/{id?}", "{a}/{b?}", "{...a}/b",
            // Only the last segment can be inferred
            "{}/{}", "{}/b", "{}/{b}",
        ).forEach { value ->
            val logger = RecordingLogger()
            assertThat(pkg.toMappedRouteSegment(value, logger)).isNull()
            assertThat(logger.errors).hasSize(1)
        }
    }

    @Test
    fun suspiciousMappingsAreWarnedAbout() {
        val pkg = PackageName("site.pages.users.user")
        listOf(
            "a/b" to "a/b",
            "a/{b}" to "a/{b}",
            // An inferred last segment is named after the package
            "a/{}" to "a/{user}",
            "{a}/{}" to "{a}/{user}",
            "{a}/{b}" to "{a}/{b}",
        ).forEach { (value, expected) ->
            val logger = RecordingLogger()
            // Warnings only, so the mapping is still applied
            assertThat(pkg.toMappedRouteSegment(value, logger)).isEqualTo(expected)
            assertThat(logger.warnings).hasSize(1)
            assertThat(logger.errors).isEmpty()
        }
    }

    @Test
    fun packagesMappedMultipleTimesAreReported() {
        val mappings = listOf(
            PackageRouteMapping(PackageName("site.pages.a"), "x", "/src/a/Mapping2.kt"),
            PackageRouteMapping(PackageName("site.pages.b"), "y", "/src/b/Mapping.kt"),
            PackageRouteMapping(PackageName("site.pages.a"), "z", "/src/a/Mapping1.kt"),
        )

        val logger = RecordingLogger()
        val routeSegments = mappings.toRouteSegmentsByPackage(logger)

        assertThat(routeSegments.keys).containsExactly(PackageName("site.pages.a"), PackageName("site.pages.b"))
        assertThat(logger.errors).hasSize(1)
        // Files are listed in a stable order, regardless of processing order
        assertThat(logger.errors.single().lines().drop(1)).containsExactly(" - /src/a/Mapping1.kt", " - /src/a/Mapping2.kt").inOrder()
    }

    @Test
    fun noErrorsWhenEachPackageIsMappedOnce() {
        val mappings = listOf(
            PackageRouteMapping(PackageName("site.pages.a"), "x", "/src/a/Mapping.kt"),
            PackageRouteMapping(PackageName("site.pages.a.b"), "y", "/src/a/b/Mapping.kt"),
        )

        val logger = RecordingLogger()
        val routeSegments = mappings.toRouteSegmentsByPackage(logger)

        assertThat(logger.errors).isEmpty()
        assertThat(routeSegments).isEqualTo(mapOf(PackageName("site.pages.a") to "x", PackageName("site.pages.a.b") to "y"))
    }
}
