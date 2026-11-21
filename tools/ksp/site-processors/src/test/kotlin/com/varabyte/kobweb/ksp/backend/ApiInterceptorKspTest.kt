@file:Suppress("FunctionName", "RedundantSuspendModifier")

package com.varabyte.kobweb.ksp.backend

import com.varabyte.kobweb.ksp.testutil.BackendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appBackendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class ApiInterceptorKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `api interceptor is recorded`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Intercept.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.http.Response
                    import com.varabyte.kobweb.api.intercept.ApiInterceptor
                    import com.varabyte.kobweb.api.intercept.ApiInterceptorContext

                    @ApiInterceptor
                    suspend fun intercept(ctx: ApiInterceptorContext): Response = Response()
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appBackendData.apiInterceptorMethod?.fqn).isEqualTo("example.intercept")
    }

    @Test
    fun `api interceptor which does not return a response is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Intercept.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.intercept.ApiInterceptor
                    import com.varabyte.kobweb.api.intercept.ApiInterceptorContext

                    @ApiInterceptor
                    suspend fun intercept(ctx: ApiInterceptorContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "The method annotated with @ApiInterceptor must return `Response`, got `kotlin.Unit`"
        )
    }

    @Test
    fun `api interceptor which returns a nullable response is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Intercept.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.http.Response
                    import com.varabyte.kobweb.api.intercept.ApiInterceptor
                    import com.varabyte.kobweb.api.intercept.ApiInterceptorContext

                    @ApiInterceptor
                    suspend fun intercept(ctx: ApiInterceptorContext): Response? = null
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "The method annotated with @ApiInterceptor must return `Response`, got `com.varabyte.kobweb.api.http.Response?`"
        )
    }

    @Test
    fun `api interceptor without a context is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Intercept.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.http.Response
                    import com.varabyte.kobweb.api.intercept.ApiInterceptor

                    @ApiInterceptor
                    suspend fun intercept(): Response = Response()
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.intercept` is tagged with the `@ApiInterceptor` annotation but doesn't take an ApiInterceptorContext as its only parameter."
        )
    }

    @Test
    fun `multiple api interceptors is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/Intercept.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.http.Response
                    import com.varabyte.kobweb.api.intercept.ApiInterceptor
                    import com.varabyte.kobweb.api.intercept.ApiInterceptorContext

                    @ApiInterceptor
                    suspend fun intercept(ctx: ApiInterceptorContext): Response = Response()

                    @ApiInterceptor
                    suspend fun interceptAgain(ctx: ApiInterceptorContext): Response = Response()
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly("At most one @ApiInterceptor function is allowed per project.")
    }

    @Test
    fun `api interceptor in a library is an error`() {
        val result = ksp.runBackend(
            options = BackendKspOptions.library(projectGroup = "example"),
            sources = mapOf(
                "example/Intercept.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.api.http.Response
                    import com.varabyte.kobweb.api.intercept.ApiInterceptor
                    import com.varabyte.kobweb.api.intercept.ApiInterceptorContext

                    @ApiInterceptor
                    suspend fun intercept(ctx: ApiInterceptorContext): Response = Response()
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly("@ApiInterceptor functions cannot be defined in library projects.")
    }
}
