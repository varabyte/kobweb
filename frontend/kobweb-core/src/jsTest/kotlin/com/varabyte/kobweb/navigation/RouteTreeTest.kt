package com.varabyte.kobweb.navigation

import com.varabyte.truthish.assertThat
import kotlin.test.Test

class RouteTreeTest {
    private val userPage: PageMethod = {}
    private val errorPage: PageMethod = {}

    @Test
    fun createPageDataDecodesDynamicParams() {
        val routeTree = RouteTree<PageMethod>()
        routeTree.register("/users/{user}", userPage)

        val pageData = routeTree.createPageData(Route("/users/john%20doe"), errorPage)

        assertThat(pageData.routeInfo.dynamicParams["user"]).isEqualTo("john doe")
        assertThat(pageData.routeInfo.params["user"]).isEqualTo("john doe")
    }
}
