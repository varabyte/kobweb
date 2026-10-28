package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.symbol.KSFile
import com.varabyte.kobweb.ksp.testutil.FakeKSFile
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.truthish.assertThat
import org.junit.Test

class RouteProcessingTest {
    private val pagesPackage = PackageName("com.example.pages")
    private val file = FakeKSFile("com.example.pages.a.b.c", "Slug.kt")

    private fun KSFile.toRoute(
        routeOverride: String?,
        packageMappings: Map<PackageName, String> = emptyMap(),
    ) = toRoute(pagesPackage, packageMappings, routeOverride?.let { RouteOverride(it) })

    @Test
    fun routeOverrideExamplesFromPageDocs() {
        listOf(
            null to "/a/b/c/slug",
            "other" to "/a/b/c/other",
            "d/e/f/" to "/a/b/c/d/e/f/slug",
            "d/e/f/other" to "/a/b/c/d/e/f/other",
            "/d/e/f/" to "/d/e/f/slug",
            "/" to "/slug",
            "{}" to "/a/b/c/{slug}",
            "{slug}" to "/a/b/c/{slug}",
            "d/e/f/{}" to "/a/b/c/d/e/f/{slug}",
            "/d/e/f/{}" to "/d/e/f/{slug}",
            "/d/{id}/f/{}" to "/d/{id}/f/{slug}",
        ).forEach { (routeOverride, expected) ->
            assertThat(file.toRoute(routeOverride)).isEqualTo(expected)
        }
    }

    @Test
    fun indexIsLeftForPageProcessingToHandle() {
        // The docs say `@Page("index")` gives "/a/b/c/", but that's done by the page processing, not here
        assertThat(file.toRoute("index")).isEqualTo("/a/b/c/index")
    }

    @Test
    fun packageMappingsApplyUnlessTheOverrideIsAbsolute() {
        val mappings = mapOf(PackageName("com.example.pages.a.b") to "{b}")
        assertThat(file.toRoute(null, mappings)).isEqualTo("/a/{b}/c/slug")
        assertThat(file.toRoute("d/other", mappings)).isEqualTo("/a/{b}/c/d/other")
        assertThat(file.toRoute("/d/other", mappings)).isEqualTo("/d/other")
    }

    @Test
    fun filesOutsideThePackageRootHaveNoRouteUnlessTheOverrideIsAbsolute() {
        val outsideFile = FakeKSFile("com.example.server", "Chat.kt")
        assertThat(outsideFile.toRoute(null)).isNull()
        assertThat(outsideFile.toRoute("chat")).isNull()
        assertThat(outsideFile.toRoute("d/chat")).isNull()
        assertThat(outsideFile.toRoute("/chat")).isEqualTo("/chat")
        assertThat(outsideFile.toRoute("/")).isEqualTo("/chat")

        // A package which only shares a prefix with the package root is not under it
        assertThat(FakeKSFile("com.example.pagesextra", "Slug.kt").toRoute(null)).isNull()
    }

    @Test
    fun slugComesFromTheFileName() {
        assertThat(FakeKSFile("com.example.pages", "OurTeam.kt").toRoute(null)).isEqualTo("/our-team")
        assertThat(FakeKSFile("com.example.pages", "_draft_.kt").toRoute("blog/")).isEqualTo("/blog/draft")
    }
}
