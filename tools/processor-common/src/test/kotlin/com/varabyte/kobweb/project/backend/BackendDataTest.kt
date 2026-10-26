package com.varabyte.kobweb.project.backend

import com.varabyte.truthish.assertThat
import org.junit.Test

class BackendDataTest {
    private fun BackendData.collectErrors(): List<String> {
        val errors = mutableListOf<String>()
        assertValid { errors.add(it) }
        return errors
    }

    @Test
    fun validDataReportsNoErrors() {
        val data = BackendData(
            apiMethods = listOf(ApiEntry("a.api.users", "/users"), ApiEntry("a.api.posts", "/posts")),
            apiStreamProperties = listOf(ApiStreamEntry("a.api.echo", "/echo"), ApiStreamEntry("a.api.chat", "/chat")),
        )
        assertThat(data.collectErrors()).isEmpty()
    }

    @Test
    fun duplicateApiRoutesAreReported() {
        val data = BackendData(
            apiMethods = listOf(
                ApiEntry("a.api.users", "/users"),
                ApiEntry("b.api.users", "/users"),
                ApiEntry("a.api.posts", "/posts"),
            ),
        )
        val errors = data.collectErrors()
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"/users\"")
        assertThat(errors[0]).contains("a.api.users")
        assertThat(errors[0]).contains("b.api.users")
    }

    @Test
    fun duplicateApiStreamRoutesAreReported() {
        val data = BackendData(
            apiStreamProperties = listOf(
                ApiStreamEntry("a.api.echo", "/echo"),
                ApiStreamEntry("b.api.echo", "/echo"),
                ApiStreamEntry("a.api.chat", "/chat"),
            ),
        )
        val errors = data.collectErrors()
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"/echo\"")
        assertThat(errors[0]).contains("a.api.echo")
        assertThat(errors[0]).contains("b.api.echo")
    }

    @Test
    fun apiStreamRoutesIgnoreLeadingSlash() {
        // The server strips a leading slash when registering streams, so these would collide at runtime
        val data = BackendData(
            apiStreamProperties = listOf(ApiStreamEntry("a.api.echo", "/echo"), ApiStreamEntry("b.api.echo", "echo")),
        )
        assertThat(data.collectErrors().size).isEqualTo(1)
    }

    @Test
    fun apiAndStreamRoutesDoNotConflictWithEachOther() {
        val data = BackendData(
            apiMethods = listOf(ApiEntry("a.api.echo", "/echo")),
            apiStreamProperties = listOf(ApiStreamEntry("a.api.echoStream", "/echo")),
        )
        assertThat(data.collectErrors()).isEmpty()
    }

    @Test
    fun mergeReportsDuplicatesAcrossModules() {
        val errors = mutableListOf<String>()
        val merged = listOf(
            BackendData(apiStreamProperties = listOf(ApiStreamEntry("lib.api.echo", "/echo"))),
            BackendData(apiStreamProperties = listOf(ApiStreamEntry("app.api.echo", "/echo"))),
        ).merge { errors.add(it) }

        assertThat(merged.apiStreamProperties.size).isEqualTo(2)
        assertThat(errors.size).isEqualTo(1)
    }
}
