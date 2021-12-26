package com.varabyte.kobweb.cli.list

import com.varabyte.kobweb.cli.common.DEFAULT_BRANCH
import com.varabyte.kobweb.cli.common.DEFAULT_REPO
import com.varabyte.kobweb.cli.common.handleFetch
import com.varabyte.kobweb.cli.common.template.KobwebTemplateFile
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.cyan
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import kotlin.io.path.relativeTo

fun handleList(repo: String, branch: String) = session {
    val tempDir = handleFetch(repo, branch) ?: return@session

    val templates = tempDir.walkBottomUp()
        .filter { it.isDirectory }
        .mapNotNull { dir ->
            KobwebFolder.inPath(dir.toPath())
                ?.let { kobwebFolder -> KobwebTemplateFile(kobwebFolder) }
        }
        .filter { templateFile -> templateFile.content != null }
        .toList()

    section {
        if (templates.isNotEmpty()) {
            text("You can create the following Kobweb projects by typing `kobweb create ")
            cyan { text("...") }
            if (repo != DEFAULT_REPO) {
                text(" --repo $repo")
            }
            if (branch != DEFAULT_BRANCH) {
                text(" --branch $branch")
            }
            textLine("`")
            textLine()

            val tempPath = tempDir.toPath()
            templates
                .sortedBy { template -> template.kobwebFolder.getProjectPath() }
                .forEach { template ->
                    val templatePath = template.kobwebFolder.getProjectPath().relativeTo(tempPath).toString()
                    val description = template.content!!.metadata.description

                    text("• ")
                    cyan { text(templatePath) }
                    textLine(": $description")
                }
        } else {
            textError("No templates were found in the specified repository.")
        }
    }.run()
}