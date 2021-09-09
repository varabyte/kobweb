package com.varabyte.kobweb.cli.create

import com.varabyte.kobweb.cli.common.*
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.red
import com.varabyte.konsole.foundation.text.textLine
import com.varabyte.konsole.runtime.KonsoleApp
import com.varabyte.konsole.runtime.concurrent.ConcurrentScopedData
import com.varabyte.konsole.runtime.concurrent.createKey
import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val TempDirKey = KonsoleApp.Lifecycle.createKey<File>()

fun runCreateFlow(template: String) = konsoleApp {
    val repo = "https://github.com/varabyte/kobweb-templates"
    val tempDir = Files.createTempDirectory("kobweb").toFile()
    data.set(TempDirKey, tempDir, dispose = { tempDir.deleteRecursively() })
    if (processing("Cloning \"$repo\"") {
            Git.cloneRepository()
                .setURI(repo)
                .setDirectory(tempDir)
                .call()
        }) {
    } else {
        konsole {
            red {
                textLine("We were unable to fetch templates. Please check the repository path and your internet connection.")
            }
        }.run()

        return@konsoleApp
    }
    konsole { textLine() }.run()

    val templateFile = run {
        val tempPath = tempDir.toPath()
        val subPaths = listOf("$template/default", template)
        subPaths
            .asSequence()
            .map { subPath -> tempPath.resolve(subPath) }
            .mapNotNull { currPath -> KobwebUtils.getTemplateFileIn(currPath) }
            .firstOrNull()

            ?: run {
                konsole {
                    red {
                        textLine("Unable to find a template named \"$template\". Please check the repository.")
                    }
                }.run()

                return@konsoleApp
            }
    }

    val projectName = queryUser("What is your project named?", "My Project")

    val defaultFolderName = PathUtils.generateEmptyPathName(
        projectName.lowercase().filter { it.isLetterOrDigit() }.takeIf { it.isNotEmpty() } ?: "myproject"
    )
    val projectFolder = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.folderName(answer) ?: Validations.emptyPath(answer)
    }
}