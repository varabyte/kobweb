package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.getBuildScripts
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.util.RouteUtils
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.gradle.api.file.FileTree
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString

private class MarkdownVisitor : AbstractVisitor() {
    private val _frontMatter = mutableMapOf<String, List<String>>()
    val frontMatter: Map<String, List<String>> = _frontMatter

    override fun visit(customBlock: CustomBlock) {
        if (customBlock is YamlFrontMatterBlock) {
            val yamlVisitor = YamlFrontMatterVisitor()
            customBlock.accept(yamlVisitor)
            _frontMatter.putAll(
                yamlVisitor.data
                    .mapValues { (_, values) ->
                        values.map { it.yamlStringToKotlinString() }
                    }
            )
        }
    }
}

abstract class ProcessMarkdownTask @Inject constructor(
    private val markdownBlock: MarkdownBlock,
) : KobwebTask("Runs the `process` callback registered in the markdown block (which gives the user a chance to generate additional files around all of the markdown resources)") {

    // Use changing the build script as a proxy for changing markdownBlock or kobwebBlock values.
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    fun getBuildScripts(): List<File> = projectLayout.getBuildScripts()

    private val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    @get:Internal
    abstract val resources: Property<FileTree>

    @InputFiles
    fun getMarkdownFiles(): Provider<FileTree> {
        return resources.zip(markdownBlock.markdownPath) { fileTree, path ->
            fileTree.matching { include("$path/**/*.md") }
        }
    }

    @OutputDirectory
    fun getGenSrcDir() = markdownBlock.getGenJsSrcRoot("process")

    @OutputDirectory
    fun getGenResDir() = markdownBlock.getGenJsResRoot()

    @TaskAction
    fun execute() {
        val process = markdownBlock.process.orNull ?: return
        val parser = markdownFeatures.createParser()
        val markdownEntries = buildList {
            val rootPath = Path(markdownBlock.markdownPath.get())
            getMarkdownFiles().get().visit {
                if (isDirectory) return@visit

                val fullPath = Path(relativePath.pathString)
                val relativePath = rootPath.relativize(fullPath)
                val visitor = MarkdownVisitor()
                parser
                    .parse(file.readText())
                    .accept(visitor)
                add(
                    @Suppress("DEPRECATION") // routeOverride supported for legacy codebases
                    MarkdownEntry(
                        filePath = relativePath.invariantSeparatorsPathString,
                        frontMatter = visitor.frontMatter,
                        route = RouteUtils.getRoute(
                            relativePath.toFile(),
                            visitor.frontMatter,
                            markdownBlock.filenameToSlug.orNull ?: markdownBlock.routeOverride.orNull,
                        )
                    )
                )
            }
        }
        val processScope = MarkdownBlock.ProcessScope()
        processScope.process(markdownEntries)

        val genResRoot = getGenResDir().get().asFile.resolve(markdownBlock.markdownPath.get())
        processScope.markdownOutput.forEach { processNode ->
            File(genResRoot, processNode.filePath).let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processNode.content)
            }
        }
        processScope.kotlinOutput.forEach { processNode ->
            File(getGenSrcDir().get().asFile, processNode.filePath).let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processNode.content)
            }
        }
    }
}
