package com.varabyte.kobweb.ksp.util

import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.truthish.assertThat
import org.junit.Test

class RouteUtilsTest {
    @Test
    fun toRouteWithoutMappings() {
        val root = PackageName("site.pages")
        assertThat(PackageName("site.pages").toRoute(root, emptyMap())).isEqualTo("/")
        assertThat(PackageName("site.pages.blog").toRoute(root, emptyMap())).isEqualTo("/blog/")
        assertThat(PackageName("site.pages.corp.ourTeam").toRoute(root, emptyMap())).isEqualTo("/corp/our-team/")
        assertThat(PackageName("site.pages.blogs._2021").toRoute(root, emptyMap())).isEqualTo("/blogs/2021/")
    }

    @Test
    fun toRouteWithMappings() {
        val root = PackageName("site.pages")
        val mappings = mapOf(
            PackageName("site.pages.blogs._2021") to "twenty-twenty-one",
            PackageName("site.pages.blogs._2021._12") to "{month}",
        )

        assertThat(PackageName("site.pages.blogs._2021").toRoute(root, mappings))
            .isEqualTo("/blogs/twenty-twenty-one/")
        assertThat(PackageName("site.pages.blogs._2021._12.tutorials").toRoute(root, mappings))
            .isEqualTo("/blogs/twenty-twenty-one/{month}/tutorials/")
        // Mappings only apply to their exact package
        assertThat(PackageName("site.pages.blogs._2022").toRoute(root, mappings)).isEqualTo("/blogs/2022/")
    }
}
