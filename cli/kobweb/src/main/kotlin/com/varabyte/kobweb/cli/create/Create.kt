package com.varabyte.kobweb.cli.create

import com.varabyte.kobweb.cli.common.PathUtils
import com.varabyte.kobweb.cli.common.Validations
import com.varabyte.kobweb.cli.common.cmd
import com.varabyte.kobweb.cli.common.findGit
import com.varabyte.kobweb.cli.common.handleFetch
import com.varabyte.kobweb.cli.common.informInfo
import com.varabyte.kobweb.cli.common.newline
import com.varabyte.kobweb.cli.common.queryUser
import com.varabyte.kobweb.cli.common.template.KobwebTemplateFile
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.cli.create.freemarker.FreemarkerState
import com.varabyte.kobweb.cli.create.freemarker.methods.IsNotEmptyMethod
import com.varabyte.kobweb.cli.create.freemarker.methods.YesNoToBoolMethod
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
import kotlin.io.path.deleteIfExists
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
    val gitClient = findGit() ?: return@session
    val tempDir = handleFetch(gitClient, repo, branch) ?: return@session

    val templateFile = run {
        val subPaths = listOf("$template$DEFAULT_SUFFIX", template)
        subPaths
            .asSequence()
            .map { subPath -> tempDir.resolve(subPath) }
            .mapNotNull { currPath -> KobwebTemplateFile.inPath(currPath) }
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
    val srcPath = templateFile.folder
    val kobwebTemplate = templateFile.template // We already checked this was set, above
    // We've parsed the template and don't need it anymore. Delete it so we don't copy it over
    templateFile.path.deleteExisting()
    // Delete legacy template.yaml file, if found. TODO(#188): Delete this line before 1.0
    KobwebFolder.inPath(templateFile.folder)?.resolve("template.yaml")?.deleteIfExists()

    run {
        val subTemplates = mutableListOf<File>()
        val root = srcPath.toFile()
        root.walkTopDown()
            .filter { file -> file != root }
            .forEach { file ->
                if (file.isDirectory && KobwebFolder.isFoundIn(file.toPath())) {
                    subTemplates.add(file)
                }
            }
        subTemplates.forEach { subTemplate -> subTemplate.deleteRecursively() }
    }

    val state = FreemarkerState(srcPath, dstPath)
    state.execute(this, kobwebTemplate.instructions)

    val projectFolder = dstPath.name

    newline()
    var gitInitialized = false
    queryUser("Would you like to initialize git for this project?", "yes").let { initializeAnswer ->
        val yesNoToBool = YesNoToBoolMethod()
        val isNotEmpty = IsNotEmptyMethod()

        val shouldInitialize = yesNoToBool.exec(initializeAnswer).toBoolean()
        if (shouldInitialize) {
            gitClient.init(dstPath)

            val commitAnswer = queryUser("Would you like to create an initial commit?", "yes")
            val shouldCommit = yesNoToBool.exec(commitAnswer).toBoolean()

            if (shouldCommit) {
                val commitMessage = queryUser(
                    "Commit message:",
                    "Initial commit",
                    validateAnswer = { answer -> isNotEmpty.exec(answer) }
                ).trim()

                gitClient.add(".", dstPath)
                gitClient.commit(commitMessage, dstPath)
            }
            gitInitialized = true
        }
    }

    section {
        fun indent() {
            text("  ")
        }
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

        // Search for the root where you can run `kobweb run` within. This is usually at the root of a Kobweb project,
        // but it might be inside a submodule in some cases. We always expect to find it, but if we can't, no big deal
        // -- just don't show the suggestion.
        val kobwebRootPath = dstPath.toFile().walkTopDown()
            .filter { file -> file.isDirectory && KobwebFolder.isFoundIn(file.toPath()) }
            .firstOrNull()?.toPath()

        if (kobwebRootPath != null) {
            textLine("We suggest that you begin by typing:")
            textLine()

            val currPath = Path.of("").toAbsolutePath()
            if (kobwebRootPath != currPath) {
                indent(); cmd("cd ${currPath.relativize(kobwebRootPath)}"); textLine()
            }
            if (!gitInitialized) {
                indent(); cmd("git init && "); cmd("git add . && "); cmd("git commit -m \"Initial commit\""); textLine()
            }
            indent(); cmd("kobweb run"); textLine()
        }
    }.run()
}