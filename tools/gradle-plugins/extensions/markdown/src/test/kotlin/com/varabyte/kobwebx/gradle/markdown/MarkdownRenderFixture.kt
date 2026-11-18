package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.truthish.assertThat
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import org.intellij.lang.annotations.Language
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory

/** For writing `"""` inside raw strings, which can't contain it directly. */
internal const val TRIPLE_QUOTE = "\"\"\""

/** Asserts that this generated code contains [kotlin], after trimming it with [trimIndent]. */
internal fun String.assertContainsKotlin(@Language("kotlin") kotlin: String) {
    assertThat(this).contains(kotlin.trimIndent())
}

/** Renders Markdown files through [KotlinRenderer] for tests. Create one per test, as it writes into a temp folder. */
internal class MarkdownRenderFixture {
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
    fun renderMarkdown(
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
}
