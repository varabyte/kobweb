package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.common.text.suffixIfNot
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownFolder
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.RelativePath
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import java.io.File
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo

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
     * See also: [markdownFiles]
     */
    @get:Nested
    abstract val markdownFolders: ListProperty<MarkdownFolder>

    /**
     * A list of all markdown files to process.
     */
    @get:InputFiles
    protected val markdownFiles get(): FileTree {
        val files = objectFactory.fileCollection()
        markdownFolders.get().forEach { markdownFolder ->
            files.from(markdownFolder.files)
        }
        return files.asFileTree.matching { include("**/*.md") }
    }

    // Should only be called during the `execute` phase
    @get:Internal
    protected val pagesPath: Path get() {
        return Path(PackageUtils.packageToPath(projectGroup.get().toString().packageConcat(pagesPackage.get())))
    }

    @get:Input
    val markdownPath = markdownBlock.markdownPath

    /**
     * Given a base dir that owns markdown files, return the target path any generated source should be written into.
     *
     * A null return value indicates that no path target was found, which shouldn't happen if we hooked everything up
     * correctly.
     *
     * Otherwise, this value will end with a trailing slash unless empty, as then you can just prepend it in front of a
     * relative to get the final output path.
     *
     * NOTE: This method is only expected to be called inside an execute method.
     */
    protected fun ListProperty<MarkdownFolder>.findTargetPackage(dir: File): String? {
        return get().asSequence()
                .filter { dir in it.files }
                .map {
                    PackageUtils.resolvePackageShortcut(
                        projectGroup.get().toString(),
                        it.targetPackage.get()
                    )
                }
                .firstOrNull()
    }

    protected fun Path.toRelativePagePath(): Path? {
        val pagesPath = Path(pagesPath.invariantSeparatorsPathString.suffixIfNot("/"))
        return if (this.startsWith(pagesPath)) {
            this.relativeTo(pagesPath)
        } else null
    }

    protected val FileTreeElement.rootDir: File
        get() {
            val absolutePath = file.absolutePath.invariantSeparatorsPath
            val relPath = relativePath.toPath().invariantSeparatorsPathString

            check(absolutePath.endsWith(relPath)) {
                "Relative path $relPath is not a suffix of absolute path $absolutePath"
            }

            return File(absolutePath.substring(0, absolutePath.length - relPath.length))
        }

    protected val File.capitalizedNameWithoutExtension get(): String {
        // The suggested replacement for "capitalize" is awful
        @Suppress("DEPRECATION")
        return nameWithoutExtension.capitalize()
    }

    protected fun RelativePath.toPath(): Path {
        return Path(this.pathString)
    }

    /** Recursively delete the contents of this directory without deleting the directory itself. */
    protected fun File.clearDirectory() {
        deleteRecursively()
        mkdirs()
    }
}
