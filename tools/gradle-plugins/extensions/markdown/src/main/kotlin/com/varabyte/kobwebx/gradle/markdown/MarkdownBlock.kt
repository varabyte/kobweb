@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.newInstance

/**
 * A class which represents the metadata associated with a target folder that contains markdown content.
 *
 * This should be instantiated by [MarkdownBlock.createMarkdownFolder] to ensure defaults are set up correctly.
 */
abstract class MarkdownFolder {
    @get:Internal
    abstract val roots: ConfigurableFileCollection

    /**
     * A collection of one (or more) directories that should contain markdown files.
     */
    @get:InputFiles
    val files = roots.asFileTree.matching { include("**/*.md") }

    /**
     * The target package under which we should generate markdown files under.
     *
     * This will default to [MarkdownBlock.defaultPackage] but can be overridden if desired.
     */
    @get:Input
    abstract val targetPackage: Property<String>
}

abstract class MarkdownBlock(
    private val providers: ProviderFactory,
    private val objects: ObjectFactory,
    baseGenDir: Provider<String>,
    pagesPackage: Property<String>,
) : KobwebBlock.FileGeneratingBlock {

    internal fun createMarkdownFolder(): MarkdownFolder {
        return objects.newInstance<MarkdownFolder>().apply {
            targetPackage.set(defaultPackage)
        }
    }

    /**
     * The path under which all markdown resources will live.
     *
     * Defaults to "markdown"
     *
     * This value will be checked when looking through resource directories for markdown files to convert to code.
     * For example, this will find files nested under `src/jsMain/resources/markdown`
     *
     * CAUTION: This should not be confused with [defaultRoot], which is the root composable used to wrap all markdown
     * content.
     */
    abstract val markdownPath: Property<String>

    /**
     * The default package that source code generated from Markdown files will be written under.
     *
     * If the package name begins with a `.`, then it will be appended to your project's group. For example,
     * `".components"` will become `"com.mysite.components"`.
     *
     * By default, this will be set to `".pages"`, as we assume that the main use-case for markdown in Kobweb projects
     * in most cases is to be used as a convenient way to make pages.
     *
     * You can use `markdown.defaultPackage.set(".")` if you want to generate markdown files under the main
     * group of your project, or `markdown.defaultPackage.set("")` if you don't want any package to be set at all.
     *
     * You can override this on a case by case basis using the [addSource] methods, which each take an optional target
     * package.
     */
    abstract val defaultPackage: Property<String>

    // Kobweb defines some default folders (`src/jsMain/resources` and `src/jsMain/kotlin`), but users can add
    // additional custom folders, e.g. a generated folder.
    internal abstract val folders: ListProperty<MarkdownFolder>

    @Suppress("FunctionName")
    private fun _addSource(dir: Any, targetPackage: String? = null) {
        _addSource(dir, providers.provider { targetPackage })
    }
    @Suppress("FunctionName")
    private fun _addSource(dir: Any, targetPackage: Provider<String>) {
        val folder = objects.newInstance<MarkdownFolder>()
        folder.roots.from(dir)
        folder.targetPackage.set(targetPackage.orElse(defaultPackage))

        folders.add(folder)
    }

    /**
     * Adds a directory under which to search for markdown files.
     */
    fun addSource(dir: Directory, targetPackage: Provider<String>) {
        _addSource(dir, targetPackage)
    }

    fun addSource(dir: Directory, targetPackage: String? = null) {
        _addSource(dir, targetPackage)
    }

    fun addSource(dirProvider: Provider<Directory>, targetPackage: Provider<String>) {
        _addSource(dirProvider, targetPackage)
    }

    fun addSource(dirProvider: Provider<Directory>, targetPackage: String? = null) {
        _addSource(dirProvider, targetPackage)
    }

    /**
     * Hooks up a task's output files as directories under which to search for additional markdown files.
     *
     * For example:
     *
     * ```
     * val generateMarkdownTask = tasks.register("generateMarkdown") {
     *     // $name here to create a unique output directory just for this task
     *     val genOutputDir = layout.buildDirectory.dir("generated/$name/src/jsMain/resources/markdown")
     *
     *     outputs.dir(genOutputDir)
     *
     *     doLast {
     *         genOutputDir.get().file("test.md").asFile.apply {
     *             parentFile.mkdirs()
     *             writeText("""
     *                 # TEST
     *             """.trimIndent()
     *             )
     *
     *             println("Generated $absolutePath")
     *         }
     *     }
     * }
     *
     * kobweb.markdown.addSource(generateMarkdownTask)
     * ```
     */
    fun addSource(taskProvider: TaskProvider<*>, targetPackage: Provider<String>) {
        _addSource(taskProvider, targetPackage)
    }

    fun addSource(taskProvider: TaskProvider<*>, targetPackage: String? = null) {
        _addSource(taskProvider, targetPackage)
    }

    /**
     * The root composable to use as a fallback if no other root is provided.
     *
     * All markdown files, when converted to code, should have some root composable it lives within, wrapping all
     * content. It should be an element that has a natural vertical flow to it, such as Compose HTML's `Div` element
     * (the default value) or Kobweb's `Column` composable.
     *
     * This root may be overridden if a `root` value is set inside the markdown's front-matter block.
     *
     * If a site doesn't want any special root element to wrap all markdown source files, then this value can be set to
     * the empty string to disable it.
     */
    abstract val defaultRoot: Property<String>

    /**
     * A list of imports that should be added to the top of every generated markdown file.
     *
     * If an import starts with a ".", it will be prepended with the current site's root package.
     *
     * Finally, you should NOT use the "import" keyword here.
     *
     * For example:
     *
     * ```kotlin
     * markdown {
     *    imports.add(".components.widgets.*")
     * }
     * ```
     * will add `import com.mysite.components.widgets.*` to the top of every generated markdown file.
     */
    abstract val imports: ListProperty<String>

    /**
     * Register a handler which will be triggered with a list of all markdown pages in this project.
     *
     * IMPORTANT: This will only be markdown that represents a page. If you generate markdown that becomes a non-page
     * composable, it won't be triggered by this callback.
     *
     * The markdown files will be partially parsed to include front matter information, plus
     * additional metadata that could be useful for generating some additional content,
     * such as a top-level listing page that links to all markdown pages.
     *
     * If set, this will run before all markdown files are converted (meaning you can
     * potentially add an additional markdown file as a result of this call, although
     * normally we expect users will just generate Kotlin files).
     *
     * @see MarkdownEntry
     */
    abstract val process: Property<ProcessScope.(List<MarkdownEntry>) -> Unit>

    class ProcessScope {
        /**
         * Data used to create a file later.
         *
         * @property filePath A path to a file that should be created. Note that this file will be scoped
         *   to some root directory and is not supposed to escape it. In other words, using ".." will result
         *   in an exception.
         */
        data class OutputFile(
            val filePath: String,
            val content: String,
        ) {
            init {
                require(!filePath.contains("..")) { "Relative paths are not allowed. Invalid: \"$filePath\"" }
            }
        }

        private val _markdownOutputs = mutableListOf<OutputFile>()
        internal val markdownOutputs: List<OutputFile> = _markdownOutputs
        private val _kotlinOutputs = mutableListOf<OutputFile>()
        internal val kotlinOutputs: List<OutputFile> = _kotlinOutputs
        private val _resourceOutputs = mutableListOf<OutputFile>()
        internal val resourceOutputs: List<OutputFile> = _resourceOutputs

        /**
         * Generate Kotlin source in the final project.
         *
         * @property filePath A path to a Kotlin file, which will automatically be put under a generated `src/jsMain/kotlin` directory.
         *   It is an error if the path does not end in `.kt`
         */
        fun generateKotlin(filePath: String, content: String) {
            require(filePath.endsWith(".kt")) { "Expected a path that ends with .kt, got \"$filePath\"" }
            _kotlinOutputs.add(OutputFile(filePath, content))
        }

        /**
         * Generate a Markdown resource.
         *
         * This file will automatically be picked up and converted to Kotlin code in the final project.
         *
         * @property filePath A path to a Markdown file, which will automatically be put under a generated
         *   `src/resources/markdown` directory. It is an error if the path does not end in `.md`
         */
        fun generateMarkdown(filePath: String, content: String) {
            require(filePath.endsWith(".md")) { "Expected a path that ends with .md, got \"$filePath\"" }
            _markdownOutputs.add(OutputFile(filePath, content))
        }

        /**
         * Generate a general resource for this site.
         *
         * NOTE: If you are trying to specifically create a new markdown file (e.g. like a listing page), you should
         * prefer calling [generateMarkdown] directly instead.
         */
        fun generateResource(filePath: String, content: String) {
            _resourceOutputs.add(OutputFile(filePath, content))
        }
    }

    init {
        markdownPath.convention("markdown")

        defaultPackage.convention(pagesPackage)
        defaultRoot.convention("org.jetbrains.compose.web.dom.Div")
        imports.set(emptyList())
        genDir.convention(baseGenDir.map { "$it/markdown" })
    }
}
