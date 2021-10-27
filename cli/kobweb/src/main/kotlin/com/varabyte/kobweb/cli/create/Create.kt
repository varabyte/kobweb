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
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.blue
import com.varabyte.konsole.foundation.text.bold
import com.varabyte.konsole.foundation.text.green
import com.varabyte.konsole.foundation.text.magenta
import com.varabyte.konsole.foundation.text.text
import com.varabyte.konsole.foundation.text.textLine
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteExisting
import kotlin.io.path.name

fun handleCreate(repo: String, branch: String, template: String) = konsoleApp {
    val tempDir = handleFetch(repo, branch) ?: return@konsoleApp

    val templateFile = run {
        val tempPath = tempDir.toPath()
        val subPaths = listOf("$template/default", template)
        subPaths
            .asSequence()
            .map { subPath -> tempPath.resolve(subPath) }
            .mapNotNull { currPath -> KobwebFolder.inPath(currPath)?.let { KobwebTemplateFile(it) } }
            .filter { it.content != null }
            .firstOrNull()

            ?: run {
                konsole {
                    textError("Unable to locate a template named \"$template\"")
                    textLine()
                    text("Consider running `"); cmd("kobweb list"); text("` for a list of choices.")
                }.run()

                return@konsoleApp
            }
    }

    val defaultFolderName = PathUtils.generateEmptyPathName("my-project")
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

    konsole {
        fun indent() {
            text("  ")
        }
        textLine()
        bold {
            green { text("Success! ") }
            textLine("Created $projectFolder at ${dstPath.absolutePathString()}")
        }
        textLine()
        text("Consider downloading "); magenta(isBright = true) { textLine("IntelliJ IDEA Community Edition") }
        text("using "); blue(isBright = true) { textLine("https://www.jetbrains.com/toolbox-app/") }
        textLine()
        textLine("We suggest that you begin by typing:")
        textLine()
        if (dstPath != Path.of("").toAbsolutePath()) {
            indent(); cmd("cd $projectFolder"); textLine()
        }
        indent(); cmd("kobweb run"); textLine()
    }.run()
}