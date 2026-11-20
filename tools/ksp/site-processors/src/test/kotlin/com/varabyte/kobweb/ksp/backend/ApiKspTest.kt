@file:Suppress("FunctionName", "RedundantSuspendModifier")

package com.varabyte.kobweb.ksp.backend

import com.varabyte.kobweb.ksp.testutil.BackendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appBackendData
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
}
