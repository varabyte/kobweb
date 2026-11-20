@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp.frontend

import com.varabyte.kobweb.ksp.testutil.FrontendKspOptions
import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.appFrontendData
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class LayoutKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `page uses the layout it names`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.layout.Layout

                    @Page
                    @Layout(".components.layouts.PageLayout")
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        val data = result.appFrontendData.frontendData
        assertThat(data.layouts.map { it.fqn to it.acceptsContext })
            .containsExactly("example.components.layouts.PageLayout" to false)
        assertThat(data.pages.map { it.fqn to it.layoutFqn })
            .containsExactly("example.pages.AboutPage" to "example.components.layouts.PageLayout")
    }

    @Test
    fun `layout can take a page context`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.PageContext
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.layouts.map { it.fqn to it.acceptsContext })
            .containsExactly("example.components.layouts.PageLayout" to true)
    }

    @Test
    fun `layout can name a parent layout`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/components/layouts/BlogLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout(".components.layouts.PageLayout")
                    @Composable
                    fun BlogLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.layouts.map { it.fqn to it.parentLayoutFqn })
            .containsExactly(
                "example.components.layouts.PageLayout" to null,
                "example.components.layouts.BlogLayout" to "example.components.layouts.PageLayout",
            )
    }

    @Test
    fun `default layout applies to pages in its package and subpackages`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/pages/DefaultLayout.kt" to
                    //language=kotlin
                    """
                    @file:Layout(".components.layouts.PageLayout")

                    package example.pages

                    import com.varabyte.kobweb.core.layout.Layout
                """.trimIndent(),
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
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.layoutFqn })
            .containsExactly(
                "example.pages.AboutPage" to "example.components.layouts.PageLayout",
                "example.pages.blog.PostPage" to "example.components.layouts.PageLayout",
            )
    }

    @Test
    fun `the most specific default layout wins`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/components/layouts/BlogLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun BlogLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/pages/DefaultLayout.kt" to
                    //language=kotlin
                    """
                    @file:Layout(".components.layouts.PageLayout")

                    package example.pages

                    import com.varabyte.kobweb.core.layout.Layout
                """.trimIndent(),
                "example/pages/blog/DefaultLayout.kt" to
                    //language=kotlin
                    """
                    @file:Layout(".components.layouts.BlogLayout")

                    package example.pages.blog

                    import com.varabyte.kobweb.core.layout.Layout
                """.trimIndent(),
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
                "example/pages/blog/posts/First.kt" to
                    //language=kotlin
                    """
                    package example.pages.blog.posts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page

                    @Page
                    @Composable
                    fun FirstPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.layoutFqn })
            .containsExactly(
                "example.pages.AboutPage" to "example.components.layouts.PageLayout",
                "example.pages.blog.posts.FirstPage" to "example.components.layouts.BlogLayout",
            )
    }

    @Test
    fun `page layout overrides the default layout`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/components/layouts/WideLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun WideLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/pages/DefaultLayout.kt" to
                    //language=kotlin
                    """
                    @file:Layout(".components.layouts.PageLayout")

                    package example.pages

                    import com.varabyte.kobweb.core.layout.Layout
                """.trimIndent(),
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.layout.Layout

                    @Page
                    @Layout(".components.layouts.WideLayout")
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.layoutFqn })
            .containsExactly("example.pages.AboutPage" to "example.components.layouts.WideLayout")
    }

    @Test
    fun `NoLayout opts a page out of the default layout`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
                "example/pages/DefaultLayout.kt" to
                    //language=kotlin
                    """
                    @file:Layout(".components.layouts.PageLayout")

                    package example.pages

                    import com.varabyte.kobweb.core.layout.Layout
                """.trimIndent(),
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.layout.NoLayout

                    @Page
                    @NoLayout
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.appFrontendData.frontendData.pages.map { it.fqn to it.layoutFqn })
            .containsExactly("example.pages.AboutPage" to "com.varabyte.kobweb.core.layout.NoLayout")
    }

    @Test
    fun `page receiver matches the layout content receiver`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    class PageScope

                    @Layout
                    @Composable
                    fun PageLayout(content: @Composable PageScope.() -> Unit) {}
                """.trimIndent(),
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.layout.Layout
                    import example.components.layouts.PageScope

                    @Page
                    @Layout(".components.layouts.PageLayout")
                    @Composable
                    fun PageScope.AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        val data = result.appFrontendData.frontendData
        assertThat(data.layouts.map { it.fqn to it.contentReceiverFqn })
            .containsExactly("example.components.layouts.PageLayout" to "example.components.layouts.PageScope")
        assertThat(data.pages.map { it.fqn to it.receiverFqn })
            .containsExactly("example.pages.AboutPage" to "example.components.layouts.PageScope")
    }
}
