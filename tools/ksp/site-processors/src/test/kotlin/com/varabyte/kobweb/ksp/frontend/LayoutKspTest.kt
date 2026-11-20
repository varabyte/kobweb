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
    fun `page with both Layout and NoLayout is an error`() {
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
                    import com.varabyte.kobweb.core.layout.NoLayout

                    @Page
                    @Layout(".components.layouts.PageLayout")
                    @NoLayout
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.pages.AboutPage` is tagged with both conflicting @NoLayout and @Layout annotations. Please remove one of them."
        )
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

    @Test
    fun `page receiver without a layout content receiver is an error`() {
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

                    class PageScope

                    @Page
                    @Layout(".components.layouts.PageLayout")
                    @Composable
                    fun PageScope.AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "Function `example.pages.AboutPage` declares receiver `example.pages.PageScope` but targets layout `example.components.layouts.PageLayout` which does not declare any receiver on its content callback. Please add a receiver to the layout callback or remove the receiver scope from the function."
        )
    }

    @Test
    fun `layout target which does not exist is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
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

        assertThat(result.errors).containsExactly(
            "@Layout(\"example.components.layouts.PageLayout\") does not reference a valid layout method. No methods with that name were found. Please check the spelling of the path."
        )
    }

    @Test
    fun `layout target without a Layout annotation is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable

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

        assertThat(result.errors).containsExactly(
            "@Layout(\"example.components.layouts.PageLayout\") does not reference a valid layout method. Did you forget to add the @Layout annotation on the target method?"
        )
    }

    @Test
    fun `layout with other parameters is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    class Settings

                    @Layout
                    @Composable
                    fun PageLayout(settings: Settings) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "`example.components.layouts.PageLayout` is tagged with the `@Layout` annotation but takes parameters that aren't allowed for layout methods. It should take (optionally) a PageContext parameter and a composable content callback."
        )
    }

    @Test
    fun `page layout without a target is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/pages/About.kt" to
                    //language=kotlin
                    """
                    package example.pages

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.layout.Layout

                    @Page
                    @Layout
                    @Composable
                    fun AboutPage() {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "@Page method \"example.pages.AboutPage\" cannot apply a `@Layout` annotation without specifying a target path. Please add a valid layout path to the layout annotation or remove it."
        )
    }

    @Test
    fun `layout which names itself as its parent is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout(".components.layouts.PageLayout")
                    @Composable
                    fun PageLayout(content: @Composable () -> Unit) {}
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "Layout \"PageLayout\" has a circular dependency with itself."
        )
    }

    @Test
    fun `layouts which name each other as parents is an error`() {
        val result = ksp.runFrontend(
            options = FrontendKspOptions.app(projectGroup = "example"),
            sources = mapOf(
                "example/components/layouts/PageLayout.kt" to
                    //language=kotlin
                    """
                    package example.components.layouts

                    import androidx.compose.runtime.Composable
                    import com.varabyte.kobweb.core.layout.Layout

                    @Layout(".components.layouts.BlogLayout")
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

        assertThat(result.errors).containsExactly(
            "Circular dependency detected: \"PageLayout\" -> \"BlogLayout\" -> \"PageLayout\"",
            "Circular dependency detected: \"BlogLayout\" -> \"PageLayout\" -> \"BlogLayout\"",
        )
    }
}
