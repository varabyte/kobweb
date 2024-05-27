package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.lang.toPackageName
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import org.gradle.api.file.FileTree
import org.gradle.api.file.RelativePath
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import java.io.File
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString

abstract class MarkdownTask @Inject constructor(
    @get:Internal protected val markdownBlock: MarkdownBlock,
    desc: String
) :
    KobwebTask(desc) {

    @get:Input
    abstract val pagesPackage: Property<String>

    @get:Internal
    protected val rootPath get() = Path(markdownBlock.markdownPath.get())

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

    protected fun funNameFor(mdFile: File): String {
        // The suggested replacement for "capitalize" is awful
        @Suppress("DEPRECATION")
        return "${mdFile.nameWithoutExtension.capitalize()}Page"
    }

    protected fun RelativePath.toPath(): Path {
        return rootPath.relativize(Path(this.pathString))
    }

    protected fun packagePartsFor(mdFile: RelativePath): List<String> {
        val mdPathRel = mdFile.toPath().invariantSeparatorsPathString

        val parts = mdPathRel.split('/')
        val dirParts = parts.subList(0, parts.lastIndex)
        return dirParts.map { it.toPackageName() }
    }

    protected fun absolutePackageFor(packageParts: List<String>): String {
        return project.prefixQualifiedPackage(
            pagesPackage.get().packageConcat(packageParts.joinToString("."))
        )
    }

    protected fun absolutePackageFor(mdFile: RelativePath): String {
        return absolutePackageFor(packagePartsFor(mdFile))
    }
}
