@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class AppKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `app function is recorded as the app entry`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/AppEntry.kt" to
                    //language=kotlin
                    """
                    package example

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.App

                    @App
                    @Composable
                    fun AppEntry(content: @Composable () -> Unit) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.appEntry?.fqn).isEqualTo("example.AppEntry")
    }

    @Test
    fun `app entry is null without an app function`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/Index.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun HomePage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.appEntry).isNull()
    }

    @Test
    fun `multiple app functions is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/AppEntry.kt" to
                    //language=kotlin
                    """
                    package example

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.App

                    @App
                    @Composable
                    fun AppEntry(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/OtherAppEntry.kt" to
                    //language=kotlin
                    """
                    package example

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.App

                    @App
                    @Composable
                    fun OtherAppEntry(content: @Composable () -> Unit) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly("At most one @App function is allowed per project.")
    }

    @Test
    fun `app function in a library is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.library(projectGroup = "example"),
            sources = mapOf(
                "example/AppEntry.kt" to
                    //language=kotlin
                    """
                    package example

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.App

                    @App
                    @Composable
                    fun AppEntry(content: @Composable () -> Unit) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly("@App functions cannot be defined in library projects.")
    }
}
