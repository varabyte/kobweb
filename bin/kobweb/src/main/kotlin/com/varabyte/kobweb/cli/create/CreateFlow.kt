package com.varabyte.kobweb.cli.create

import com.varabyte.kobweb.cli.common.Validations
import com.varabyte.kobweb.cli.common.queryUser
import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.konsoleApp
import java.time.Duration

private const val DEFAULT_PROJECT_NAME = "My Project"

fun runCreateFlow(template: String) = konsoleApp {
    val processingAnim = konsoleAnimOf(listOf(".", "..", "..."), Duration.ofMillis(250))

    val projectName = queryUser("What is your project named?", "My Project")
    val defaultFolderName = projectName.lowercase().filter { it.isLetterOrDigit() }
    val projectFolder = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.folderName(answer)
    }
}