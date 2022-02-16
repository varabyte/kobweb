package com.varabyte.kobweb.gradle.application.project.api

import com.varabyte.kobweb.gradle.application.extensions.visitAllChildren
import com.varabyte.kobweb.gradle.application.project.common.PackageUtils.resolvePackageShortcut
import com.varabyte.kobweb.gradle.application.project.common.PsiUtils
import com.varabyte.kobweb.gradle.application.project.Reporter
import com.varabyte.kobweb.gradle.application.project.common.RouteUtils
import com.varabyte.kobweb.gradle.application.project.common.getStringValue
import com.varabyte.kobweb.gradle.application.project.common.parseKotlinFile
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFileAnnotationList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

class ApiData {
    private val _initMethods = mutableListOf<InitApiEntry>()
    val initMethods: List<InitApiEntry> = _initMethods

    private val _apiMethods = mutableListOf<ApiEntry>()
    val apiMethods: List<ApiEntry> = _apiMethods

    private class ApiToProcess(
        val funName: String,
        val pkg: String,
        val slugFromFile: String,
        // See @Api annotation header docs for `routeOverride` behavior
        val routeOverride: String?
    )

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

            // fqPkg to subdir, e.g. "api.id._as._int" to "int"
            val packageMappings = mutableMapOf<String, String>()
            // We need to collect all package mappings before we start processing API routes, so we store them in this
            // intermediate structure for a while
            val apisToProcess = mutableListOf<ApiToProcess>()

            val qualifiedApiPackage = resolvePackageShortcut(group, apiPackage)

            apiSources.forEach { file ->
                val ktFile = kotlinProject.parseKotlinFile(file)

                var currPackage = ""
                var packageMappingSimpleName = PACKAGE_MAPPING_SIMPLE_NAME
                var initSimpleName = INIT_SIMPLE_NAME
                var apiSimpleName = API_SIMPLE_NAME

                // This value needs to be processed after all children are visited, because `currPackage`
                // gets set AFTER @file annotations. Wild.
                var packageMappingAnnotation: KtAnnotationEntry? = null

                ktFile.visitAllChildren { element ->
                    when (element) {
                        is KtPackageDirective -> {
                            currPackage = element.fqName.asString()
                        }
                        is KtFileAnnotationList -> {
                            val annotations = element.annotationEntries.toList()
                            annotations.forEach { entry ->
                                when (entry.shortName?.asString()) {
                                    packageMappingSimpleName ->
                                        packageMappingAnnotation = entry
                                }
                            }
                        }

                        is KtImportDirective -> {
                            // It's unlikely this will happen but catch the "import as" case,
                            // e.g. `import com.varabyte.kobweb.api.Api as MyApi`
                            when (element.importPath?.fqName?.asString()) {
                                PACKAGE_MAPPING_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { packageMappingSimpleName = it }
                                    }
                                }

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

                                        val routeOverride = entry.getStringValue(0)?.takeIf { it.isNotBlank() }

                                        if (routeOverride?.startsWith("/") == true || currPackage.startsWith(qualifiedApiPackage)) {
                                            apisToProcess.add(
                                                ApiToProcess(
                                                    funName = element.name!!.toString(),
                                                    pkg = currPackage,
                                                    slugFromFile = file.nameWithoutExtension.toLowerCase(),
                                                    routeOverride = routeOverride
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

                @Suppress("NAME_SHADOWING")
                packageMappingAnnotation?.let { packageMappingAnnotation ->
                    if (currPackage.startsWith(qualifiedApiPackage)) {
                        packageMappings[currPackage] = packageMappingAnnotation.getStringValue(0)!!.let { value ->
                            // {} is a special value which means infer from the current package,
                            // e.g. `a.b.pkg` -> `"{pkg}"`
                            if (value != "{}") value else "{${currPackage.substringAfterLast('.')}}"
                        }
                    }
                    else {
                        reporter.report("${packageMappingAnnotation.containingFile.virtualFile.path}: Skipped over `@file:$packageMappingSimpleName`. It is defined under package `$currPackage` but must exist under `$qualifiedApiPackage`")
                    }
                }
            }

            for (apiToProcess in apisToProcess) {
                val routeOverride = apiToProcess.routeOverride

                val slugPrefix = if (routeOverride != null && routeOverride.startsWith("/")) {
                    // If route override starts with "/" it means the user set the full route explicitly
                    routeOverride.substringBeforeLast("/")
                } else {
                    RouteUtils
                        .resolve(packageMappings, apiToProcess.pkg)
                        .removePrefix(qualifiedApiPackage.replace('.', '/'))
                }

                val prefixExtra = if (routeOverride != null && !routeOverride.startsWith("/") && routeOverride.contains("/")) {
                    // If route override did NOT begin with slash, but contains at least one subdir, it means append
                    // subdir to base route
                    "/" + routeOverride.substringBeforeLast("/")
                }
                else {
                    ""
                }

                val slug = if (routeOverride != null && routeOverride.last() != '/') {
                    routeOverride.substringAfterLast("/")
                } else {
                    apiToProcess.slugFromFile
                }

                apiData._apiMethods.add(
                    ApiEntry(
                        "${apiToProcess.pkg}.${apiToProcess.funName}",
                        "$slugPrefix$prefixExtra/$slug"
                    )
                )
            }

            return apiData
        }
    }
}