@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class PageKspTest {
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

    @Test
    fun `package mapping renames its package in routes`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/users/user/PackageMapping.kt" to
                    //language=kotlin
                    """
                    @file:PackageMapping("{}")

                    package example.pages.users.user

                    import com.varabyte.kobweb.core.PackageMapping
                """.trimIndent(),
                "example/pages/users/user/Profile.kt" to
                    //language=kotlin
                    """
                    package example.pages.users.user

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun ProfilePage() {}
                """.trimIndent(),
                "example/pages/users/user/posts/Index.kt" to
                    //language=kotlin
                    """
                    package example.pages.users.user.posts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun PostsPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.route })
            .containsExactly(
                "example.pages.users.user.ProfilePage" to "/users/{user}/profile",
                "example.pages.users.user.posts.PostsPage" to "/users/{user}/posts/",
            )
    }

    @Test
    fun `package mapping outside the pages package is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/PackageMapping.kt" to
                    //language=kotlin
                    """
                    @file:PackageMapping("widgets")

                    package example.components

                    import com.varabyte.kobweb.core.PackageMapping
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`@file:PackageMapping` is defined under package `example.components` but must exist under `example.pages`."
        )
    }

    @Test
    fun `init route is attached to the page in its file`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.init.InitRoute
                    import com.varabyte.kobweb.core.init.InitRouteContext

                    @InitRoute
                    fun initAboutPage(ctx: InitRouteContext) {}

                    @Page
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
                "example/pages/Contact.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun ContactPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.initRouteFqn })
            .containsExactly(
                "example.pages.AboutPage" to "example.pages.initAboutPage",
                "example.pages.ContactPage" to null,
            )
    }

    @Test
    fun `init route without a page or layout in its file is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import com.varabyte.kobweb.core.init.InitRoute
                    import com.varabyte.kobweb.core.init.InitRouteContext

                    @InitRoute
                    fun initAboutPage(ctx: InitRouteContext) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "@InitRoute method \"example.pages.initAboutPage\" must be defined in the same file as a @Page or @Layout method."
        )
    }

    @Test
    fun `multiple init routes in one file is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.init.InitRoute
                    import com.varabyte.kobweb.core.init.InitRouteContext

                    @InitRoute
                    fun initAboutPage(ctx: InitRouteContext) {}

                    @InitRoute
                    fun initAboutPageAgain(ctx: InitRouteContext) {}

                    @Page
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "Only one @InitRoute method per file is allowed, but found multiple: initAboutPage, initAboutPageAgain. Please remove all but one."
        )
    }
}
