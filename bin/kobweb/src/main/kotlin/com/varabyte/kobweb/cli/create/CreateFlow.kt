package com.varabyte.kobweb.cli.create

import com.varabyte.kobweb.cli.common.PathUtils
import com.varabyte.kobweb.cli.common.Validations
import com.varabyte.kobweb.cli.common.processing
import com.varabyte.kobweb.cli.common.queryUser
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.red
import com.varabyte.konsole.foundation.text.textLine
import org.eclipse.jgit.api.Git
import java.nio.file.Files

fun runCreateFlow(template: String) = konsoleApp {
    val repo = "https://github.com/varabyte/kobweb-templates"
    val tempDir = Files.createTempDirectory("kobweb").toFile()
    try {
        if (processing("Cloning \"$repo\"") {
                Git.cloneRepository()
                    .setURI(repo)
                    .setDirectory(tempDir)
                    .call()
            }) {
            konsole { textLine() }.run()
        } else {
            konsole {
                red {
                    textLine("We were unable to fetch templates. Please check the repository path and your internet connection.")
                }
            }.run()

            return@konsoleApp
        }
    }
    finally {
        tempDir.deleteRecursively()
    }


    val projectName = queryUser("What is your project named?", "My Project")
    val defaultFolderName = PathUtils.generateEmptyPathName(projectName.lowercase().filter { it.isLetterOrDigit() })
    val projectFolder = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.folderName(answer) ?: Validations.emptyPath(answer)
    }
}