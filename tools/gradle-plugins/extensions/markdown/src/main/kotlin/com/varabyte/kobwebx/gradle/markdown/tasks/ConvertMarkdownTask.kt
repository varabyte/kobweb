package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.lang.toPackageName
import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.util.LoggingReporter
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.core.util.getResourceRoots
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.MarkdownHandlers
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.io.IOException
import javax.inject.Inject

abstract class ConvertMarkdownTask @Inject constructor(
    private val kobwebBlock: KobwebBlock,
    private val markdownBlock: MarkdownBlock,
) : DefaultTask() {
    init {
        description = "Convert markdown files found in the project's resources path to source code in the final project"
    }

    private val markdownHandlers = markdownBlock.extensions.getByType<MarkdownHandlers>()
    private val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    private fun getMarkdownRoots(): Sequence<File> = project.getResourceRoots(project.jsTarget)
        .map { root -> root.resolve(markdownBlock.markdownPath.get()) }

    private fun getMarkdownFilesWithRoots(): List<RootAndFile> {
        val mdRoots = getMarkdownRoots()
        return project.getResourceFilesWithRoots(project.jsTarget)
            .filter { rootAndFile -> rootAndFile.file.extension == "md" }
            .mapNotNull { rootAndFile ->
                mdRoots.find { mdRoot -> rootAndFile.file.startsWith(mdRoot) }
                    ?.let { mdRoot -> RootAndFile(mdRoot, rootAndFile.file) }
            }
            .toList()
    }

    @InputFiles
    fun getMarkdownFiles(): List<File> {
        return getMarkdownFilesWithRoots().map { it.file }
    }

    @OutputDirectory
    fun getGenDir(): File = kobwebBlock.getGenJsSrcRoot<MarkdownBlock>(project).resolve(
        project.prefixQualifiedPackage(kobwebBlock.pagesPackage.get()).replace(".", "/")
    )

    @TaskAction
    fun execute() {
        val cache = NodeCache(markdownFeatures.createParser(), getMarkdownRoots().toList())
        getMarkdownFilesWithRoots().forEach { rootAndFile ->
            val mdFile = rootAndFile.file
            val mdPathRel = rootAndFile.relativeFile.toUnixSeparators()

            val parts = mdPathRel.split('/')
            val dirParts = parts.subList(0, parts.lastIndex)
            val packageParts = dirParts.map { it.toPackageName() }

            for (i in dirParts.indices) {
                if (dirParts[i] != packageParts[i]) {
                    // If not a match, that means the path that the markdown file is coming from is not compatible with
                    // Java package names, e.g. "2021" was converted to "_2021". This is fine -- we just need to tell
                    // Kobweb about the mapping.

                    val subpackage = packageParts.subList(0, i + 1)

                    File(getGenDir(), "${subpackage.joinToString("/")}/PackageMapping.kt")
                        // Multiple markdown files in the same folder will try to write this over and over again; we
                        // can skip after the first time
                        .takeIf { !it.exists() }
                        ?.let { mappingFile ->
                            mappingFile.parentFile.mkdirs()
                            mappingFile.writeText(
                                """
                                @file:PackageMapping("${dirParts[i]}")

                                package ${
                                    project.prefixQualifiedPackage(
                                        kobwebBlock.pagesPackage.get().packageConcat(
                                            subpackage.joinToString(".")
                                        )
                                    )
                                }

                                import com.varabyte.kobweb.core.PackageMapping
                            """.trimIndent()
                            )
                        }
                }
            }

            val ktFileName = mdFile.nameWithoutExtension
            File(getGenDir(), "${packageParts.joinToString("/")}/$ktFileName.kt").let { outputFile ->
                outputFile.parentFile.mkdirs()
                val mdPackage = project.prefixQualifiedPackage(
                    kobwebBlock.pagesPackage.get().packageConcat(packageParts.joinToString("."))
                )

                // The suggested replacement for "capitalize" is awful
                @Suppress("DEPRECATION")
                val funName = "${ktFileName.capitalize()}Page"
                val ktRenderer = KotlinRenderer(
                    project,
                    cache::getRelative,
                    markdownBlock.imports.get(),
                    mdPathRel,
                    markdownHandlers,
                    mdPackage,
                    markdownBlock.routeOverride.orNull,
                    funName,
                    LoggingReporter(logger),
                )
                outputFile.writeText(ktRenderer.render(cache.get(mdFile)))
            }
        }
    }

    private class NodeCache(private val parser: Parser, private val roots: List<File>) {
        private val existingNodes = mutableMapOf<String, Node>()

        fun get(file: File): Node = existingNodes.computeIfAbsent(file.canonicalFile.toUnixSeparators()) {
            parser.parse(file.readText())
        }

        fun getRelative(relPath: String): Node? = try {
            roots.asSequence()
                .map { it to it.resolve(relPath).canonicalFile }
                // Make sure we don't access anything outside our markdown roots
                .firstOrNull { (root, canonicalFile) -> canonicalFile.exists() && canonicalFile.isFile && canonicalFile.startsWith(root) }
                ?.second?.let(::get)
        } catch (ignored: IOException) {
            null
        }
    }
}
