package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.common.text.suffixIfNot
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownFolder
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
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
    @get:Input
    abstract val pagesPackage: Property<String>

    @get:Input
    abstract val projectGroup: Property<Any>

    @get:Nested
    abstract val markdownFolders: ListProperty<MarkdownFolder>

    // Should only be called during the `execute` phase
    @get:Internal
    protected val pagesPath: Path get() {
        return Path(PackageUtils.packageToPath(projectGroup.get().toString().packageConcat(pagesPackage.get())))
    }

    /**
     * [MarkdownFolder.targetPackage] but resolved against [projectGroup] to ensure it is an absolute package.
     */
    protected val MarkdownFolder.resolvedTargetPackage: String
        get() {
            return PackageUtils.resolvePackageShortcut(
                projectGroup.get().toString(),
                targetPackage.get()
            )
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

    protected val Path.capitalizedNameWithoutExtension get(): String {
        return toFile().capitalizedNameWithoutExtension
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
