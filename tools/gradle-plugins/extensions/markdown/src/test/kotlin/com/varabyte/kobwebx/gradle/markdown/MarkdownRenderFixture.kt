package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.truthish.assertThat
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import org.intellij.lang.annotations.Language
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.io.path.Path

/** For writing `"""` inside raw strings, which can't contain it directly. */
internal const val TRIPLE_QUOTE = "\"\"\""

/** Asserts that this generated code contains [kotlin], after trimming it with [trimIndent]. */
internal fun String.assertContainsKotlin(@Language("kotlin") kotlin: String) {
    assertThat(this).contains(kotlin.trimIndent())
}

/**
 * Renders Markdown files through [KotlinRenderer] for tests.
 *
 * Use it as a JUnit rule (`@get:Rule val fixture = MarkdownRenderFixture()`), which gives each test a fresh temp folder
 * for its Markdown files and deletes it afterward.
 */
class MarkdownRenderFixture : TestRule {
    private val tempFolder = TemporaryFolder()
    private val project = ProjectBuilder.builder().build()
    // Only available while a test runs, once the rule has created the folder
    private val markdownRoot get() = tempFolder.root

    override fun apply(base: Statement, description: Description): Statement = tempFolder.apply(base, description)

    /** Fails the test on any report, so that tests notice unexpected warnings. */
    private val failingReporter = object : Reporter {
        override fun error(message: String) = throw AssertionError(message)
        override fun warn(message: String) = throw AssertionError(message)
    }

    private class OtherFile(val filePath: String, val route: String?)
    private val otherFiles = mutableListOf<OtherFile>()

    /**
     * Adds a Markdown file next to the one [renderMarkdown] renders, e.g. as a target for links.
     *
     * [filePath] and [route] mean the same as in [renderMarkdown].
     */
    fun addMarkdownFile(@Language("markdown") markdown: String, filePath: String, route: String?) {
        writeMarkdownFile(markdown, filePath)
        otherFiles.add(OtherFile(filePath, route))
    }

    private fun writeMarkdownFile(markdown: String, filePath: String) {
        markdownRoot.resolve(filePath).apply {
            parentFile.mkdirs()
            writeText(markdown.trimIndent())
        }
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
        configureFeatures: MarkdownFeatures.() -> Unit = {},
        configureHandlers: MarkdownHandlers.() -> Unit = {},
        dependsOnMarkdownArtifact: Boolean = false,
        reporter: Reporter = failingReporter,
    ): String {
        val features = project.objects.newInstance<MarkdownFeatures>().apply(configureFeatures)
        val handlers = project.objects.newInstance<MarkdownHandlers>(project).apply {
            useSilk.set(false)
            configureHandlers()
        }

        writeMarkdownFile(markdown, filePath)

        val nodeCache = NodeCache(features.createParser(), setOf(markdownRoot.canonicalFile))
        fun register(filePath: String, pkg: String, route: String?) {
            nodeCache.metadata[nodeCache[markdownRoot.resolve(filePath)]] = NodeCache.Metadata.Entry(
                projectRoot = "site",
                markdownSourceRoot = Path("src/jsMain/resources/markdown"),
                sourceFilePath = Path(filePath),
                outputRootPath = Path(pkg.replace('.', '/')),
                `package` = pkg,
                routeWithSlug = route,
            )
        }
        register(filePath, pkg, route)
        otherFiles.forEach { register(it.filePath, pkg = "", it.route) }
        val node = nodeCache[markdownRoot.resolve(filePath)]

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
