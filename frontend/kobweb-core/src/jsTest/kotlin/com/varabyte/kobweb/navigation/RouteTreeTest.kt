package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.truthish.assertThat
import kotlin.test.Test

class RouteTreeTest {
    private fun RouteTree.resolveRouteInfo(from: String): PageContext.RouteInfo {
        return createPageData(Route(from)).routeInfo
    }

    private fun RouteTree.resolveRoutePath(from: String): String {
        return resolveRouteInfo(from).path
    }

    private fun RouteTree.resolvePageMethod(from: String): PageMethod {
        return createPageData(Route(from)).pageMethod
    }

    @Test
    fun registerRoutesAndConfirmTheyAreRegistered() {
        val routeTree = RouteTree()

        val methodA = @Composable {}
        val methodB = @Composable {}
        val methodC = @Composable {}
        val methodABC = @Composable {}

        assertThat(methodA).isNotSameAs(methodB)

        routeTree.register("/a", methodA)
        routeTree.register("/b", methodB)
        routeTree.register("/c", methodC)
        routeTree.register("/a/b/c", methodABC)

        assertThat(routeTree.isRegistered("/a")).isTrue()
        assertThat(routeTree.isRegistered("/b")).isTrue()
        assertThat(routeTree.isRegistered("/c")).isTrue()
        assertThat(routeTree.isRegistered("/d")).isFalse()

        assertThat(routeTree.isRegistered("/a/")).isFalse()
        assertThat(routeTree.isRegistered("/b/")).isFalse()
        assertThat(routeTree.isRegistered("/c/")).isFalse()

        assertThat(routeTree.isRegistered("/a/b/c")).isTrue()
        assertThat(routeTree.isRegistered("/a/b/")).isFalse()
        assertThat(routeTree.isRegistered("/a/b")).isFalse()

        assertThat(routeTree.resolvePageMethod("/a")).isSameAs(methodA)
        assertThat(routeTree.resolvePageMethod("/b")).isSameAs(methodB)
        assertThat(routeTree.resolvePageMethod("/c")).isSameAs(methodC)
        assertThat(routeTree.resolvePageMethod("/a/b/c")).isSameAs(methodABC)
        assertThat(routeTree.resolveRoutePath("/a")).isEqualTo("/a")
        assertThat(routeTree.resolveRoutePath("/b")).isEqualTo("/b")
        assertThat(routeTree.resolveRoutePath("/c")).isEqualTo("/c")
        assertThat(routeTree.resolveRoutePath("/a/b/c")).isEqualTo("/a/b/c")

        routeTree.register("/x") {}
        routeTree.register("/x/") {}
        assertThat(routeTree.isRegistered("/x")).isTrue()
        assertThat(routeTree.isRegistered("/x/")).isTrue()

        routeTree.register("/z/") {}
        assertThat(routeTree.isRegistered("/z/")).isTrue()
        assertThat(routeTree.isRegistered("/z")).isFalse()
    }

    @Test
    fun dynamicRoutesWork() {
        val routeTree = RouteTree()
        routeTree.register("/users/{user}/posts/{post}") {}
        routeTree.createPageData(Route("/users/123/posts/11")).routeInfo.apply {
            assertThat(params["user"]).isEqualTo("123")
            assertThat(params["post"]).isEqualTo("11")
        }
    }

    @Test
    fun simpleRedirectWorks() {
        val routeTree = RouteTree()
        routeTree.register("/new-page") {}
        routeTree.registerRedirect("/old-page", "/new-page")

        assertThat(routeTree.isRegistered("/old-page")).isTrue()
        assertThat(routeTree.resolveRoutePath("/old-page")).isEqualTo("/new-page")
    }

    @Test
    fun redirectsProcessedInOrder() {
        val routeTree = RouteTree()
        routeTree.register("/d") {}
        routeTree.registerRedirect("/a", "/b")
        routeTree.registerRedirect("/b", "/c")
        routeTree.registerRedirect("/c", "/d")

        assertThat(routeTree.resolveRoutePath("/a")).isEqualTo("/d")
    }

