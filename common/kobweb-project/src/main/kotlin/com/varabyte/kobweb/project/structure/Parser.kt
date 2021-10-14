package com.varabyte.kobweb.project.structure

import com.varabyte.kobweb.project.KobwebProject
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

// For now, we're directly parsing Kotlin code using the embedded Kotlin compiler. This is a temporary approach.
// In the future, this should use KSP to navigate through source files. See also: Bug #4
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

class ProjectData {
    var app: AppEntry? = null
        internal set
    private val _pages = mutableListOf<PageEntry>()
    val pages: List<PageEntry> = _pages
    private val _apiMethods = mutableListOf<ApiEntry>()
    val apiMethods: List<ApiEntry> = _apiMethods

    companion object {
        fun from(
            group: String,
            pagesPackage: String,
            siteSources: List<File>,
            apiPackage: String,
            apiSources: List<File>
        ): ProjectData {
            val projectData = ProjectData()
            siteSources.forEach { file ->
                val ktFile = PsiManager.getInstance(kotlinProject)
                    .findFile(LightVirtualFile(file.name, KotlinFileType.INSTANCE, file.readText())) as KtFile

                var currPackage = ""
                var pageSimpleName = PAGE_SIMPLE_NAME
                var appSimpleName = APP_SIMPLE_NAME
                ktFile.visitAllChildren { element ->
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
                                        val customAppFqcn = when {
                                            currPackage.isNotEmpty() -> "$currPackage.${element.name}"
                                            else -> element.name
                                        }
                                        customAppFqcn?.let { projectData.app = AppEntry(it) }
                                    }
                                    pageSimpleName -> {
                                        val pagesPackage = KobwebProject.prefixQualifiedPackage(group, pagesPackage)
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

                                            projectData._pages.add(
                                                PageEntry(
                                                    "$currPackage.${element.name}",
                                                    "$slugPrefix/$slug"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            apiSources.forEach { file ->
                val ktFile = PsiManager.getInstance(kotlinProject)
                    .findFile(LightVirtualFile(file.name, KotlinFileType.INSTANCE, file.readText())) as KtFile

                var currPackage = ""
                var apiSimpleName = API_SIMPLE_NAME
                ktFile.visitAllChildren { element ->
                    when (element) {
                        is KtPackageDirective -> {
                            currPackage = element.fqName.asString()
                        }
                        is KtImportDirective -> {
                            // It's unlikely this will happen but catch the "import as" case,
                            // e.g. `import com.varabyte.kobweb.core.Page as MyPage`
                            when (element.importPath?.fqName?.asString()) {
                                API_FQCN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { apiSimpleName = it }
                                    }
                                }
                            }
                        }
                        is KtNamedFunction -> {
                            element.annotationEntries.forEach { entry ->
                                when (entry.shortName?.asString()) {
                                    apiSimpleName -> {
                                        val apiPackage = KobwebProject.prefixQualifiedPackage(group, apiPackage)
                                        if (currPackage.startsWith(apiPackage)) {
                                            // e.g. com.example.pages.blog -> blog
                                            val slugPrefix = currPackage
                                                .removePrefix(apiPackage)
                                                .replace('.', '/')

                                            val slug = file.nameWithoutExtension.removeSuffix("Api").toLowerCase()

                                            projectData._apiMethods.add(
                                                ApiEntry(
                                                    "$currPackage.${element.name}",
                                                    "$slugPrefix/$slug"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return projectData
        }
    }
}