package com.varabyte.kobweb.cli.list

import com.varabyte.kobweb.cli.common.DEFAULT_BRANCH
import com.varabyte.kobweb.cli.common.DEFAULT_REPO
import com.varabyte.kobweb.cli.common.findGit
import com.varabyte.kobweb.cli.common.handleFetch
import com.varabyte.kobweb.cli.common.template.KobwebTemplateFile
import com.varabyte.kobweb.cli.common.template.getName
import com.varabyte.kobweb.cli.common.textError
import com.varabyte.kobweb.cli.common.version.versionIsSupported
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.cyan
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.runtime.render.RenderScope
import java.nio.file.Path

private fun RenderScope.renderTemplateItem(
    rootPath: Path,
    templateFile: KobwebTemplateFile,
) {
    val templatePath = templateFile.getName(rootPath)
    val description = templateFile.template.metadata.description

    text("• ")
    cyan { text(templatePath) }
    textLine(": $description")
}

private fun List<KobwebTemplateFile>.renderTemplateItems(
    rootPath: Path,
    renderScope: RenderScope,
) {
    this
        .sortedBy { template -> template.folder }
        .forEach { template -> renderScope.renderTemplateItem(rootPath, template) }
}

fun handleList(repo: String, branch: String) = session {
    val gitClient = findGit() ?: return@session
    val tempDir = handleFetch(gitClient, repo, branch) ?: return@session

    val templates = tempDir.toFile().walkTopDown()
        .filter { it.isDirectory }
        .mapNotNull { dir -> KobwebTemplateFile.inPath(dir.toPath()) }
        .filter { it.template.versionIsSupported }
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
                val highlightedTemplates = templates.filter { it.template.metadata.shouldHighlight }
                if (highlightedTemplates.isNotEmpty()) {
                    yellow { textLine("Highlighted projects") }
                    textLine()
                    highlightedTemplates.renderTemplateItems(tempDir, this)
                    textLine()
                    yellow { textLine("All projects") }
                    textLine()
                }
            }

            templates.renderTemplateItems(tempDir, this)
        } else {
            textError("No templates were found in the specified repository.")
        }
    }.run()
}