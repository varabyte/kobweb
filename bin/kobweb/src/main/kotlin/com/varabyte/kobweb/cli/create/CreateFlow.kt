package com.varabyte.kobweb.cli.create

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.cli.common.*
import com.varabyte.kobweb.cli.create.freemarker.FreemarkerState
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.red
import com.varabyte.konsole.foundation.text.textLine
import com.varabyte.konsole.runtime.KonsoleApp
import com.varabyte.konsole.runtime.concurrent.createKey
import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteIfExists
import kotlin.io.path.notExists

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

        throw KobwebException("Unable to fetch templates")
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

                throw KobwebException("Unable to find matching template")
            }
    }

    val defaultFolderName = PathUtils.generateEmptyPathName("my-project")
    val projectFolder = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.isFileName(answer) ?: Validations.isEmptyPath(answer)
    }

    val srcPath = templateFile.parent
    val dstPath = Path.of(projectFolder).also { if (it.notExists()) { it.createDirectory() } }

    val kobwebTemplate = Yaml.default.decodeFromString(KobwebTemplate.serializer(), templateFile.toFile().readText())
    // Template almost ready to be processed - remove all files that should NEVER end up in the final project
    templateFile.deleteIfExists()
    run {
        val subTemplates = mutableListOf<File>()
        srcPath.toFile().walkBottomUp().forEach { file ->
            if (file.isDirectory && KobwebUtils.isTemplateFileIn(file.toPath())) {
                subTemplates.add(file)
            }
        }
        subTemplates.forEach { subTemplate -> subTemplate.deleteRecursively() }
    }

    val state = FreemarkerState(srcPath, dstPath, projectFolder)
    state.execute(this, kobwebTemplate.instructions)
}