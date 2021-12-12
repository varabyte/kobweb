package com.varabyte.kobweb.gradle.application.project.api

import com.varabyte.kobweb.gradle.application.extensions.visitAllChildren
import com.varabyte.kobweb.gradle.application.project.PackageUtils.resolvePackageShortcut
import com.varabyte.kobweb.gradle.application.project.PsiUtils
import com.varabyte.kobweb.gradle.application.project.Reporter
import com.varabyte.kobweb.gradle.application.project.parseKotlinFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

class ApiData {
    private val _initMethods = mutableListOf<InitApiEntry>()
    val initMethods: List<InitApiEntry> = _initMethods

    private val _apiMethods = mutableListOf<ApiEntry>()
    val apiMethods: List<ApiEntry> = _apiMethods

    companion object {
        /**
         * @param group The group of this project, e.g. "org.example.mysite", required for prefixing relative package
         *   names (e.g. ".blog" -> "org.example.mysite.blog")
         * @param apiPackage The relative package path that represents the root of the user's API methods, e.g. ".api".
         *   Annotated methods in source files not under this package will be ignored.
         * @param apiSources Kotlin jvmMain source files that drive this site's backend.
         */
        fun from(
            group: String,
            apiPackage: String,
            apiSources: List<File>,
            reporter: Reporter,
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
                                        apiData._initMethods.add(InitApiEntry("$currPackage.${element.name}"))
                                    }
                                    apiSimpleName -> {
                                        val qualifiedApiPackage = resolvePackageShortcut(group, apiPackage)
                                        if (currPackage.startsWith(qualifiedApiPackage)) {
                                            // e.g. com.example.pages.blog -> blog
                                            val slugPrefix = currPackage
                                                .removePrefix(qualifiedApiPackage)
                                                .replace('.', '/')

                                            val slug = file.nameWithoutExtension.removeSuffix("Api").toLowerCase()

                                            // This file should be somewhere underneath the api package
                                            check(currPackage.isNotEmpty())
                                            apiData._apiMethods.add(
                                                ApiEntry(
                                                    "$currPackage.${element.name}",
                                                    "$slugPrefix/$slug"
                                                )
                                            )
                                        }
                                        else {
                                            reporter.report("${file.absolutePath}: Skipped over `@$apiSimpleName fun ${element.name}`. It is defined under package `$currPackage` but must exist under `$qualifiedApiPackage`")
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