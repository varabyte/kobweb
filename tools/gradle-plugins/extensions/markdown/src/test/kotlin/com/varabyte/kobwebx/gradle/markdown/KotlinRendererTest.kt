package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.truthish.assertThat
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory

class KotlinRendererTest {
    @Test
    fun `renders simple formatted markdown as page`() {
        val project = ProjectBuilder.builder().build()
        val features = project.objects.newInstance<MarkdownFeatures>()
        val handlers = project.objects.newInstance<MarkdownHandlers>(project).apply {
            useSilk.set(false)
        }

        val root = createTempDirectory("md-root").toFile().also { it.deleteOnExit() }
        val mdFile = root.resolve("Hello.md").apply { writeText("Hello *world*") }

        val nodeCache = NodeCache(features.createParser(), setOf(root.canonicalFile))
        val node = nodeCache[mdFile]
        nodeCache.metadata[node] = NodeCache.Metadata.Entry(
            projectRoot = "site",
            markdownSourceRoot = Path("src/jsMain/resources/markdown"),
            sourceFilePath = Path("Hello.md"),
            outputRootPath = Path("org/example/pages"),
            `package` = "org.example.pages",
            routeWithSlug = "/hello",
        )

        val renderer = KotlinRenderer(
            projectGroup = "org.example",
            nodeCache = nodeCache,
            defaultLayout = null,
            defaultRoot = null,
            imports = emptyList(),
            handlers = handlers,
            funName = "HelloPage",
            dependsOnMarkdownArtifact = false,
            reporter = object : Reporter {
                override fun error(message: String) = throw AssertionError(message)
                override fun warn(message: String) = throw AssertionError(message)
            },
        )

        assertThat(renderer.render(node)).isEqualTo(
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

            """.trimIndent()
        )
    }
}
