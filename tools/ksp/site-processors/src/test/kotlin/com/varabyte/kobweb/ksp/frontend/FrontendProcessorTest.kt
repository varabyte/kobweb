@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class FrontendProcessorTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `page in the pages package gets a top level route`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.route })
            .containsExactly("example.pages.AboutPage" to "/about")
    }

    @Test
    fun `page in a subpackage gets a nested route`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/blog/Post.kt" to
                    //language=kotlin
                    """
                    package example.pages.blog

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun PostPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.route })
            .containsExactly("example.pages.blog.PostPage" to "/blog/post")
    }
}
