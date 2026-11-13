package com.varabyte.kobweb.core

import com.varabyte.kobweb.navigation.Route
import com.varabyte.truthish.assertThat
import kotlin.test.Test

class RouteInfoTest {
    @Test
    fun queryParamsAreDecoded() {
        val routeInfo = RouteInfo(Route("/users?name=john%20doe"), dynamicParams = emptyMap())

        assertThat(routeInfo.queryParams["name"]).isEqualTo("john doe")
        assertThat(routeInfo.params["name"]).isEqualTo("john doe")
    }

    @Test
    fun queryParamsDoNotAppearInDynamicParams() {
        val routeInfo = RouteInfo(Route("/users/posts?user=123&post=11"), dynamicParams = emptyMap())

        assertThat(routeInfo.queryParams).isEqualTo(mapOf("user" to "123", "post" to "11"))
        assertThat(routeInfo.dynamicParams).isEmpty()
        assertThat(routeInfo.params).isEqualTo(mapOf("user" to "123", "post" to "11"))
    }

    @Test
    fun dynamicParamsDoNotAppearInQueryParams() {
        val routeInfo = RouteInfo(
            Route("/users/123/posts/11"),
            dynamicParams = mapOf("user" to "123", "post" to "11"),
        )

        assertThat(routeInfo.dynamicParams).isEqualTo(mapOf("user" to "123", "post" to "11"))
        assertThat(routeInfo.queryParams).isEmpty()
        assertThat(routeInfo.params).isEqualTo(mapOf("user" to "123", "post" to "11"))
    }

    @Test
    fun paramsCombinesQueryAndDynamicParams() {
        val routeInfo = RouteInfo(
            Route("/users/123/posts?sort=asc"),
            dynamicParams = mapOf("user" to "123"),
        )

        assertThat(routeInfo.dynamicParams).isEqualTo(mapOf("user" to "123"))
        assertThat(routeInfo.queryParams).isEqualTo(mapOf("sort" to "asc"))
        assertThat(routeInfo.params).isEqualTo(mapOf("user" to "123", "sort" to "asc"))
    }

    @Test
    fun dynamicParamsTakePrecedenceOverQueryParamsWithTheSameName() {
        val routeInfo = RouteInfo(
            Route("/users/123?user=456"),
            dynamicParams = mapOf("user" to "123"),
        )

        assertThat(routeInfo.params["user"]).isEqualTo("123")
        assertThat(routeInfo.dynamicParams["user"]).isEqualTo("123")
        assertThat(routeInfo.queryParams["user"]).isEqualTo("456")
    }

    @Test
    fun pathSlugAndFragment() {
        val routeInfo = RouteInfo(Route("/a/b/c/slug?x=1&y=2#id"), dynamicParams = emptyMap())

        assertThat(routeInfo.path).isEqualTo("/a/b/c/slug")
        assertThat(routeInfo.slug).isEqualTo("slug")
        assertThat(routeInfo.fragment).isEqualTo("id")
        assertThat(routeInfo.pathQueryAndFragment).isEqualTo("/a/b/c/slug?x=1&y=2#id")
    }

    @Test
    fun fragmentIsNullWhenMissing() {
        val routeInfo = RouteInfo(Route("/a/b/c"), dynamicParams = emptyMap())

        assertThat(routeInfo.fragment).isNull()
    }

    @Test
    fun copyKeepsDynamicParams() {
        val routeInfo = RouteInfo(
            Route("/users/123?sort=asc"),
            dynamicParams = mapOf("user" to "123"),
        )

        val copied = routeInfo.copy(queryParams = mapOf("sort" to "desc"))

        assertThat(copied.dynamicParams).isEqualTo(mapOf("user" to "123"))
        assertThat(copied.queryParams).isEqualTo(mapOf("sort" to "desc"))
        assertThat(copied.params).isEqualTo(mapOf("user" to "123", "sort" to "desc"))
    }

    @Test
    fun copyKeepsDynamicParamsAsIs() {
        // "100%25" would turn into "100%" if it were decoded again
        val routeInfo = RouteInfo(
            Route("/discounts/100%2525"),
            dynamicParams = mapOf("amount" to "100%25"),
        )

        val copied = routeInfo.copy(fragment = "details")

        assertThat(copied.dynamicParams["amount"]).isEqualTo("100%25")
    }

    @Test
    fun routesWithTheSamePathParamsAndFragmentAreEqual() {
        val first = RouteInfo(Route("/users/123?sort=asc#top"), dynamicParams = mapOf("user" to "123"))
        val second = RouteInfo(Route("/users/123?sort=asc#top"), dynamicParams = mapOf("user" to "123"))

        assertThat(first).isEqualTo(second)
        assertThat(first.hashCode()).isEqualTo(second.hashCode())
    }

    @Test
    fun routesWithDifferentParamsAreNotEqual() {
        val first = RouteInfo(Route("/users?sort=asc"), dynamicParams = emptyMap())
        val second = RouteInfo(Route("/users?sort=desc"), dynamicParams = emptyMap())

        assertThat(first).isNotEqualTo(second)
    }
}
