package com.varabyte.kobweb.gradle.core.processors

import com.varabyte.kobweb.gradle.core.project.backend.API_FQN
import com.varabyte.kobweb.gradle.core.project.backend.API_SIMPLE_NAME
import com.varabyte.kobweb.gradle.core.project.backend.ApiEntry
import com.varabyte.kobweb.gradle.core.project.backend.BackendData
import com.varabyte.kobweb.gradle.core.project.backend.INIT_FQN
import com.varabyte.kobweb.gradle.core.project.backend.INIT_SIMPLE_NAME
import com.varabyte.kobweb.gradle.core.project.backend.InitApiEntry
import com.varabyte.kobweb.gradle.core.project.backend.assertValid
import com.varabyte.kobweb.gradle.core.project.common.RouteUtils
import com.varabyte.kobweb.gradle.core.project.common.getStringValue
import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobweb.gradle.core.util.visitAllChildren
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFileAnnotationList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

/**
 * A processor that runs over a site's backend code, searching for Kobweb hooks like `@Api` annotations.
 *
 * See also: [BackendData].
 *
 * @param qualifiedApiPackage The fully qualified path to the root of the pages folder. For example,
 *   "com.site.pages"
 */
class BackendDataProcessor(
    private val reporter: Reporter,
    private val qualifiedApiPackage: String
) : TokenProcessor<BackendData> {
    private class ApiToProcess(
        val funName: String,
        val pkg: String,
        val slugFromFile: String,
        // See @Api annotation header docs for `routeOverride` behavior
        val routeOverride: String?
    )

    private val initMethods = mutableListOf<InitApiEntry>()
    private val apiMethods = mutableListOf<ApiEntry>()

    // fqPkg to subdir, e.g. "api.id._as._int" to "int"
    private val packageMappings = mutableMapOf<String, String>()
    // We need to collect all package mappings before we start processing API routes, so we store them in this
    // intermediate structure for a while
    private val apisToProcess = mutableListOf<ApiToProcess>()

    override fun handle(file: File, ktFile: KtFile) {
        var currPackage = ""
        var packageMappingSimpleName = com.varabyte.kobweb.gradle.core.project.backend.PACKAGE_MAPPING_SIMPLE_NAME
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
                        com.varabyte.kobweb.gradle.core.project.backend.PACKAGE_MAPPING_FQN -> {
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
                                initMethods.add(InitApiEntry("$currPackage.${element.name}"))
                            }

                            apiSimpleName -> {

                                val routeOverride = entry.getStringValue(0)?.takeIf { it.isNotBlank() }

                                if (routeOverride?.startsWith("/") == true || currPackage.startsWith(qualifiedApiPackage)) {
                                    apisToProcess.add(
                                        ApiToProcess(
                                            funName = element.name!!.toString(),
                                            pkg = currPackage,
                                            slugFromFile = file.nameWithoutExtension.lowercase(),
                                            routeOverride = routeOverride
                                        )
                                    )
                                } else {
                                    reporter.error("${file.absolutePath}: Skipped over `@$apiSimpleName fun ${element.name}`. It is defined under package `$currPackage` but must exist under `$qualifiedApiPackage`")
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
            } else {
                reporter.error("${packageMappingAnnotation.containingFile.virtualFile.path}: Skipped over `@file:$packageMappingSimpleName`. It is defined under package `$currPackage` but must exist under `$qualifiedApiPackage`")
            }
        }
    }

    override fun finish(): BackendData {
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

            apiMethods.add(
                ApiEntry(
                    "${apiToProcess.pkg}.${apiToProcess.funName}",
                    "$slugPrefix$prefixExtra/$slug"
                )
            )
        }

        return BackendData(initMethods, apiMethods).also { it.assertValid() }
    }
}