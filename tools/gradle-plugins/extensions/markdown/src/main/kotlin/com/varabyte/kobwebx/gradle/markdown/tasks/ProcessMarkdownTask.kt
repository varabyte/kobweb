package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.util.RouteUtils
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject
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

abstract class ProcessMarkdownTask @Inject constructor(markdownBlock: MarkdownBlock) :
    MarkdownTask(
        markdownBlock,
        "Runs the `process` callback registered in the markdown block (which gives the user a chance to generate additional files around all of the markdown resources)"
    ) {
    @Nested
    val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    @Nested
    val markdownProcess = markdownBlock.process

    @OutputDirectory
    fun getGenSrcDir() = markdownBlock.getGenJsSrcRoot("process")

    @OutputDirectory
    fun getGenResDir() = markdownBlock.getGenJsResRoot("process")

    @TaskAction
    fun execute() {
        getGenSrcDir().get().asFile.clearDirectory()
        getGenResDir().get().asFile.clearDirectory()
        val process = markdownProcess.orNull ?: return
        val parser = markdownFeatures.createParser()
        val markdownEntries = buildList {
            markdownResources.asFileTree.visit {
                if (isDirectory) return@visit

                val mdFile = file
                val visitor = MarkdownVisitor()

                parser
                    .parse(mdFile.readText())
                    .accept(visitor)
                add(
                    MarkdownEntry(
                        filePath = relativePath.toPath().invariantSeparatorsPathString,
                        frontMatter = visitor.frontMatter,
                        route = RouteUtils.getRoute(
                            relativePath.toPath().toFile(),
                            visitor.frontMatter,
                        ),
                        "${absolutePackageFor(relativePath)}.${funNameFor(mdFile)}"
                    )
                )
            }
        }
        val processScope = MarkdownBlock.ProcessScope()
        processScope.process(markdownEntries)

        val genResRoot = getGenResDir().get().asFile.resolve(markdownPath.get())
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
