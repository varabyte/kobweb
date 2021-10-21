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

class ApiData {
    private val _apiMethods = mutableListOf<ApiEntry>()
    val apiMethods: List<ApiEntry> = _apiMethods

    companion object {
        fun from(
            group: String,
            apiPackage: String,
            apiSources: List<File>
        ): ApiData {
            val kotlinProject = createKotlinProject()
            val apiData = ApiData()
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
                                        val qualifiedApiPackage = KobwebProject.prefixQualifiedPackage(group, apiPackage)
                                        if (currPackage.startsWith(qualifiedApiPackage)) {
                                            // e.g. com.example.pages.blog -> blog
                                            val slugPrefix = currPackage
                                                .removePrefix(qualifiedApiPackage)
                                                .replace('.', '/')

                                            val slug = file.nameWithoutExtension.removeSuffix("Api").toLowerCase()

                                            apiData._apiMethods.add(
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

            return apiData
        }
    }
}