package com.varabyte.kobweb.project.common

import com.varabyte.truthish.assertThat
import org.junit.Test

class RouteEntryTest {
    private class TestRouteEntry(override val fqn: String, override val route: String) : RouteEntry

    private fun routeErrors(vararg routes: Pair<String, String>): List<String> {
        val errors = mutableListOf<String>()
        routes.map { (fqn, route) -> TestRouteEntry(fqn, route) }.assertValidRoutes("page") { errors.add(it) }
        return errors
    }

    @Test
    fun validRoutesReportNoErrors() {
        assertThat(
            routeErrors(
                "index" to "/",
                "about" to "/about",
                "post" to "/blog/{post}",
                "comments" to "/blog/{post}/comments",
                "rest" to "/docs/{...rest}",
                "user" to "/users/{user?}",
            )
        ).isEmpty()
    }

    @Test
    fun duplicateRoutesAreReported() {
        val errors = routeErrors("a" to "/about", "b" to "/about", "c" to "/other")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"/about\"")
        assertThat(errors[0]).contains(" - a")
        assertThat(errors[0]).contains(" - b")
    }

    @Test
    fun differentDynamicSegmentsInTheSamePositionConflict() {
        val errors = routeErrors("a" to "/users/{id}", "b" to "/users/{name}")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"{id}\" and \"{name}\"")

        // Even when the rest of the routes differ
        assertThat(routeErrors("a" to "/{a}/x", "b" to "/{b}/y").size).isEqualTo(1)
        // Or only the kind of dynamic segment does
        assertThat(routeErrors("a" to "/docs/{page}", "b" to "/docs/{...page}").size).isEqualTo(1)
        // Only the first conflict in a route is reported
        assertThat(routeErrors("a" to "/a/{b}/c/{d}", "b" to "/a/{x}/c/{y}").size).isEqualTo(1)

        // The same dynamic segment can be shared, and dynamic segments under different parents don't interact
        assertThat(routeErrors("a" to "/{a}/x", "b" to "/{a}/y")).isEmpty()
        assertThat(routeErrors("a" to "/x/{a}", "b" to "/y/{b}")).isEmpty()
        // Static and dynamic segments can be siblings
        assertThat(routeErrors("a" to "/users/me", "b" to "/users/{id}")).isEmpty()
    }

    @Test
    fun catchAllAndOptionalSegmentsMustBeLast() {
        assertThat(routeErrors("a" to "/docs/{...rest}/edit").size).isEqualTo(1)
        assertThat(routeErrors("a" to "/users/{id?}/posts").size).isEqualTo(1)
        // Including the empty segment after a trailing slash
        assertThat(routeErrors("a" to "/users/{id?}/").size).isEqualTo(1)

        // A normal dynamic segment can be followed by anything
        assertThat(routeErrors("a" to "/users/{id}/posts")).isEmpty()
        assertThat(routeErrors("a" to "/users/{id}/")).isEmpty()
    }

    @Test
    fun unnamedDynamicSegmentsAreReported() {
        assertThat(routeErrors("a" to "/a/{}/b").size).isEqualTo(1)
        assertThat(routeErrors("a" to "/a/{...}").size).isEqualTo(1)
    }

    @Test
    fun repeatedDynamicSegmentNamesAreReported() {
        val errors = routeErrors("a" to "/users/{user}/{user}")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"user\"")

        // Catch-all and optional segments are read by name too
        assertThat(routeErrors("a" to "/{id}/x/{...id}").size).isEqualTo(1)
        assertThat(routeErrors("a" to "/{id}/x/{id?}").size).isEqualTo(1)

        // Static segments can repeat
        assertThat(routeErrors("a" to "/a/{a}/a")).isEmpty()
    }

    @Test
    fun trailingOptionalSegmentConflictsWithParentRoute() {
        val errors = routeErrors("index" to "/users/", "id" to "/users/{id?}")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"{id?}\" -> \"{id}\"")

        // The parent route can have dynamic segments too
        assertThat(routeErrors("a" to "/teams/{team}/", "b" to "/teams/{team}/{member?}").size).isEqualTo(1)

        // Without the static parent route, an optional segment is fine
        assertThat(routeErrors("id" to "/users/{id?}")).isEmpty()
    }
}
