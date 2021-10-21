package com.varabyte.kobweb.gradle.application.project

import com.varabyte.kobweb.gradle.application.extensions.visitAllChildren
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

class SiteData {
    var app: AppEntry? = null
        internal set
    private val _pages = mutableListOf<PageEntry>()
    val pages: List<PageEntry> = _pages

    companion object {
        fun from(
            group: String,
            pagesPackage: String,
            siteSources: List<File>,
        ): SiteData {
            val siteData = SiteData()
            val kotlinProject = createKotlinProject()
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
                                        customAppFqcn?.let { siteData.app = AppEntry(it) }
                                    }
                                    pageSimpleName -> {
                                        val qualifiedPackagesPackage = KobwebProject.prefixQualifiedPackage(group, pagesPackage)
                                        if (currPackage.startsWith(qualifiedPackagesPackage)) {
                                            // e.g. com.example.pages.blog -> blog
                                            val slugPrefix = currPackage
                                                .removePrefix(qualifiedPackagesPackage)
                                                .replace('.', '/')

                                            val slug = when (val maybeSlug =
                                                file.nameWithoutExtension.toLowerCase()) {
                                                "index" -> ""
                                                else -> maybeSlug
                                            }

                                            siteData._pages.add(
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

            return siteData
        }
    }
}