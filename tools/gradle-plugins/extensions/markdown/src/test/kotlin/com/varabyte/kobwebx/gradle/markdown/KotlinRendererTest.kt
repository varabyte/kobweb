@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.truthish.assertThat
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory

/** For writing `"""` inside raw strings, which can't contain it directly. */
private const val TRIPLE_QUOTE = "\"\"\""

/** Asserts that this generated code contains [kotlin], after trimming it with [trimIndent]. */
private fun String.assertContainsKotlin(@Language("kotlin") kotlin: String) {
    assertThat(this).contains(kotlin.trimIndent())
}

class KotlinRendererTest {
    private val project = ProjectBuilder.builder().build()
    private val markdownRoot = createTempDirectory("md-root").toFile().also { it.deleteOnExit() }

    /** Fails the test on any report, so that tests notice unexpected warnings. */
    private val failingReporter = object : Reporter {
        override fun error(message: String) = throw AssertionError(message)
        override fun warn(message: String) = throw AssertionError(message)
    }

    /**
     * Renders [markdown] (after trimming it with [trimIndent]) as if it were the file at [filePath] (relative to the
     * Markdown root) in package [pkg].
     *
     * Pass a null [route] for a Markdown file outside the pages package.
     *
     * Set [dependsOnMarkdownArtifact] to simulate a site that depends on Kobweb's optional `kobwebx-markdown` library.
     * With it present, each generated page also registers a `MarkdownContext` (the source file path plus its front
     * matter), which composables on the page can read via `rememberPageContext().markdown`.
     *
     * Silk is off unless [configureHandlers] turns it back on.
     */
    private fun renderMarkdown(
        @Language("markdown") markdown: String,
        filePath: String,
        route: String?,
        funName: String,
        pkg: String = "",
        defaultLayout: String? = null,
        defaultRoot: String? = null,
        imports: List<String> = emptyList(),
        configureHandlers: MarkdownHandlers.() -> Unit = {},
        dependsOnMarkdownArtifact: Boolean = false,
        reporter: Reporter = failingReporter,
    ): String {
        val features = project.objects.newInstance<MarkdownFeatures>()
        val handlers = project.objects.newInstance<MarkdownHandlers>(project).apply {
            useSilk.set(false)
            configureHandlers()
        }

        val mdFile = markdownRoot.resolve(filePath).apply {
            parentFile.mkdirs()
            writeText(markdown.trimIndent())
        }

        val nodeCache = NodeCache(features.createParser(), setOf(markdownRoot.canonicalFile))
        val node = nodeCache[mdFile]
        nodeCache.metadata[node] = NodeCache.Metadata.Entry(
            projectRoot = "site",
            markdownSourceRoot = Path("src/jsMain/resources/markdown"),
            sourceFilePath = Path(filePath),
            outputRootPath = Path(pkg.replace('.', '/')),
            `package` = pkg,
            routeWithSlug = route,
        )

        val renderer = KotlinRenderer(
            projectGroup = "org.example",
            nodeCache = nodeCache,
            defaultLayout = defaultLayout,
            defaultRoot = defaultRoot,
            imports = imports,
            handlers = handlers,
            funName = funName,
            dependsOnMarkdownArtifact = dependsOnMarkdownArtifact,
            reporter = reporter,
        )
        return renderer.render(node)
    }

