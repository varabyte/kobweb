package com.varabyte.kobweb.cli.list

import com.varabyte.kobweb.cli.common.DEFAULT_BRANCH
import com.varabyte.kobweb.cli.common.DEFAULT_REPO
import com.varabyte.kobweb.cli.common.findGit
import com.varabyte.kobweb.cli.common.handleFetch
import com.varabyte.kobweb.cli.common.template.KobwebTemplateFile
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.common.toUnixSeparators
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.cyan
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.runtime.render.RenderScope
import java.nio.file.Path
import kotlin.io.path.relativeTo

private fun RenderScope.renderTemplateItem(rootPath: Path, template: KobwebTemplateFile) {
    val templatePath =
        template.kobwebFolder.getProjectPath().relativeTo(rootPath).toString()
            // Even on Windows, show Unix-style slashes, as `kobweb create` expects that format
            .toUnixSeparators()
            .removeSuffix("/default")
    val description = template.content!!.metadata.description

    text("• ")
    cyan { text(templatePath) }
    textLine(": $description")
}

private fun List<KobwebTemplateFile>.renderTemplateItemsInto(rootPath: Path, renderScope: RenderScope) {
    this
        .sortedBy { template -> template.kobwebFolder.getProjectPath() }
        .forEach { template -> renderScope.renderTemplateItem(rootPath, template) }
}

fun handleList(repo: String, branch: String) = session {
    val gitClient = findGit() ?: return@session
    val tempDir = handleFetch(gitClient, repo, branch) ?: return@session

    val templates = tempDir.toFile().walkBottomUp()
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

            run {
                val highlightedTemplates = templates.filter { it.content!!.metadata.shouldHighlight }
                if (highlightedTemplates.isNotEmpty()) {
                    yellow { textLine("Highlighted projects") }
                    textLine()
                    highlightedTemplates.renderTemplateItemsInto(tempDir, this)
                    textLine()
                    yellow { textLine("All projects") }
                    textLine()
                }
            }

            templates.renderTemplateItemsInto(tempDir, this)
        } else {
            textError("No templates were found in the specified repository.")
        }
    }.run()
}