    @Test
    fun redirectTakesPriorityOverRegisteredRoute() {
        val routeTree = RouteTree()
        routeTree.register("/a") {}
        routeTree.register("/b") {}
        routeTree.registerRedirect("/a", "/b")

        assertThat(routeTree.resolveRoutePath("/a")).isEqualTo("/b")
    }

    @Test
    fun redirectSubstitutionWorks() {
        val routeTree = RouteTree()
        routeTree.register("/socials/meta/feedback") {}
        routeTree.register("/socials/meta/admin") {}
        routeTree.register("/socials/meta/analytics") {}
        routeTree.register("/socials/meta/about-meta") {}

        routeTree.registerRedirect("/socials/facebook/([^/]+)", "/socials/meta/$1")
        routeTree.registerRedirect("(/socials/meta)/about-facebook", "$1/about-meta")

        assertThat(routeTree.resolveRoutePath("/socials/facebook/feedback")).isEqualTo("/socials/meta/feedback")
        assertThat(routeTree.resolveRoutePath("/socials/facebook/admin")).isEqualTo("/socials/meta/admin")
        assertThat(routeTree.resolveRoutePath("/socials/facebook/analytics")).isEqualTo("/socials/meta/analytics")
        assertThat(routeTree.resolveRoutePath("/socials/meta/about-facebook")).isEqualTo("/socials/meta/about-meta")

        routeTree.register("/a/b/c/d") {}
        routeTree.registerRedirect("/d/([^/]+)/([^/]+)/([^/]+)", "/$1/$2/$3/d")
        assertThat(routeTree.resolveRoutePath("/d/a/b/c")).isEqualTo("/a/b/c/d")
    }

    @Test
    fun legacyRoutesWorkIfEnabled() {
        val routeTree = RouteTree()
        routeTree.register("/a/multi-word-folder-x/b/multi-word-folder-y/c/multi-word-resource") {}
        routeTree.register("/a/multi-word-folder-x/b/multi-word-folder-z/c/multi-word-resource") {}

        routeTree.legacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.ALLOW
        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multiWordFolderY/c/multiwordresource")).isTrue()
        assertThat(routeTree.isRegistered("/a/multi-word-folder-x/b/multiWordFolderY/c/multiwordresource")).isTrue()
        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multi-word-folder-y/c/multiwordresource")).isTrue()
        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multiWordFolderY/c/multi-word-resource")).isTrue()
        assertThat(routeTree.isRegistered("/a/multi-word-folder-x/b/multi-word-folder-y/c/multiwordresource")).isTrue()

        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multiWordFolderZ/c/multiwordresource")).isTrue()

        routeTree.legacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.DISALLOW
        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multiWordFolderY/c/multiwordresource")).isFalse()
        assertThat(routeTree.isRegistered("/a/multi-word-folder-x/b/multiWordFolderY/c/multiwordresource")).isFalse()
        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multi-word-folder-y/c/multiwordresource")).isFalse()
        assertThat(routeTree.isRegistered("/a/multiWordFolderX/b/multiWordFolderY/c/multi-word-resource")).isFalse()
        assertThat(routeTree.isRegistered("/a/multi-word-folder-x/b/multi-word-folder-y/c/multiwordresource")).isFalse()
    }

    @Test
    fun dynamicRoutesArePopulatedWithExtraValuesIfLegacyRoutesAreEnabled() {
        val routeTree = RouteTree()
        routeTree.register("/{multi-word-folder}/{multi-word-resource}") {}

        routeTree.legacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.ALLOW
        routeTree.resolveRouteInfo("/a/b").let { routeInfo ->
            assertThat(routeInfo.params).containsExactly(
                "multi-word-folder" to "a",
                "multiWordFolder" to "a",
                "multiwordfolder" to "a",
                "multi_word_folder" to "a",
                "multi-word-resource" to "b",
                "multiWordResource" to "b",
                "multiwordresource" to "b",
                "multi_word_resource" to "b",
            )
        }

        routeTree.legacyRouteRedirectStrategy = Router.LegacyRouteRedirectStrategy.DISALLOW
        routeTree.resolveRouteInfo("/a/b").let { routeInfo ->
            assertThat(routeInfo.params).containsExactly(
                "multi-word-folder" to "a",
                "multi-word-resource" to "b",
            )
        }
    }
}
