package com.varabyte.kobweb.core

import com.varabyte.kobweb.navigation.Route
import com.varabyte.truthish.assertThat
import kotlin.test.Test

class RouteInfoTest {
    @Test
    fun paramsContainsDecodedDynamicValues() {
        val routeInfo = RouteInfo(
            Route("/users/john%20doe", queryParams = emptyMap(), fragment = null),
            dynamicParams = mapOf("user" to "john%20doe"),
        )

        assertThat(routeInfo.dynamicParams["user"]).isEqualTo("john doe")
        assertThat(routeInfo.params["user"]).isEqualTo("john doe")
    }
}
