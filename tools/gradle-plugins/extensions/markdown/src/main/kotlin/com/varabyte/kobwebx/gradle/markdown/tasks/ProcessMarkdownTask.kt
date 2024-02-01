package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
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
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject

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
    private val kobwebBlock: KobwebBlock,
    @get:Input val markdownBlock: MarkdownBlock,
) : KobwebTask("Runs the `process` callback registered in the markdown block (which gives the user a chance to generate additional files around all of the markdown resources)") {

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
    fun getGenSrcDir(): File = kobwebBlock.getGenJsSrcRoot<MarkdownBlock>(projectLayout)
        .resolve("process")

    @OutputDirectory
    fun getGenResDir(): File = kobwebBlock.getGenJsResRoot<MarkdownBlock>(projectLayout)

    @TaskAction
    fun execute() {
        val process = markdownBlock.process.orNull ?: return
        val parser = markdownFeatures.createParser()
        val markdownEntries = buildList {
            getMarkdownFiles().get().visit {
                if (isDirectory) return@visit
                val visitor = MarkdownVisitor()
                parser
                    .parse(file.readText())
                    .accept(visitor)
                add(
                    MarkdownEntry(
                        filePath = relativePath.pathString,
                        frontMatter = visitor.frontMatter
                    )
                )
            }
        }
        val processScope = MarkdownBlock.ProcessScope()
        processScope.process(markdownEntries)

        processScope.markdownOutput.forEach { processNode ->
            File(getGenResDir().resolve(markdownBlock.markdownPath.get()), processNode.filePath).let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processNode.content)
            }
        }
        processScope.kotlinOutput.forEach { processNode ->
            File(getGenSrcDir(), processNode.filePath).let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processNode.content)
            }
        }
    }
}
