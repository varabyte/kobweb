package com.varabyte.kobweb.server

import com.varabyte.kobweb.server.plugins.configureRouting
import com.varabyte.truthish.assertThat
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("Hello World!")
            }
        }
    }
}