package com.varabyte.kobweb.cli.create

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.cli.common.*
import com.varabyte.kobweb.cli.create.freemarker.FreemarkerState
import com.varabyte.kobweb.cli.create.yaml.KobwebTemplate
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.*
import com.varabyte.konsole.runtime.KonsoleApp
import com.varabyte.konsole.runtime.concurrent.createKey
import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists
import kotlin.io.path.name

private val TempDirKey = KonsoleApp.Lifecycle.createKey<File>()

fun runCreate(template: String) = konsoleApp {
    val repo = "https://github.com/varabyte/kobweb-templates"
    val tempDir = Files.createTempDirectory("kobweb").toFile()
    data.set(TempDirKey, tempDir, dispose = { tempDir.deleteRecursively() })
    if (!processing("Cloning \"$repo\"") {
            Git.cloneRepository()
                .setURI(repo)
                .setDirectory(tempDir)
                .call()
        }) {
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
            .mapNotNull { currPath -> KobwebFolder.inPath(currPath) }
            .mapNotNull { folder -> folder.resolve("template.yaml") }
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
    val dstPath = queryUser("Specify a folder for your project:", defaultFolderName) { answer ->
        Validations.isFileName(answer) ?: Validations.isEmptyPath(answer)
    }.let { answer ->
        Path.of(if (answer != ".") answer else "").toAbsolutePath()
    }
    val srcPath = KobwebFolder.fromChildPath(templateFile)!!.getProjectPath()

    val kobwebTemplate = Yaml.default.decodeFromString(KobwebTemplate.serializer(), templateFile.toFile().readText())
    // Template almost ready to be processed - remove all files that should NEVER end up in the final project
    templateFile.deleteIfExists()
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

    konsole {
        val projectFolder = dstPath.name
        textLine()
        green { text("Success! ") }
        textLine("Created $projectFolder at ${dstPath.absolutePathString()}")
        textLine()
        textLine("We suggest that you begin by typing:")
        textLine()
        if (dstPath != Path.of("").toAbsolutePath()) {
            text("  "); cyan { text("cd") }; textLine(" $projectFolder")
        }
        text("  "); cyan { textLine("./gradlew jsRun --continuous") }
        textLine()
    }.run()
}