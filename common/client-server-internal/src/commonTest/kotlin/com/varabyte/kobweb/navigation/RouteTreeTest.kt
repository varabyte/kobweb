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
    fun canRegisterNestedRoutesBeforeNonNested() {
        val routeTree = RouteTree<DummyData>()

        val dataA = DummyData()
        val dataABC = DummyData()

        routeTree.register("/a/b/c", dataABC)
        routeTree.register("/a", dataA)

        assertThat(routeTree.resolveRouteData("/a")).isSameAs(dataA)
        assertThat(routeTree.resolveRouteData("/a/b/c")).isSameAs(dataABC)
    }

    @Test
    fun canRepeatDynamicRouteIfNameIsSame() {
        val routeTree = RouteTree<DummyData>()

        val dataRoot = DummyData()
        val dataA = DummyData()
        val dataB = DummyData()

        routeTree.register("/{dynamic}", dataRoot)
        routeTree.register("/{dynamic}/a", dataA)
        routeTree.register("/{dynamic}/b", dataB)

        assertThat(routeTree.resolveRouteData("/root")).isSameAs(dataRoot)
        assertThat(routeTree.resolveRouteData("/root/a")).isSameAs(dataA)
        assertThat(routeTree.resolveRouteData("/root/b")).isSameAs(dataB)
        assertThat(routeTree.resolve("/root/c")).isNull()

        routeTree.resolve("/root")!!.captureDynamicValues().let { values ->
            assertThat(values).containsExactly("dynamic" to "root")
        }
        routeTree.resolve("/root/a")!!.captureDynamicValues().let { values ->
            assertThat(values).containsExactly("dynamic" to "root")
        }
        routeTree.resolve("/root/b")!!.captureDynamicValues().let { values ->
            assertThat(values).containsExactly("dynamic" to "root")
        }
    }

    @Test
    fun dynamicRoutesAndStaticRoutesCanExistSideBySideAndStaticRoutesTakePrecedence() {
        run {
            val routeTree = RouteTree<DummyData>()

            val dataA = DummyData()
            val dataB = DummyData()
            val dataC = DummyData()
            val dataABC = DummyData()
            val dataElse = DummyData()

            routeTree.register("/a", dataA)
            routeTree.register("/b", dataB)
            routeTree.register("/c", dataC)
            routeTree.register("/a/b/c", dataABC)
            routeTree.register("/{else}", dataElse)
            routeTree.register("/{else}/b/c", dataElse)
            routeTree.register("/a/{else}/c", dataElse)
            routeTree.register("/a/b/{else}", dataElse)


            assertThat(routeTree.resolveRouteData("/a")).isSameAs(dataA)
            assertThat(routeTree.resolveRouteData("/b")).isSameAs(dataB)
            assertThat(routeTree.resolveRouteData("/c")).isSameAs(dataC)
            assertThat(routeTree.resolveRouteData("/x")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/a/b/c")).isSameAs(dataABC)
            assertThat(routeTree.resolveRouteData("/o/b/c")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/a/o/c")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/a/b/o")).isSameAs(dataElse)
        }

        run {
            val routeTree = RouteTree<DummyData>()

            val dataX = DummyData()
            val dataXYZ = DummyData()
            val dataElse = DummyData()

            routeTree.register("/{else}", dataElse)
            routeTree.register("/{else}/y/z", dataElse)
            routeTree.register("/x/{else}/z", dataElse)
            routeTree.register("/x/y/{else}", dataElse)

            // static nodes registered after dynamic nodes still take precedence
            routeTree.register("/x", dataX)
            routeTree.register("/x/y/z", dataXYZ)

            assertThat(routeTree.resolveRouteData("/x")).isSameAs(dataX)
            assertThat(routeTree.resolveRouteData("/z")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/a/y/z")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/x/a/z")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/x/y/a")).isSameAs(dataElse)
            assertThat(routeTree.resolveRouteData("/x/y/z")).isSameAs(dataXYZ)
        }

        run {
            val routeTree = RouteTree<DummyData>()

            val dataAB = DummyData()
            val dataAElse = DummyData()
            val dataElseB = DummyData()
            val dataElse = DummyData()

            routeTree.register("/{a}/b", dataElseB)
            routeTree.register("/a/{b}", dataAElse)
            routeTree.register("/{a}/{b}", dataElse)
            assertThat(routeTree.resolveRouteData("/a/y")).isSameAs(dataAElse)
            assertThat(routeTree.resolveRouteData("/x/b")).isSameAs(dataElseB)
            assertThat(routeTree.resolveRouteData("/x/y")).isSameAs(dataElse)

            // Both registered routes match the following URL, but the one with the earlier static route takes
            // precedence.
            assertThat(routeTree.resolveRouteData("/a/b")).isSameAs(dataAElse)

            routeTree.register("/a/b", dataAB)
            assertThat(routeTree.resolveRouteData("/a/b")).isSameAs(dataAB)
        }
    }

    @Test
    fun staticRoutesDoNotPreventDynamicRoutesFromMatching() {
        val routeTree = RouteTree<DummyData>()

        val dataXY = DummyData()
        val dataElse = DummyData()

        routeTree.register("/x/y", dataXY)
        routeTree.register("/{else}", dataElse)

        assertThat(routeTree.resolveRouteData("/x")).isSameAs(dataElse)
        assertThat(routeTree.resolveRouteData("/x/y")).isSameAs(dataXY)
    }

    @Test
    fun onlyOneDynamicRouteNameAllowedPerLevel() {
        val routeTree = RouteTree<DummyData>()

        val data = DummyData()

        // Same name is OK
        routeTree.register("/{a}", data)
        routeTree.register("/{a}/x", data)
        routeTree.register("/{a}/y", data)
        routeTree.register("/a/{b}", data)
        routeTree.register("/a/{b}/x", data)
        routeTree.register("/a/{b}/y", data)

        // {j} conflicts with {a}
        assertThrows<IllegalStateException> {
            routeTree.register("/{j}", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("{j}")
                that(ex.message!!).contains("{a}")
            }
        }

        // {j} conflicts with {a}
        assertThrows<IllegalStateException> {
            routeTree.register("/{j}/k", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("{j}")
                that(ex.message!!).contains("{a}")
            }
        }

        // {j} conflicts with {b}
        assertThrows<IllegalStateException> {
            routeTree.register("/a/{j}", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("{j}")
                that(ex.message!!).contains("{b}")
            }
        }

        // {j} conflicts with {b}
        assertThrows<IllegalStateException> {
            routeTree.register("/a/{j}/k", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("{j}")
                that(ex.message!!).contains("{b}")
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
    fun dynamicRoutesCannotHaveDynamicSiblings() {
        val data = DummyData()
        assertThrows<IllegalStateException> {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/a/{x}/c", data)
            routeTree.register("/a/{d}", data)
        }.let { ex ->
            assertAll {
                that(ex.message!!).contains("{x}")
                that(ex.message!!).contains("{d}")
            }
        }
    }

    @Test
    fun catchAllRoutesCannotHaveNonCatchAllSiblingsEvenWithTheSameName() {
        val data = DummyData()
        assertThrows<IllegalStateException> {
            val routeTree = RouteTree<DummyData>()
            routeTree.register("/a/{else}", data)
            routeTree.register("/a/{...else}", data)
        }.let { ex ->
            assertThat(ex.message!!).contains("else")
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
