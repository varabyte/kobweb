@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.backend

import com.varabyte.kobweb.ksp.testutil.BackendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appBackendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class ApiStreamKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `api stream routes come from their file path`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    val echo = ApiStream { ctx -> }
                """.trimIndent(),
                "example/api/chat/Room.kt" to
                    //language=kotlin
                    """
                    package example.api.chat

                    import com.varabyte.kobweb.api.stream.ApiStream

                    val room = object : ApiStream() {
                        override suspend fun onTextReceived(ctx: TextReceivedContext) {}
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiStreamProperties.map { it.fqn to it.route }).containsExactly(
            "example.api.echo" to "/echo",
            "example.api.chat.room" to "/chat/room",
        )
    }

    @Test
    fun `route override replaces the file name`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/chat/Room.kt" to
                    //language=kotlin
                    """
                    package example.api.chat

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.stream.ApiStream

                    @Api("lobby")
                    val room = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiStreamProperties.map { it.fqn to it.route })
            .containsExactly("example.api.chat.room" to "/chat/lobby")
    }

    @Test
    fun `absolute route override works outside the api package`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/server/Echo.kt" to
                    //language=kotlin
                    """
                    package example.server

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.stream.ApiStream

                    @Api("/a/b/c")
                    val echo = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiStreamProperties.map { it.fqn to it.route })
            .containsExactly("example.server.echo" to "/a/b/c")
    }

    @Test
    fun `api stream outside the api package is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/server/Echo.kt" to
                    //language=kotlin
                    """
                    package example.server

                    import com.varabyte.kobweb.api.stream.ApiStream

                    val echo = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`val echo` is defined under package `example.server` but must exist under `example.api` (or use `@Api` with a route override that starts with a slash)."
        )
    }

    @Test
    fun `api stream with a dynamic route is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/chat/Room.kt" to
                    //language=kotlin
                    """
                    package example.api.chat

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.stream.ApiStream

                    @Api("{}")
                    val room = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`val room` resolves to the route \"/chat/{room}\", but API streams don't support dynamic route segments."
        )
    }

    @Test
    fun `api streams with the same route are an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    val echo = ApiStream { ctx -> }
                    val echoAgain = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "The API stream route \"/echo\" was defined more than once. Used by:\n - example.api.echo\n - example.api.echoAgain"
        )
    }

    @Test
    fun `nested api stream is not registered`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    object Streams {
                        val echo = ApiStream { ctx -> }
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).containsExactly(
            "Not registering ApiStream `val echo`, as only top-level API streams are supported at this time. Although fixing this is recommended, you can manually register your API Stream inside an @InitApi block instead (`ctx.apis.registerStream(\"route\", echo)`). Suppress this message by adding a `@Suppress(\"TOP_LEVEL_API_STREAM\")` annotation."
        )
        assertThat(result.appBackendData.backendData.apiStreamProperties).isEmpty()
    }

    @Test
    fun `nested api stream warning can be suppressed`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    object Streams {
                        @Suppress("TOP_LEVEL_API_STREAM")
                        val echo = ApiStream { ctx -> }
                    }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiStreamProperties).isEmpty()
    }

    @Test
    fun `private api stream is not registered`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    private val echo = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).containsExactly(
            "Not registering ApiStream `val echo`, as it is not public. Although fixing this is recommended, you can manually register your API Stream inside an @InitApi block instead (`ctx.apis.registerStream(\"route\", echo)`). Suppress this message by adding a `@Suppress(\"PRIVATE_API_STREAM\")` annotation or prepending the name with an underscore."
        )
        assertThat(result.appBackendData.backendData.apiStreamProperties).isEmpty()
    }

    @Test
    fun `private api stream warning can be suppressed with a leading underscore`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    private val _echo = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiStreamProperties).isEmpty()
    }

    @Test
    fun `private api stream warning can be suppressed with an annotation`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.stream.ApiStream

                    @Suppress("PRIVATE_API_STREAM")
                    private val echo = ApiStream { ctx -> }
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiStreamProperties).isEmpty()
    }
}
