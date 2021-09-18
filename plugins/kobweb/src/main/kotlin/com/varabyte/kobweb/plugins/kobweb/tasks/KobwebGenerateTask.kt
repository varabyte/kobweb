package com.varabyte.kobweb.plugins.kobweb.tasks

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.plugins.kobweb.*
import com.varabyte.kobweb.plugins.kobweb.conf.KobwebConf
import com.varabyte.kobweb.plugins.kobweb.kmp.kotlin
import com.varabyte.kobweb.plugins.kobweb.templates.createHtmlFile
import com.varabyte.kobweb.plugins.kobweb.templates.createMainFunction
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

// Note to be confused with a Gradle project, this is an IntelliJ project which will allow us to scan a parsed file's
// contents.
private val kotlinProject by lazy {
    KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        CompilerConfiguration(),
        EnvironmentConfigFiles.JS_CONFIG_FILES
    ).project
}

private fun PsiElement.visitAllChildren(indent: String = "", visit: (PsiElement, String) -> Unit) {
    visit(this, indent)
    children.forEach { it.visitAllChildren("$indent  ", visit) }
}

private fun File.isDescendantOf(maybeAncestor: File): Boolean {
    var curr: File? = this
    while (curr != null) {
        if (curr == maybeAncestor) {
            return true
        }
        curr = curr.parentFile
    }
    return false
}

private class PageEntry(
    val fqcn: String,
    val route: String,
)

abstract class KobwebGenerateTask : KobwebTask("Generate Kobweb webserver code for the current project") {
    @get:InputFile
    abstract val configFile: Property<File>

    @get:OutputDirectory
    abstract val genDir: DirectoryProperty

    private fun getFiles(rootDirProducer: (KotlinSourceSet) -> FileCollection): Sequence<File> {
        val genDirFile = genDir.get().asFile

        return project.kotlin.sourceSets.asSequence()
            .filter { sourceSet -> sourceSet.name == "jsMain" }
            .flatMap { sourceSet ->
                rootDirProducer(sourceSet)
                    .filter { rootDir -> !rootDir.isDescendantOf(genDirFile) }
                    .flatMap { rootDir ->
                        rootDir.walkBottomUp().filter { it.isFile }
                    }
            }

    }

    @InputFiles
    fun getSourceFiles(): List<File> {
        return getFiles { sourceSet -> sourceSet.kotlin.sourceDirectories }
            .filter { it.extension == "kt" }
            .toList()
    }

    @InputFiles
    fun getResourceFiles(): List<File> = getFiles { sourceSet -> sourceSet.resources }.toList()

    @TaskAction
    fun execute() {
        val configFile = configFile.get()
        if (!configFile.exists()) {
            throw GradleException("A Kobweb project must have a \"${configFile.name}\" file in its root directory")
        }

        val conf = Yaml.default.decodeFromString(KobwebConf.serializer(), configFile.readText())

        // For now, we're directly parsing Kotlin code using the embedded Kotlin compiler. This is a temporary approach.
        // In the future, this should use KSP to navigate through source files. See also: Bug #4
        var customAppFqcn: String? = null
        val pageEntries = mutableListOf<PageEntry>()
        getSourceFiles().forEach { file ->
            val ktFile = PsiManager.getInstance(kotlinProject)
                .findFile(LightVirtualFile(file.name, KotlinFileType.INSTANCE, file.readText())) as KtFile

            var currPackage = ""
            var pageSimpleName = PAGE_SIMPLE_NAME
            var appSimpleName = APP_SIMPLE_NAME
            ktFile.visitAllChildren { element, indent ->
                when (element) {
                    is KtPackageDirective -> {
                        currPackage = element.fqName.asString()
                    }
                    is KtImportDirective -> {
                        // It's unlikely this will happen but catch the "import as" case,
                        // e.g. `import com.varabyte.kobweb.core.Page as MyPage`
                        when (element.importPath?.fqName?.asString()) {
                            APP_FQCN -> {
                                element.alias?.let { alias ->
                                    alias.name?.let { appSimpleName = it }
                                }
                            }
                            PAGE_FQCN -> {
                                element.alias?.let { alias ->
                                    alias.name?.let { pageSimpleName = it }
                                }
                            }
                        }
                    }
                    is KtNamedFunction -> {
                        element.annotationEntries.forEach { entry ->
                            when (entry.shortName?.asString()) {
                                appSimpleName -> {
                                    customAppFqcn = when {
                                        currPackage.isNotEmpty() -> "$currPackage.${element.name}"
                                        else -> element.name
                                    }
                                }
                                pageSimpleName -> {
                                    val pagesPackage = conf.locations.getPagesPackage(project.group.toString())
                                    if (currPackage.startsWith(pagesPackage)) {
                                        // e.g. com.example.pages.blog -> blog
                                        val slugPrefix = currPackage
                                            .removePrefix(pagesPackage)
                                            .replace('.', '/')

                                        val slug = when (val maybeSlug =
                                            file.nameWithoutExtension.removeSuffix("Page").toLowerCase()) {
                                            "index" -> ""
                                            else -> maybeSlug
                                        }

                                        pageEntries.add(PageEntry("$currPackage.${element.name}", "$slugPrefix/$slug"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val genDirFile = genDir.get().asFile
        val genDirSrcRoot = File(genDirFile, SRC_SUFFIX).also { it.mkdirs() }
        val genDirResRoot = File(genDirFile, RESOURCE_SUFFIX).also { it.mkdirs() }

        File(genDirSrcRoot, "main.kt").writeText(
            createMainFunction(
                customAppFqcn,
                // Sort by route as it makes the generated registration logic easier to follow
                pageEntries
                    .associate { it.fqcn to it.route }
                    .toList()
                    .sortedBy { (_, route) -> route }
                    .toMap(),
                "/"
            )
        )

        File(genDirResRoot, "index.html").writeText(
            createHtmlFile(
                conf.site.title,
                // TODO(Bug #7): Only specify font-awesome link if necessary
                listOf("""<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />"""),
                "helloworld.js" // TODO(Bug #8): Create this correctly
            )
        )
    }
}