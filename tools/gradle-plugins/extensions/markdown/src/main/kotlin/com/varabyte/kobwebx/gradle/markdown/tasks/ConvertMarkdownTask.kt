package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.lang.toPackageName
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.LoggingReporter
import com.varabyte.kobweb.gradle.core.util.getBuildScripts
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.MarkdownHandlers
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString

abstract class ConvertMarkdownTask @Inject constructor(private val markdownBlock: MarkdownBlock) :
    KobwebTask("Convert markdown files found in the project's resources path to source code in the final project") {

    // Use changing the build script as a proxy for changing markdownBlock or kobwebBlock values.
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    fun getBuildScripts(): List<File> = projectLayout.getBuildScripts()

    private val markdownHandlers = markdownBlock.extensions.getByType<MarkdownHandlers>()
    private val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Input
    abstract val pagesPackage: Property<String>

    @get:Internal
    abstract val resources: Property<SourceDirectorySet>

    @InputFiles
    fun getMarkdownRoots(): Provider<List<File>> = resources.map {
        it.srcDirs.map { root -> root.resolve(markdownBlock.markdownPath.get()) }
    }

    @InputFiles
    fun getMarkdownResources(): Provider<FileTree> {
        return resources.zip(markdownBlock.markdownPath) { fileTree, path ->
            fileTree.matching { include("$path/**/*.md") }
        }
    }

    @get:InputDirectory
    abstract val generatedMarkdownDir: DirectoryProperty

    @OutputDirectory
    fun getGenDir(): Provider<Directory> {
        return markdownBlock.getGenJsSrcRoot().zip(pagesPackage) { genRoot, pagesPackage ->
            genRoot.dir(project.prefixQualifiedPackage(pagesPackage).replace(".", "/"))
        }
    }

    @TaskAction
    fun execute() {
        val cache = NodeCache(
            parser = markdownFeatures.createParser(),
            roots = getMarkdownRoots().get() + generatedMarkdownDir.asFileTree
        )
        val markdownFiles = getMarkdownResources().get() + objectFactory.fileTree().setDir(generatedMarkdownDir)

        val rootPath = Path(markdownBlock.markdownPath.get())
        markdownFiles.visit {
            if (isDirectory) return@visit
            val mdFile = this.file
            val fullPath = Path(relativePath.pathString)
            val mdPathRel = rootPath.relativize(fullPath).invariantSeparatorsPathString

            val parts = mdPathRel.split('/')
            val dirParts = parts.subList(0, parts.lastIndex)
            val packageParts = dirParts.map { it.toPackageName() }

            val ktFileName = mdFile.nameWithoutExtension
            File(getGenDir().get().asFile, "${packageParts.joinToString("/")}/$ktFileName.kt").let { outputFile ->
                outputFile.parentFile.mkdirs()
                val mdPackage = project.prefixQualifiedPackage(
                    pagesPackage.get().packageConcat(packageParts.joinToString("."))
                )

                // The suggested replacement for "capitalize" is awful
                @Suppress("DEPRECATION")
                val funName = "${ktFileName.capitalize()}Page"

                val ktRenderer = KotlinRenderer(
                    project,
                    cache::getRelative,
                    markdownBlock.defaultRoot.orNull ?: @Suppress("DEPRECATION") markdownHandlers.defaultRoot.orNull,
                    markdownBlock.imports.get(),
                    mdPathRel,
                    markdownHandlers,
                    mdPackage,
                    funName,
                    LoggingReporter(logger),
                )
                outputFile.writeText(ktRenderer.render(cache[mdFile]))
            }
        }
    }

    /**
     * Class which maintains a cache of parsed markdown content associated with their source files.
     *
     * This cache is useful because Markdown files can reference other Markdown files, meaning as we process a
     * collection of them, we might end up referencing the same file multiple times.
     *
     * Note that this cache should not be created with too long a lifetime, because users may edit Markdown files and
     * those changes should be picked up. It is intended to be used only for a single processing run across a collection
     * of markdown files and then discarded.
     *
     * @param parser The parser to use to parse markdown files.
     * @param roots A collection of root folders under which Markdown files should be considered for processing. Any
     *   markdown files referenced outside of these roots should be ignored for caching purposes.
     */
    private class NodeCache(private val parser: Parser, private val roots: List<File>) {
        private val existingNodes = mutableMapOf<String, Node>()

        /**
         * Returns a parsed Markdown [Node] for the target file (which is expected to be a valid markdown file).
         *
         * Once queried, the node will be cached so that subsequent calls to this method will not re-read the file. If
         * the file fails to parse, this method will throw an exception.
         */
        operator fun get(file: File): Node = file.canonicalFile.let { canonicalFile ->
            require(roots.any { canonicalFile.startsWith(it) }) {
                "File $canonicalFile is not under any of the specified Markdown roots: $roots"
            }
            existingNodes.computeIfAbsent(canonicalFile.invariantSeparatorsPath) {
                parser.parse(canonicalFile.readText())
            }
        }

        /**
         * Returns a parsed Markdown node given a relative path which will be resolved against all markdown roots.
         *
         * For example, "test/example.md" will return parsed markdown information if found in
         * `src/jsMain/resources/markdown/test/example.md`.
         *
         * This will return null if:
         * * no file is found matching the passed in path.
         * * the file at the specified location fails to parse.
         * * the relative file path escapes the current root, e.g. `../public/files/license.md`, as this could be a
         *   useful way to link to a raw markdown file that should be served as is and not converted into an html page.
         */
        fun getRelative(relPath: String): Node? = try {
            roots.asSequence()
                .map { it to it.resolve(relPath).canonicalFile }
                // Make sure we don't access anything outside our markdown roots
                .firstOrNull { (root, canonicalFile) ->
                    canonicalFile.exists() && canonicalFile.isFile && canonicalFile.startsWith(root)
                }?.second?.let(::get)
        } catch (ignored: IOException) {
            null
        }
    }
}
