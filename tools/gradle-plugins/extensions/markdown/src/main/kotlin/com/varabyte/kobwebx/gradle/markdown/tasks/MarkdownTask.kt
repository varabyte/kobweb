package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.lang.toPackageName
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RelativePath
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
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
) : KobwebTask(desc) {
    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Input
    abstract val pagesPackage: Property<String>

    @get:Input
    abstract val projectGroup: Property<Any>

    /**
     * A list of all directories containing markdown files to process.
     *
     * See also: [markdownResources]
     */
    @get:Internal
    abstract val markdownDirs: ConfigurableFileCollection

    /**
     * A list of all markdown files to process.
     */
    @get:InputFiles
    protected val markdownResources get(): FileCollection {
        return markdownDirs.asFileTree.matching { include("**/*.md") }
    }

    @get:Input
    val markdownPath = markdownBlock.markdownPath

    protected fun funNameFor(mdFile: File): String {
        // The suggested replacement for "capitalize" is awful
        @Suppress("DEPRECATION")
        return "${mdFile.nameWithoutExtension.capitalize()}Page"
    }

    protected fun RelativePath.toPath(): Path {
        return Path(this.pathString)
    }

    /** Recursively delete the contents of this directory without deleting the directory itself. */
    protected fun File.clearDirectory() {
        deleteRecursively()
        mkdirs()
    }

    protected fun packagePartsFor(mdFile: RelativePath): List<String> {
        val mdPathRel = mdFile.toPath().invariantSeparatorsPathString

        val parts = mdPathRel.split('/')
        val dirParts = parts.subList(0, parts.lastIndex)
        return dirParts.map { it.toPackageName() }
    }

    protected fun absolutePackageFor(packageParts: List<String>): String {
        return PackageUtils.resolvePackageShortcut(
            projectGroup.get().toString(),
            pagesPackage.get().packageConcat(packageParts.joinToString("."))
        )
    }

    protected fun absolutePackageFor(mdFile: RelativePath): String {
        return absolutePackageFor(packagePartsFor(mdFile))
    }
}
