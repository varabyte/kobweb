@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.conf.KobwebConfFile
import com.varabyte.kobweb.gradle.application.*
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.templates.createHtmlFile
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File
import javax.inject.Inject

// Note to be confused with a Gradle project, this is an IntelliJ project which will allow us to scan a parsed file's
// contents.
private val kotlinProject by lazy {
    KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        CompilerConfiguration(),
        EnvironmentConfigFiles.JS_CONFIG_FILES
    ).project
}

private fun PsiElement.visitAllChildren(visit: (PsiElement) -> Unit) {
    visit(this)
    children.forEach { it.visitAllChildren(visit) }
}

private class PageEntry(
    val fqcn: String,
    val route: String,
)

abstract class KobwebGenerateTask @Inject constructor(private val config: KobwebConfig) : KobwebTask("Generate Kobweb code and resources") {
    @get:InputFile
    abstract val confFile: RegularFileProperty

    @get:OutputDirectory
    abstract val genDir: DirectoryProperty

    @InputFiles
    fun getSourceFiles() = config.getSourceFiles(project)

    @InputFiles
    fun getResourceFiles(): List<File> = config.getResourceFiles(project)

    /**
     * The root package of all pages.
     *
     * Any composable function not under this root will be ignored, even if annotated by @Page.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".pages" -> "com.example.pages"
     */
    @get:Input
    abstract val pagesPackage: Property<String>

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    @get:Input
    abstract val publicPath: Property<String>

    init {
        confFile.convention(project.layout.projectDirectory.file(config.confFile.get()))
        genDir.convention(project.layout.buildDirectory.dir(config.genDir.get()))
        pagesPackage.convention(".pages")
        publicPath.convention("public")
    }

    private fun getPagesPackage(projectGroup: String): String {
        return pagesPackage.get().let { pages ->
            when {
                pages.startsWith('.') -> "$projectGroup$pages"
                else -> pages
            }
        }
    }

    private fun getPublicPath(resourcesDir: File) = resourcesDir.resolve(publicPath.get())


    @TaskAction
    fun execute() {
        val confFile = confFile.get().asFile
        if (!confFile.exists()) {
            throw GradleException("A Kobweb project must have a \"${confFile.name}\" file in its root directory")
        }

        // It's a little roundabout, but we get the kobweb folder from the conf file and then get the parsed conf
        // information back again using it.
        val conf = KobwebFolder.fromChildPath(confFile.toPath())!!.let { kobwebFolder ->
            KobwebConfFile(kobwebFolder).content!!
        }

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
            ktFile.visitAllChildren { element->
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
                                    val pagesPackage = getPagesPackage(project.group.toString())
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
                    .toMap()
            )
        )

        File(genDirResRoot, "index.html").writeText(
            createHtmlFile(
                conf.site.title,
                // TODO(Bug #7): Only specify font-awesome link if necessary
                listOf("""<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />"""),
                conf.server.locations.dev.script.substringAfterLast(".")
            )
        )
    }
}