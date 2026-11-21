@file:Suppress("FunctionName", "RedundantSuspendModifier")

package com.varabyte.kobweb.ksp.backend

import com.varabyte.kobweb.ksp.testutil.BackendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appBackendData
import com.varabyte.kobweb.ksp.testutil.libraryBackendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class ApiKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `api routes come from their file path`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun echo(ctx: ApiContext) {}
                """.trimIndent(),
                "example/api/users/Profile.kt" to
                    //language=kotlin
                    """
                    package example.api.users

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun fetchProfile(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiMethods.map { it.fqn to it.route }).containsExactly(
            "example.api.echo" to "/echo",
            "example.api.users.fetchProfile" to "/users/profile",
        )
    }

    @Test
    fun `api package can be configured`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example", apiPackage = ".server"),
            sources = mapOf(
                "example/server/Echo.kt" to
                    //language=kotlin
                    """
                    package example.server

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun echo(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiMethods.map { it.fqn to it.route })
            .containsExactly("example.server.echo" to "/echo")
    }

    @Test
    fun `api outside the api package is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/server/Health.kt" to
                    //language=kotlin
                    """
                    package example.server

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun health(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`@Api fun health` is defined under package `example.server` but must exist under `example.api`."
        )
    }

    @Test
    fun `api without a context is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.Api

                    @Api
                    suspend fun echo() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.api.echo` is tagged with the `@Api` annotation but doesn't take an ApiContext as its only parameter."
        )
    }

    @Test
    fun `route override replaces the file name`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/users/Profile.kt" to
                    //language=kotlin
                    """
                    package example.api.users

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api("{}")
                    suspend fun fetchProfile(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiMethods.map { it.fqn to it.route })
            .containsExactly("example.api.users.fetchProfile" to "/users/{profile}")
    }

    @Test
    fun `absolute route override works outside the api package`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/server/Health.kt" to
                    //language=kotlin
                    """
                    package example.server

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api("/status/health")
                    suspend fun health(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiMethods.map { it.fqn to it.route })
            .containsExactly("example.server.health" to "/status/health")
    }

    @Test
    fun `invalid route override is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api("say hi")
                    suspend fun echo(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`@Api(\"say hi\")` is invalid. `say hi` contains characters which can't be part of a URL path as is: a space (`%20`). To include them in the URL, write their percent-encoded value instead."
        )
    }

    @Test
    fun `apis with the same route are an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun echo(ctx: ApiContext) {}

                    @Api
                    suspend fun echoLoudly(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "The API route \"/echo\" was defined more than once. Used by:\n - example.api.echo\n - example.api.echoLoudly"
        )
    }

    @Test
    fun `package mapping renames its package in routes`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/api/users/user/PackageMapping.kt" to
                    //language=kotlin
                    """
                    @file:PackageMapping("{}")

                    package example.api.users.user

                    import com.varabyte.kobweb.api.PackageMapping
                """.trimIndent(),
                "example/api/users/user/Profile.kt" to
                    //language=kotlin
                    """
                    package example.api.users.user

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun fetchProfile(ctx: ApiContext) {}
                """.trimIndent(),
                "example/api/users/user/Settings.kt" to
                    //language=kotlin
                    """
                    package example.api.users.user

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun updateSettings(ctx: ApiContext) {}
                """.trimIndent(),
                "example/api/users/user/posts/Recent.kt" to
                    //language=kotlin
                    """
                    package example.api.users.user.posts

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun fetchRecentPosts(ctx: ApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.apiMethods.map { it.fqn to it.route })
            .containsExactly(
                "example.api.users.user.fetchProfile" to "/users/{user}/profile",
                "example.api.users.user.updateSettings" to "/users/{user}/settings",
                "example.api.users.user.posts.fetchRecentPosts" to "/users/{user}/posts/recent",
            )
    }

    @Test
    fun `package mapping outside the api package is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/server/PackageMapping.kt" to
                    //language=kotlin
                    """
                    @file:PackageMapping("status")

                    package example.server

                    import com.varabyte.kobweb.api.PackageMapping
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`@file:PackageMapping` is defined under package `example.server` but must exist under `example.api`."
        )
    }

    @Test
    fun `init api functions are collected`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Init.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.init.InitApi
                    import com.varabyte.kobweb.api.init.InitApiContext

                    @InitApi
                    fun initDatabase(ctx: InitApiContext) {}

                    @InitApi
                    fun initCache(ctx: InitApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.backendData.initMethods.map { it.fqn })
            .containsExactly("example.initDatabase", "example.initCache")
    }

    @Test
    fun `init api without a context is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Init.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.init.InitApi

                    @InitApi
                    fun initDatabase() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.initDatabase` is tagged with the `@InitApi` annotation but doesn't take an InitApiContext as its only parameter."
        )
    }

    @Test
    fun `library apis and init functions are written to library metadata`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.library(projectGroup = "example"),
            sources = mapOf(
                "example/api/Echo.kt" to
                    //language=kotlin
                    """
                    package example.api

                    import com.varabyte.kobweb.api.Api
                    import com.varabyte.kobweb.api.ApiContext

                    @Api
                    suspend fun echo(ctx: ApiContext) {}
                """.trimIndent(),
                "example/Init.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.init.InitApi
                    import com.varabyte.kobweb.api.init.InitApiContext

                    @InitApi
                    fun initDatabase(ctx: InitApiContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.libraryBackendData.apiMethods.map { it.fqn to it.route })
            .containsExactly("example.api.echo" to "/echo")
        assertThat(result.libraryBackendData.initMethods.map { it.fqn })
            .containsExactly("example.initDatabase")
    }
}
