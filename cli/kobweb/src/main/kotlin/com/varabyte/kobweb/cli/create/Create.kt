package com.varabyte.kobweb.cli.create

import com.varabyte.kobweb.cli.common.PathUtils
import com.varabyte.kobweb.cli.common.Validations
import com.varabyte.kobweb.cli.common.cmd
import com.varabyte.kobweb.cli.common.handleFetch
import com.varabyte.kobweb.cli.common.informInfo
import com.varabyte.kobweb.cli.common.queryUser
import com.varabyte.kobweb.cli.common.template.KobwebTemplateFile
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.cli.create.freemarker.FreemarkerState
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.blue
import com.varabyte.kotter.foundation.text.bold
import com.varabyte.kotter.foundation.text.green
import com.varabyte.kotter.foundation.text.magenta
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteExisting
import kotlin.io.path.name

/**
 * A special-case path suffix which is used for identifying the main choice for a template that has multiple possible
 * choices.
 *
 * A concrete example will help here. Say that you originally start by creating a single template called "site", and
 * users are getting used to calling `kobweb create site`. Later, you get a lot of requests for an empty site template,
 * and you want to add `kobweb create site/empty`.
 *
 * If you do this, now you have a kobweb project nested inside of another kobweb project, which is awkward. Instead, you
 * should refactor the original "site" template to "site/default", and then "site/default" and "site/empty" can live
 * side by side.
 *
 * For convenience, though, users shouldn't have to type "/default" themselves; it should be handled automatically. So,
 * `kobweb create site` might actually be instantiating the project that lives at "site/default", but the user doesn't
 * need to know that.
 */
private const val DEFAULT_SUFFIX = "/default"

fun handleCreate(repo: String, branch: String, template: String) = session {
    val tempDir = handleFetch(repo, branch) ?: return@session

    val templateFile = run {
        val tempPath = tempDir.toPath()
        val subPaths = listOf("$template$DEFAULT_SUFFIX", template)
        subPaths
            .asSequence()
            .map { subPath -> tempPath.resolve(subPath) }
            .mapNotNull { currPath -> KobwebFolder.inPath(currPath)?.let { KobwebTemplateFile(it) } }
            .filter { it.content != null }
            .firstOrNull()

            ?: run {
                section {
                    textError("Unable to locate a template named \"$template\"")
                    textLine()
                    text("Consider running `"); cmd("kobweb list"); text("` for a list of choices.")
                }.run()

                return@session
            }
    }

    // Convert template name to folder name, e.g. "site" -> "site" and "examples/clock" -> "clock". One exception:
    // the special-case "default" name, which is supposed to be transparent to users.
    val defaultFolderName =
        PathUtils.generateEmptyPathName(template.removeSuffix(DEFAULT_SUFFIX).substringAfterLast('/'))
    informInfo("The folder you choose here will be created under your current path.")
    informInfo("You can enter `.` if you want to use the current directory.")
    val dstPath = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.isFileName(answer) ?: Validations.isEmptyPath(answer)
    }.let { answer ->
        Path.of(if (answer != ".") answer else "").toAbsolutePath()
    }
    val srcPath = KobwebFolder.fromChildPath(templateFile.path)!!.getProjectPath()
    val kobwebTemplate = templateFile.content!! // We already checked this was set, above

    // Template almost ready to be processed - remove all files that should NEVER end up in the final project
    templateFile.path.deleteExisting()
    run {
        val subTemplates = mutableListOf<File>()
        val root = srcPath.toFile()
        root.walkBottomUp()
            .filter { file -> file != root }
            .forEach { file ->
                if (file.isDirectory && KobwebFolder.isKobwebProject(file.toPath())) {
                    subTemplates.add(file)
                }
            }
        subTemplates.forEach { subTemplate -> subTemplate.deleteRecursively() }
    }

    val state = FreemarkerState(srcPath, dstPath)
    state.execute(this, kobwebTemplate.instructions)

    val projectFolder = dstPath.name

    section {
        fun indent() {
            text("  ")
        }
        textLine()
        bold {
            green { text("Success! ") }
            textLine("Created $projectFolder at ${dstPath.absolutePathString()}")
        }
        textLine()
        bordered(BorderCharacters.CURVED, paddingLeftRight = 1) {
            text("Consider downloading "); magenta(isBright = true) { textLine("IntelliJ IDEA Community Edition") }
            text("using "); blue(isBright = true) { textLine("https://www.jetbrains.com/toolbox-app/") }
        }
        textLine()
        textLine("We suggest that you begin by typing:")
        textLine()
        if (dstPath != Path.of("").toAbsolutePath()) {
            indent(); cmd("cd $projectFolder"); textLine()
        }
        indent(); cmd("kobweb run"); textLine()
        textLine()
    }.run()
}