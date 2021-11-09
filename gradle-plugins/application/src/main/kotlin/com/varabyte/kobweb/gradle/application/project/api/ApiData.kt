package com.varabyte.kobweb.gradle.application.project.api

import com.varabyte.kobweb.gradle.application.extensions.visitAllChildren
import com.varabyte.kobweb.gradle.application.project.KobwebProject
import com.varabyte.kobweb.gradle.application.project.PsiUtils
import com.varabyte.kobweb.gradle.application.project.parseKotlinFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

class ApiData {
    private val _initMethods = mutableListOf<InitEntry>()
    val initMethods: List<InitEntry> = _initMethods

    private val _apiMethods = mutableListOf<ApiEntry>()
    val apiMethods: List<ApiEntry> = _apiMethods

    companion object {
        fun from(
            group: String,
            apiPackage: String,
            apiSources: List<File>
        ): ApiData {
            val kotlinProject = PsiUtils.createKotlinProject()
            val apiData = ApiData()
            apiSources.forEach { file ->
                val ktFile = kotlinProject.parseKotlinFile(file)

                var currPackage = ""
                var initSimpleName = INIT_SIMPLE_NAME
                var apiSimpleName = API_SIMPLE_NAME
                ktFile.visitAllChildren { element ->
                    when (element) {
                        is KtPackageDirective -> {
                            currPackage = element.fqName.asString()
                        }
                        is KtImportDirective -> {
                            // It's unlikely this will happen but catch the "import as" case,
                            // e.g. `import com.varabyte.kobweb.api.Api as MyApi`
                            when (element.importPath?.fqName?.asString()) {
                                INIT_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { initSimpleName = it }
                                    }
                                }
                                API_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { apiSimpleName = it }
                                    }
                                }
                            }
                        }
                        is KtNamedFunction -> {
                            element.annotationEntries.forEach { entry ->
                                when (entry.shortName?.asString()) {
                                    initSimpleName -> {
                                        apiData._initMethods.add(InitEntry("$currPackage.${element.name}"))
                                    }
                                    apiSimpleName -> {
                                        val qualifiedApiPackage =
                                            KobwebProject.prefixQualifiedPackage(group, apiPackage)
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