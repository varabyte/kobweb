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
import com.varabyte.kobweb.cli.common.template.getName
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.cli.create.freemarker.FreemarkerState
import com.varabyte.kobweb.cli.create.freemarker.methods.IsNotEmptyMethod
import com.varabyte.kobweb.cli.create.freemarker.methods.YesNoToBoolMethod
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.input.runUntilKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.blue
import com.varabyte.kotter.foundation.text.bold
import com.varabyte.kotter.foundation.text.cyan
import com.varabyte.kotter.foundation.text.green
import com.varabyte.kotter.foundation.text.magenta
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.runtime.render.RenderScope
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.name

private fun RenderScope.renderTemplateItem(
    rootPath: Path,
    templateFile: KobwebTemplateFile,
    isSelected: Boolean,
) {
    val templatePath = templateFile.getName(rootPath)
    val description = templateFile.template.metadata.description
    val isImportant = templateFile.template.metadata.shouldHighlight

    text(if (isSelected) '>' else ' ')
    text(' ')
    cyan(isBright = isImportant) { text(templatePath) }
    textLine(": $description")
}


fun handleCreate(repo: String, branch: String, templateName: String?) = session {
    val gitClient = findGit() ?: return@session
    val tempDir = handleFetch(gitClient, repo, branch) ?: return@session

    val templateRoots = tempDir.toFile().walkTopDown()
        .filter { it.isDirectory }
        .mapNotNull { dir -> KobwebTemplateFile.inPath(dir.toPath()) }
        .toList()


    val templateFile =
        (if (templateName != null) {
            templateRoots
                .firstOrNull { templateFile -> templateFile.getName(tempDir) == templateName }
                ?: run {
                    section {
                        textError("Unable to locate a template named \"$templateName\". Falling back to choosing...")
                        textLine()
                    }.run()

                    null
                }
        } else null)
        // If we can't find the template OR if no template is specified, offer a list to choose from
            ?: run {
                @Suppress("NAME_SHADOWING")
                val templateRoots = templateRoots
                    .sortedBy { templateFile -> templateFile.folder }
                    .sortedByDescending { templateFile -> templateFile.template.metadata.shouldHighlight }

                var selectedIndex by liveVarOf(0)
                var finished by liveVarOf(false)
                section {
                    bold { textLine("Press ENTER to select a project to instantiate:") }
                    textLine()
                    templateRoots.forEachIndexed { i, templateFile ->
                        this.renderTemplateItem(tempDir, templateFile, selectedIndex == i)
                    }
                    textLine()
                    if (!finished) {
                        if (templateRoots[selectedIndex].template.metadata.shouldHighlight) {
                            yellow {
                                textLine("  Note: This project has been highlighted as important by the template creator.")
                            }
                        }
                    }
                }
                    .onFinishing { finished = true }
                    .runUntilKeyPressed(Keys.ENTER) {
                        onKeyPressed {
                            when (key) {
                                Keys.UP -> selectedIndex =
                                    if (selectedIndex == 0) templateRoots.lastIndex else selectedIndex - 1

                                Keys.DOWN -> selectedIndex =
                                    if (selectedIndex == templateRoots.lastIndex) 0 else selectedIndex + 1

                                Keys.HOME -> selectedIndex = 0
                                Keys.END -> selectedIndex = templateRoots.lastIndex
                            }
                        }
                    }

                templateRoots[selectedIndex]
            }


    // Convert full template name to folder name, e.g. "site" -> "site" and "examples/clock" -> "clock".
    val defaultFolderName =
        PathUtils.generateEmptyPathName(templateFile.getName(tempDir).substringAfterLast('/'))

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

    // If a user wants to create a template underneath another template for naming purposes, e.g.
    // `demosite` and `demosite/dark`, they can just nest one template project underneath another, and Kobweb will
    // remove it after syncing.
    run {
        val subTemplates = mutableListOf<File>()
        val root = srcPath.toFile()
        root.walkTopDown()
            .filter { file -> file != root }
            .forEach { file ->
                if (file.isDirectory && KobwebTemplateFile.isFoundIn(file.toPath())) {
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