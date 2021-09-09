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
    val defaultFolderName = run {
        val nameBase = projectName.lowercase().filter { it.isLetterOrDigit() }
        var finalName = nameBase
        var i = 2
        while (Validations.emptyPath(finalName) != null) {
            finalName = "$nameBase$i"
            i++
        }
        finalName
    }
    val projectFolder = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.folderName(answer) ?: Validations.emptyPath(answer)
    }
}