    @Test
    fun `renders simple formatted markdown as page`() {
        val output = renderMarkdown(
            "Hello *world*",
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            pkg = "org.example.pages",
        )

        output.assertContainsKotlin(
            """
            package org.example.pages

            import androidx.compose.runtime.*
            import com.varabyte.kobweb.core.Page
            import com.varabyte.kobweb.core.data.*
            import com.varabyte.kobweb.core.layout.Layout
            import com.varabyte.kobweb.core.layout.NoLayout
            import com.varabyte.kobweb.core.init.InitRoute
            import com.varabyte.kobweb.core.init.InitRouteContext

            @Page("/hello")
            @Composable
            fun HelloPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello ")
                    org.jetbrains.compose.web.dom.Em {
                        org.jetbrains.compose.web.dom.Text("world")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `renders markdown outside pages as plain composable`() {
        val output = renderMarkdown(
            "Hello",
            filePath = "Greeting.md",
            route = null,
            funName = "Greeting",
            pkg = "org.example.components.sections",
            defaultLayout = ".components.layouts.PageLayout",
            dependsOnMarkdownArtifact = true,
        )

        output.assertContainsKotlin("package org.example.components.sections")
        assertThat(output).doesNotContain("@InitRoute")
        assertThat(output).doesNotContain("@Page")
        assertThat(output).doesNotContain("@Layout")
        output.assertContainsKotlin(
            """
            @Composable
            fun Greeting() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `provides markdown context only when markdown artifact is available`() {
        fun renderPost(dependsOnMarkdownArtifact: Boolean) = renderMarkdown(
            "Hello",
            filePath = "blog/Post.md",
            route = "/blog/post",
            funName = "PostPage",
            pkg = "org.example.pages.blog",
            dependsOnMarkdownArtifact = dependsOnMarkdownArtifact,
        )

        run {
            val output = renderPost(dependsOnMarkdownArtifact = true)

            output.assertContainsKotlin("package org.example.pages.blog")
            output.assertContainsKotlin(
                """
                import com.varabyte.kobwebx.frontmatter.*
                import com.varabyte.kobwebx.markdown.*
                """
            )
            output.assertContainsKotlin(
                """
                @InitRoute
                fun initPostPage(ctx: InitRouteContext) {
                    ctx.data.add(MarkdownContext("blog/Post.md", FrontMatterElement.EmptyMap))
                }

                @Page("/blog/post")
                @Composable
                fun PostPage() {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Hello")
                    }
                }
                """
            )
        }

        run {
            val output = renderPost(dependsOnMarkdownArtifact = false)

            output.assertContainsKotlin("package org.example.pages.blog")
            assertThat(output).doesNotContain("com.varabyte.kobwebx")
            assertThat(output).doesNotContain("@InitRoute")
            assertThat(output).doesNotContain("MarkdownContext")
            output.assertContainsKotlin(
                """
                @Page("/blog/post")
                @Composable
                fun PostPage() {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Hello")
                    }
                }
                """
            )
        }
    }

    @Test
    fun `passes user front matter into markdown context`() {
        val output = renderMarkdown(
            """
            ---
            title: Say "hi"
            tags: [intro, "quoted \"tag\""]
            author:
              name: Jo
              links:
                site: example.com
            layout: .components.layouts.BlogLayout
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
            funName = "PostPage",
            dependsOnMarkdownArtifact = true,
        )

        output.assertContainsKotlin(
            """
            @InitRoute
            fun initPostPage(ctx: InitRouteContext) {
                ctx.data.add(MarkdownContext("blog/Post.md", FrontMatterElement.Builder { addScalar("title", "Say \"hi\""); addList("tags") { addScalar("intro"); addScalar("quoted \"tag\""); }; addMap("author") { addScalar("name", "Jo"); addMap("links") { addScalar("site", "example.com"); }; }; }))
            }

            @Page("/blog/post")
            @Layout(".components.layouts.BlogLayout")
            @Composable
            fun PostPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `front matter can rename generated functions`() {
        val output = renderMarkdown(
            """
            ---
            funName: CustomPage
            ---

            Hello
            """,
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            dependsOnMarkdownArtifact = true,
        )

        output.assertContainsKotlin(
            """
            @InitRoute
            fun initCustomPage(ctx: InitRouteContext) {
                ctx.data.add(MarkdownContext("Hello.md", FrontMatterElement.EmptyMap))
            }

            @Page("/hello")
            @Composable
            fun CustomPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `adds imports from build script and front matter`() {
        val output = renderMarkdown(
            """
            ---
            imports:
              - .components.widgets.*
              - .components.widgets.Button as Btn
              - com.example.util.format
            ---

            Hello
            """,
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            pkg = "org.example.pages",
            imports = listOf(".components.sections.*", "com.example.Shared"),
        )

        output.assertContainsKotlin(
            """
            package org.example.pages

            import androidx.compose.runtime.*
            import com.varabyte.kobweb.core.Page
            import com.varabyte.kobweb.core.data.*
            import com.varabyte.kobweb.core.layout.Layout
            import com.varabyte.kobweb.core.layout.NoLayout
            import com.varabyte.kobweb.core.init.InitRoute
            import com.varabyte.kobweb.core.init.InitRouteContext
            import org.example.components.sections.*
            import com.example.Shared
            import org.example.components.widgets.*
            import org.example.components.widgets.Button as Btn
            import com.example.util.format

            @Page("/hello")
            @Composable
            fun HelloPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `uses default layout`() {
        val output = renderMarkdown(
            "Hello",
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            defaultLayout = ".components.layouts.PageLayout",
        )

        output.assertContainsKotlin(
            """
            @Page("/hello")
            @Layout(".components.layouts.PageLayout")
            @Composable
            fun HelloPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `front matter layout overrides default layout`() {
        val output = renderMarkdown(
            """
            ---
            layout: .components.layouts.BlogLayout
            ---

            Hello
            """,
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            defaultLayout = ".components.layouts.PageLayout",
        )

        output.assertContainsKotlin(
            """
            @Page("/hello")
            @Layout(".components.layouts.BlogLayout")
            @Composable
            fun HelloPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `empty layout in front matter opts out of default layout`() {
        val output = renderMarkdown(
            """
            ---
            layout: ""
            ---

            Hello
            """,
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            defaultLayout = ".components.layouts.PageLayout",
        )

        output.assertContainsKotlin(
            """
            @Page("/hello")
            @NoLayout
            @Composable
            fun HelloPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    // Note: `root` is legacy code. Users are supposed to use `layout` concepts now.
    @Test
    fun `wraps content in default root`() {
        val output = renderMarkdown(
            "Hello",
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            defaultRoot = ".components.layouts.PageLayout",
        )

        output.assertContainsKotlin(
            """
            @Page("/hello")
            @Composable
            fun HelloPage() {
                org.example.components.layouts.PageLayout {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Hello")
                    }
                }
            }
            """
        )
    }

    // Note: `root` is legacy code. Users are supposed to use `layout` concepts now.
    @Test
    fun `front matter root overrides default root`() {
        val output = renderMarkdown(
            """
            ---
            root: .components.layouts.BlogLayout("Blog")
            ---

            Hello
            """,
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            defaultRoot = ".components.layouts.PageLayout",
        )

        output.assertContainsKotlin(
            """
            @Page("/hello")
            @Composable
            fun HelloPage() {
                org.example.components.layouts.BlogLayout("Blog") {
                    org.jetbrains.compose.web.dom.P {
                        org.jetbrains.compose.web.dom.Text("Hello")
                    }
                }
            }
            """
        )
    }

    // Note: `root` is legacy code. Users are supposed to use `layout` concepts now.
    @Test
    fun `ignores root when a layout is set`() {
        val output = renderMarkdown(
            "Hello",
            filePath = "Hello.md",
            route = "/hello",
            funName = "HelloPage",
            defaultLayout = ".components.layouts.PageLayout",
            defaultRoot = ".components.layouts.LegacyRoot",
        )

        output.assertContainsKotlin(
            """
            @Page("/hello")
            @Layout(".components.layouts.PageLayout")
            @Composable
            fun HelloPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    // Note: `root` is legacy code. Users are supposed to use `layout` concepts now.
    @Test
    fun `warns when front matter root is ignored because of default layout`() {
        val warnings = mutableListOf<String>()
        val output = renderMarkdown(
            """
            ---
            root: .components.layouts.LegacyRoot
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
            funName = "PostPage",
            defaultLayout = ".components.layouts.PageLayout",
            reporter = object : Reporter {
                override fun error(message: String) = throw AssertionError(message)
                override fun warn(message: String) {
                    warnings.add(message)
                }
            },
        )

        assertThat(warnings).containsExactly(
            "Markdown file 'blog/Post.md' sets `root` in its front matter, but it is ignored because the build script sets `markdown.defaultLayout`. Consider deleting the unused `root` value."
        )
        output.assertContainsKotlin(
            """
            @Page("/blog/post")
            @Layout(".components.layouts.PageLayout")
            @Composable
            fun PostPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    // Note: `root` is legacy code. Users are supposed to use `layout` concepts now.
    @Test
    fun `warns when front matter root is ignored because of front matter layout`() {
        val warnings = mutableListOf<String>()
        val output = renderMarkdown(
            """
            ---
            root: .components.layouts.LegacyRoot
            layout: .components.layouts.BlogLayout
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
            funName = "PostPage",
            reporter = object : Reporter {
                override fun error(message: String) = throw AssertionError(message)
                override fun warn(message: String) {
                    warnings.add(message)
                }
            },
        )

        assertThat(warnings).containsExactly(
            "Markdown file 'blog/Post.md' sets `root` in its front matter, but it is ignored because its front matter also sets `layout`. Consider deleting the unused `root` value."
        )
        output.assertContainsKotlin(
            """
            @Page("/blog/post")
            @Layout(".components.layouts.BlogLayout")
            @Composable
            fun PostPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `relative route override is relative to the file's folder`() {
        val output = renderMarkdown(
            """
            ---
            routeOverride: first-post
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
            funName = "PostPage",
        )

        output.assertContainsKotlin(
            """
            @Page("/blog/first-post")
            @Composable
            fun PostPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `absolute route override replaces the whole route`() {
        val output = renderMarkdown(
            """
            ---
            routeOverride: /articles/first-post
            ---

            Hello
            """,
            filePath = "blog/Post.md",
            route = "/blog/post",
            funName = "PostPage",
        )

        output.assertContainsKotlin(
            """
            @Page("/articles/first-post")
            @Composable
            fun PostPage() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Hello")
                }
            }
            """
        )
    }

    @Test
    fun `escapes quotes, backslashes and dollars in text`() {
        val output = renderMarkdown(
            """Say "hi" to C:\Users and pay $5""",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.Text("Say \"hi\" to C:\\Users and pay ${'$'}5")
                }
            }
            """
        )
    }

    @Test
    fun `gives each heading a unique id`() {
        val output = renderMarkdown(
            """
            # Getting Started

            ## Setup

            ## Setup
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.H1(attrs = { id("getting-started") }) {
                    org.jetbrains.compose.web.dom.Text("Getting Started")
                }
                org.jetbrains.compose.web.dom.H2(attrs = { id("setup") }) {
                    org.jetbrains.compose.web.dom.Text("Setup")
                }
                org.jetbrains.compose.web.dom.H2(attrs = { id("setup-2") }) {
                    org.jetbrains.compose.web.dom.Text("Setup")
                }
            }
            """
        )
    }

    @Test
    fun `heading ids can be turned off`() {
        val output = renderMarkdown(
            "# Getting Started",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { generateHeaderIds.set(false) },
        )

        output.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.H1 {
                    org.jetbrains.compose.web.dom.Text("Getting Started")
                }
            }
            """
        )
    }

    @Test
    fun `renders links with Silk only when it is available`() {
        fun renderLink(useSilk: Boolean) = renderMarkdown(
            "[Kobweb](https://kobweb.varabyte.com)",
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
            configureHandlers = { this.useSilk.set(useSilk) },
        )

        renderLink(useSilk = true).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    com.varabyte.kobweb.silk.components.navigation.Link("https://kobweb.varabyte.com") {
                        org.jetbrains.compose.web.dom.Text("Kobweb")
                    }
                }
            }
            """
        )
        renderLink(useSilk = false).assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.P {
                    org.jetbrains.compose.web.dom.A("https://kobweb.varabyte.com") {
                        org.jetbrains.compose.web.dom.Text("Kobweb")
                    }
                }
            }
            """
        )
    }

    @Test
    fun `renders code block as raw string`() {
        val output = renderMarkdown(
            $$"""
            ```kotlin
            val price = "$5"
            val quotes = $${TRIPLE_QUOTE}
            ```
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        output.assertContainsKotlin(
            $$"""
            fun Hello() {
                org.jetbrains.compose.web.dom.Pre { org.jetbrains.compose.web.dom.Code { org.jetbrains.compose.web.dom.Text($${TRIPLE_QUOTE}val price = "${'$'}5"
            val quotes = ${"\"\"\""}
            $${TRIPLE_QUOTE}) } }
            }
            """
        )
    }

    @Test
    fun `unwraps paragraphs only in tight lists`() {
        val tightOutput = renderMarkdown(
            """
            - One
            - Two
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )
        val looseOutput = renderMarkdown(
            """
            - One

            - Two
            """,
            filePath = "Hello.md",
            route = null,
            funName = "Hello",
        )

        tightOutput.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Ul {
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.Text("One")
                    }
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.Text("Two")
                    }
                }
            }
            """
        )
        looseOutput.assertContainsKotlin(
            """
            fun Hello() {
                org.jetbrains.compose.web.dom.Ul {
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.P {
                            org.jetbrains.compose.web.dom.Text("One")
                        }
                    }
                    org.jetbrains.compose.web.dom.Li {
                        org.jetbrains.compose.web.dom.P {
                            org.jetbrains.compose.web.dom.Text("Two")
                        }
                    }
                }
            }
            """
        )
    }
}
