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

    @Test
    fun `index files get the route of their package`() {
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
                "example/pages/blog/Index.kt" to
                    //language=kotlin
                    """
                    package example.pages.blog

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun BlogPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.route })
            .containsExactly(
                "example.pages.HomePage" to "/",
                "example.pages.blog.BlogPage" to "/blog/",
            )
    }

    @Test
    fun `route override replaces the file name`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/blog/Post.kt" to
                    //language=kotlin
                    """
                    package example.pages.blog

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page("{}")
                    @Composable
                    fun PostPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.route })
            .containsExactly("example.pages.blog.PostPage" to "/blog/{post}")
    }

    @Test
    fun `pages package can be configured`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(
                projectGroup = "example",
                pagesPackage = ".site.routes",
            ),
            sources = mapOf(
                "example/site/routes/About.kt" to
                    //language=kotlin
                    """
                    package example.site.routes

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
            .containsExactly("example.site.routes.AboutPage" to "/about")
    }

    @Test
    fun `page can take a page context`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.PageContext

                    @Page
                    @Composable
                    fun AboutPage(ctx: PageContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.acceptsContext })
            .containsExactly("example.pages.AboutPage" to true)
    }

    @Test
    fun `page with other parameters is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    class Settings

                    @Page
                    @Composable
                    fun AboutPage(settings: Settings) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.pages.AboutPage` is tagged with the `@Page` annotation but takes parameters that aren't allowed for page methods. It should take (optionally) a PageContext parameter or nothing."
        )
    }

    @Test
    fun `page outside the pages package is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/About.kt" to
                    //language=kotlin
                    """
                    package example.components

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`@Page fun AboutPage` is defined under package `example.components` but must exist under `example.pages` (or use a route override that starts with a slash)."
        )
    }

    @Test
    fun `page which is not composable is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import com.varabyte.kobweb.core.Page

                    @Page
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`fun AboutPage` annotated with `@Page` must also be `@Composable`."
        )
    }
}
