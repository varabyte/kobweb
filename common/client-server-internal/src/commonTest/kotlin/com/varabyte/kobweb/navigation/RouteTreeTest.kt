package com.varabyte.kobweb.navigation

import com.varabyte.truthish.assertAll
import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import kotlin.test.Test

class RouteTreeTest {
    class DummyData

    private fun RouteTree<DummyData>.resolveRouteData(from: String, allowRedirects: Boolean = true): DummyData {
        return this.resolve(from, allowRedirects)!!.last().node.data!!
    }

    @Test
    fun registerRoutesAndConfirmTheyAreRegistered() {
        val routeTree = RouteTree<DummyData>()

        val dataA = DummyData()
        val dataB = DummyData()
        val dataC = DummyData()
        val dataABC = DummyData()

        routeTree.register("/a", dataA)
        routeTree.register("/b", dataB)
        routeTree.register("/c", dataC)
        routeTree.register("/a/b/c", dataABC)

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

        assertThat(routeTree.resolveRouteData("/a")).isSameAs(dataA)
        assertThat(routeTree.resolveRouteData("/b")).isSameAs(dataB)
        assertThat(routeTree.resolveRouteData("/c")).isSameAs(dataC)
        assertThat(routeTree.resolveRouteData("/a/b/c")).isSameAs(dataABC)

        val dataX = DummyData()
        routeTree.register("/x", dataX)
        routeTree.register("/x/", dataX)
        assertThat(routeTree.isRegistered("/x")).isTrue()
        assertThat(routeTree.isRegistered("/x/")).isTrue()

        val dataZ = DummyData()
        routeTree.register("/z/", dataZ)
        assertThat(routeTree.isRegistered("/z/")).isTrue()
        assertThat(routeTree.isRegistered("/z")).isFalse()
    }

    @Test
    fun dynamicRoutesWork() {
        val data = DummyData()
        run {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/users/{user}/posts/{post}", data)
            routeTree.resolve("/users/123/posts/11")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly(
                    "user" to "123",
                    "post" to "11"
                )
            }
        }

        run {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/games/{...slug}", data)
            routeTree.resolve("/games/frogger")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "frogger")
            }
            routeTree.resolve("/games/space-invaders/difficulty/easy")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "space-invaders/difficulty/easy")
            }

            assertThat(routeTree.resolve("/games/")).isNull()
        }

        run {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/games/{...slug?}", data)
            routeTree.resolve("/games/frogger")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "frogger")
            }
            routeTree.resolve("/games/space-invaders/difficulty/easy")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "space-invaders/difficulty/easy")
            }

            routeTree.resolve("/games/")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "")
            }

            assertThat(routeTree.resolve("/games")).isNull()
        }

        run {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/{...slug?}", data)
            routeTree.resolve("/")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "")
            }
            routeTree.resolve("/a/b/c")!!.captureDynamicValues().let { dynamicParams ->
                assertThat(dynamicParams).containsExactly("slug" to "a/b/c")
            }
        }
    }

    @Test
    fun catchAllDynamicRoutesMustBeTheLastPartOfTheRoute() {
        val data = DummyData()
        assertThrows<IllegalStateException> {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/a/b/{...slug}/etc", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("{...slug}")
                that(ex.message!!).contains("etc")
            }
        }
    }

    @Test
    fun dynamicRoutesCannotHaveSiblings() {
        val data = DummyData()
        assertThrows<IllegalStateException> {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/a/b/c", data)
            routeTree.register("/a/{d}", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("b")
                that(ex.message!!).contains("{d}")
            }
        }

        assertThrows<IllegalStateException> {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/a/{d}", data)
            routeTree.register("/a/b/c", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("b")
                that(ex.message!!).contains("{d}")
            }
        }
    }

    @Test
    fun simpleRedirectWorks() {
        val routeTree = RouteTree<DummyData>()
        val data = DummyData()
        routeTree.register("/new-page", data)
        routeTree.registerRedirect("/old-page", "/new-page")

        assertThat(routeTree.isRegistered("/old-page")).isTrue()
        assertThat(routeTree.resolveRouteData("/old-page", allowRedirects = true)).isSameAs(data)
        assertThat(routeTree.resolve("/old-page", allowRedirects = false)).isNull()
    }
    @Test
    fun redirectsProcessedInOrder() {
        val routeTree = RouteTree<DummyData>()
        val data = DummyData()
        routeTree.register("/d", data)
        routeTree.registerRedirect("/a", "/b")
        routeTree.registerRedirect("/b", "/c")
        routeTree.registerRedirect("/c", "/d")

        assertThat(routeTree.resolveRouteData("/a")).isSameAs(data)
    }

    @Test
    fun redirectTakesPriorityOverRegisteredRoute() {
        val routeTree = RouteTree<DummyData>()
        val dataA = DummyData()
        val dataB = DummyData()
        routeTree.register("/a", dataA)
        routeTree.register("/b", dataB)
        routeTree.registerRedirect("/a", "/b")

        assertThat(routeTree.resolveRouteData("/a")).isEqualTo(dataB)
    }

    @Test
    fun redirectSubstitutionWorks() {
        val routeTree = RouteTree<DummyData>()
        val dataFeedback = DummyData()
        val dataAdmin = DummyData()
        val dataAnalytics = DummyData()
        val dataAbout = DummyData()
        routeTree.register("/socials/meta/feedback", dataFeedback)
        routeTree.register("/socials/meta/admin", dataAdmin)
        routeTree.register("/socials/meta/analytics", dataAnalytics)
        routeTree.register("/socials/meta/about-meta", dataAbout)

        routeTree.registerRedirect("/socials/facebook/([^/]+)", "/socials/meta/$1")
        routeTree.registerRedirect("(/socials/meta)/about-facebook", "$1/about-meta")

        assertThat(routeTree.resolveRouteData("/socials/facebook/feedback")).isSameAs(dataFeedback)
        assertThat(routeTree.resolveRouteData("/socials/facebook/admin")).isSameAs(dataAdmin)
        assertThat(routeTree.resolveRouteData("/socials/facebook/analytics")).isSameAs(dataAnalytics)
        assertThat(routeTree.resolveRouteData("/socials/meta/about-facebook")).isSameAs(dataAbout)

        val dataABCD = DummyData()
        routeTree.register("/a/b/c/d", dataABCD)
        routeTree.registerRedirect("/d/([^/]+)/([^/]+)/([^/]+)", "/$1/$2/$3/d")
        assertThat(routeTree.resolveRouteData("/d/a/b/c")).isSameAs(dataABCD)
    }
}
