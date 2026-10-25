package com.varabyte.kobweb.ksp.util

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class RouteUtilsTest {
    @Test
    fun packageSegmentToRouteSegment() {
        assertThat(RouteUtils.packageSegmentToRouteSegment("blog")).isEqualTo("blog")
        assertThat(RouteUtils.packageSegmentToRouteSegment("ourTeam")).isEqualTo("our-team")
        assertThat(RouteUtils.packageSegmentToRouteSegment("_2021")).isEqualTo("2021")
        assertThat(RouteUtils.packageSegmentToRouteSegment("fun_")).isEqualTo("fun")
        assertThat(RouteUtils.packageSegmentToRouteSegment("our_team")).isEqualTo("our-team")
        assertThat(RouteUtils.packageSegmentToRouteSegment("our__team")).isEqualTo("our-team")

        assertThrows<IllegalArgumentException> { RouteUtils.packageSegmentToRouteSegment("a.b") }
    }

    @Test
    fun convertPackageToRouteWithoutMappings() {
        assertThat(RouteUtils.convertPackageToRoute("site.pages", emptyMap(), "site.pages")).isEqualTo("/")
        assertThat(RouteUtils.convertPackageToRoute("site.pages", emptyMap(), "site.pages.blog")).isEqualTo("/blog/")
        assertThat(RouteUtils.convertPackageToRoute("site.pages", emptyMap(), "site.pages.corp.ourTeam"))
            .isEqualTo("/corp/our-team/")
        assertThat(RouteUtils.convertPackageToRoute("site.pages", emptyMap(), "site.pages.blogs._2021"))
            .isEqualTo("/blogs/2021/")
    }

    @Test
    fun convertPackageToRouteWithMappings() {
        val mappings = mapOf(
            "site.pages.blogs._2021" to "twenty-twenty-one",
            "site.pages.blogs._2021._12" to "{month}",
        )

        assertThat(RouteUtils.convertPackageToRoute("site.pages", mappings, "site.pages.blogs._2021"))
            .isEqualTo("/blogs/twenty-twenty-one/")
        assertThat(RouteUtils.convertPackageToRoute("site.pages", mappings, "site.pages.blogs._2021._12.tutorials"))
            .isEqualTo("/blogs/twenty-twenty-one/{month}/tutorials/")
        // Mappings only apply to their exact package
        assertThat(RouteUtils.convertPackageToRoute("site.pages", mappings, "site.pages.blogs._2022"))
            .isEqualTo("/blogs/2022/")
    }
}
