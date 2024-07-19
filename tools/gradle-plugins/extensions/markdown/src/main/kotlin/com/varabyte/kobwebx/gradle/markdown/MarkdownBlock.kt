@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

abstract class MarkdownBlock(baseGenDir: Provider<String>) : KobwebBlock.FileGeneratingBlock {
    /**
     * The path to all markdown resources to process.
     *
     * This path should live in the root of the project's `resources` folder, e.g. `src/jsMain/resources`
     */
    abstract val markdownPath: Property<String>

    /**
     * The root composable to use as a fallback if no other root is provided.
     *
     * All markdown files, when converted to code, should have some root composable called first, wrapping all content.
     * This root is often done set a YAML block, but we should handle the case where one is not specified (either
     * because the YAML block doesn't include it or when there's no YAML block at all). This is because Kobweb's default
     * layouts use a Box as the root, which just stacks elements on top of each other.
     *
     * Note that a user might have overridden the Kobweb default root layout via the `@App` annotation, in which case,
     * they may not need to specify a root at all here (since that could just add an unnecessary extra layer to the DOM
     * tree). To indicate this (expectedly rare) case, this value may be set to the empty string (or `null as String?`)
     * to disable it.
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
     * Register a handler which will be triggered with a list of all markdown files in this project.
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

        private val _markdownOutput = mutableListOf<OutputFile>()
        internal val markdownOutput: List<OutputFile> = _markdownOutput
        private val _kotlinOutput = mutableListOf<OutputFile>()
        internal val kotlinOutput: List<OutputFile> = _kotlinOutput

        /**
         * Generate Kotlin source in the final project.
         *
         * @property filePath A path to a Kotlin file, which will automatically be put under a generated `src/jsMain/kotlin` directory.
         *   It is an error if the path does not end in `.kt`
         */
        fun generateKotlin(filePath: String, content: String) {
            require(filePath.endsWith(".kt")) { "Expected a path that ends with .kt, got \"$filePath\"" }
            _kotlinOutput.add(OutputFile(filePath, content))
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
            _markdownOutput.add(OutputFile(filePath, content))
        }
    }

    init {
        markdownPath.convention("markdown")
        defaultRoot.convention("com.varabyte.kobweb.compose.foundation.layout.Column")
        imports.set(emptyList())
        genDir.convention(baseGenDir.map { "$it/markdown" })
    }
}
