package com.varabyte.kobweb.gradle.core.processors

import com.varabyte.kobweb.gradle.core.project.backend.API_FQN
import com.varabyte.kobweb.gradle.core.project.backend.API_SIMPLE_NAME
import com.varabyte.kobweb.gradle.core.project.backend.API_STREAM_FQN
import com.varabyte.kobweb.gradle.core.project.backend.API_STREAM_SIMPLE_NAME
import com.varabyte.kobweb.gradle.core.project.backend.ApiEntry
import com.varabyte.kobweb.gradle.core.project.backend.ApiStreamEntry
import com.varabyte.kobweb.gradle.core.project.backend.BackendData
import com.varabyte.kobweb.gradle.core.project.backend.INIT_FQN
import com.varabyte.kobweb.gradle.core.project.backend.INIT_SIMPLE_NAME
import com.varabyte.kobweb.gradle.core.project.backend.InitApiEntry
import com.varabyte.kobweb.gradle.core.project.backend.assertValid
import com.varabyte.kobweb.gradle.core.project.common.RouteUtils
import com.varabyte.kobweb.gradle.core.project.common.getStringValue
import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobweb.gradle.core.util.visitAllChildren
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFileAnnotationList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.parents
import java.io.File

private fun KtAnnotated.shouldSuppress(label: String) =
    annotationEntries.any { it.shortName?.asString() == "Suppress" && it.text.contains("\"$label\"") }

/** Common class used for methods annotated with `@Api` and ApiStream objects */
private data class ApiTarget(
    val type: Type,
    val name: String,
    val pkg: String,
    val slugFromFile: String,
    val routeOverride: String?
) {
    enum class Type {
        API,
        STREAM,
    }
}

// Handle `val api = ...` where it is assigned to either an `object : ApiStream` or a return value from the `ApiStream`
// helper method.
private fun processApiStreamProperty(
    file: File,
    filePackage: String,
    property: KtProperty,
    routeOverride: String?,
    apiStreams: MutableList<ApiTarget>,
    reporter: Reporter
): Boolean {
    val propertyName = property.name ?: return false // Null name not expected in practice; fail silently

    // Only top-level properties are allowed for now, so getting the fully qualified path is easy
    if (property.parent !is KtFile) {
        // A user might prefer to define and register an ApiStream inside an @InitApi block, so we'll allow that.
        var showWarning = true
        run {
            var currExpr: KtElement? = property
            while (currExpr != null) {
                if (currExpr is KtNamedFunction && currExpr.annotationEntries.any { it.shortName?.asString() == "InitApi" }) {
                    showWarning = false
                    break
                }
                currExpr = currExpr.parent as? KtElement
            }
        }
        if (showWarning && !property.shouldSuppress("TOP_LEVEL_API_STREAM")) {
            reporter.error("${file.absolutePath}: Not registering ApiStream `val $propertyName`, as only top-level properties are supported at this time. Although fixing this is recommended, you can manually register your API stream inside an @InitApi block instead (`ctx.apis.register($propertyName)`). Suppress this message by adding a `@Suppress(\"TOP_LEVEL_API_STREAM\")` annotation.")
        }
        return false
    }

    return if (property.isPublic) {
        apiStreams.add(
            ApiTarget(
                ApiTarget.Type.STREAM,
                propertyName,
                filePackage,
                slugFromFile = file.nameWithoutExtension.lowercase(),
                routeOverride = routeOverride
            )
        )
        true
    } else {
        if (!property.shouldSuppress("PRIVATE_API_STREAM")) {
            reporter.error("${file.absolutePath}: Not registering ApiStream `val $propertyName`, as it is not public. Although fixing this is recommended, you can manually register your API stream inside an @InitApi block instead (`ctx.apis.register($propertyName)`). Suppress this message by adding a `@Suppress(\"PRIVATE_API_STREAM\")` annotation.")
        }
        false
    }
}

// Handle `val api = ApiStream { ... }`
// callExpr should point at the start of the `ApiStream` call
private fun processApiStreamFactoryMethod(
    file: File,
    filePackage: String,
    callExpr: KtCallExpression,
    apiStreams: MutableList<ApiTarget>,
    reporter: Reporter
): Boolean {
    val property = callExpr.parents.filterIsInstance<KtProperty>().firstOrNull() ?: return false
    val routeOverride = callExpr.getStringValue(0)?.takeIf { it.isNotBlank() }
    return processApiStreamProperty(file, filePackage, property, routeOverride, apiStreams, reporter)
}

// Handle `val api = object : ApiStream { ... }`
private fun processApiStreamObject(
    file: File,
    filePackage: String,
    callEntry: KtSuperTypeCallEntry,
    apiStreams: MutableList<ApiTarget>,
    reporter: Reporter
): Boolean {
    val property = callEntry.parents.filterIsInstance<KtProperty>().firstOrNull() ?: return false
    val routeOverride = callEntry.getStringValue(0)?.takeIf { it.isNotBlank() }
    return processApiStreamProperty(file, filePackage, property, routeOverride, apiStreams, reporter)
}

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
    private val initMethods = mutableListOf<InitApiEntry>()
    private val apiMethods = mutableListOf<ApiEntry>()
    private val apiStreams = mutableListOf<ApiStreamEntry>()

    // fqPkg to subdir, e.g. "api.id._as._int" to "int"
    private val packageMappings = mutableMapOf<String, String>()

    // We need to collect all package mappings before we start processing API routes, so we store them in this
    // intermediate structure for a while
    private val apiTargets = mutableListOf<ApiTarget>()

    override fun handle(file: File, ktFile: KtFile) {
        var currPackage = ""
        var packageMappingSimpleName = com.varabyte.kobweb.gradle.core.project.backend.PACKAGE_MAPPING_SIMPLE_NAME
        var initSimpleName = INIT_SIMPLE_NAME
        var apiSimpleName = API_SIMPLE_NAME
        var apiStreamSimpleName = API_STREAM_SIMPLE_NAME

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

                        API_STREAM_FQN -> {
                            element.alias?.let { alias ->
                                alias.name?.let { apiStreamSimpleName = it }
                            }
                        }
                    }
                }

                is KtCallExpression -> {
                    when (element.calleeExpression?.text) {
                        apiStreamSimpleName -> {
                            processApiStreamFactoryMethod(file, currPackage, element, apiTargets, reporter)
                        }
                    }
                }

                is KtSuperTypeCallEntry -> {
                    when (element.calleeExpression.text) {
                        apiStreamSimpleName -> {
                            processApiStreamObject(file, currPackage, element, apiTargets, reporter)
                        }
                    }
                }

                is KtNamedFunction -> {
                    element.annotationEntries.forEach { entry ->
                        when (val annotationName = entry.shortName?.asString()) {
                            initSimpleName -> {
                                initMethods.add(InitApiEntry("$currPackage.${element.name}"))
                            }

                            apiSimpleName -> {
                                val routeOverride = entry.getStringValue(0)?.takeIf { it.isNotBlank() }
                                if (routeOverride?.startsWith("/") == true || currPackage.startsWith(qualifiedApiPackage)) {
                                    apiTargets.add(
                                        ApiTarget(
                                            type = when (annotationName) {
                                                apiSimpleName -> ApiTarget.Type.API
                                                apiStreamSimpleName -> ApiTarget.Type.STREAM
                                                else -> error("Unexpected annotationName $annotationName")
                                            },
                                            name = element.name!!.toString(),
                                            pkg = currPackage,
                                            slugFromFile = file.nameWithoutExtension.lowercase(),
                                            routeOverride = routeOverride
                                        )
                                    )
                                } else {
                                    reporter.error("${file.absolutePath}: Skipped over `@$annotationName fun ${element.name}`. It is defined under package `$currPackage` but must exist under `$qualifiedApiPackage`")
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
        for (apiTarget in apiTargets) {
            val routeOverride = apiTarget.routeOverride

            val slugPrefix = if (routeOverride != null && routeOverride.startsWith("/")) {
                // If route override starts with "/" it means the user set the full route explicitly
                routeOverride.substringBeforeLast("/")
            } else {
                RouteUtils
                    .resolve(packageMappings, apiTarget.pkg)
                    .removePrefix(qualifiedApiPackage.replace('.', '/'))
            }

            val prefixExtra =
                if (routeOverride != null && !routeOverride.startsWith("/") && routeOverride.contains("/")) {
                    // If route override did NOT begin with slash, but contains at least one subdir, it means append
                    // subdir to base route
                    "/" + routeOverride.substringBeforeLast("/")
                } else {
                    ""
                }

            val slug = if (routeOverride != null && routeOverride.last() != '/') {
                routeOverride.substringAfterLast("/")
            } else {
                apiTarget.slugFromFile
            }

            val fqn = "${apiTarget.pkg}.${apiTarget.name}"
            val route = "$slugPrefix$prefixExtra/$slug"

            when (apiTarget.type) {
                ApiTarget.Type.API -> apiMethods.add(ApiEntry(fqn, route))
                ApiTarget.Type.STREAM -> apiStreams.add(ApiStreamEntry(fqn, route))
            }
        }

        return BackendData(initMethods, apiMethods, apiStreams).also { it.assertValid() }
    }
}
