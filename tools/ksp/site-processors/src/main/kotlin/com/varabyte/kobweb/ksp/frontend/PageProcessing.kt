package com.varabyte.kobweb.ksp.frontend

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.common.PAGE_FQN
import com.varabyte.kobweb.ksp.common.processRoute
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.symbol.nameWithoutExtension
import com.varabyte.kobweb.project.frontend.PageEntry

fun processPagesFun(
    annotatedFun: KSFunctionDeclaration,
    qualifiedPagesPackage: String,
    packageMappings: Map<String, String>,
    logger: KSPLogger,
): PageEntry? {
    val pageAnnotation = annotatedFun.getAnnotationsByName(PAGE_FQN).first()
    val funName = annotatedFun.simpleName.asString()
    val pageSimpleName = pageAnnotation.shortName.asString()
    val file = annotatedFun.containingFile ?: error("Symbol does not come from a source file")

    if (annotatedFun.annotations.none { it.shortName.asString() == "Composable" }) {
        logger.warn("`fun ${funName}` annotated with `@$pageSimpleName` must also be `@Composable`.", annotatedFun)
    }

    val currPackage = annotatedFun.packageName.asString()
    val routeOverride = pageAnnotation.arguments.first().value?.toString()?.takeIf { it.isNotBlank() }

    if (routeOverride?.startsWith("/") == true || currPackage.startsWith(qualifiedPagesPackage)) {
        // To maintain the general association between the file name and the slug, we reject route overrides which use
        // the "{}" inferred dynamic route syntax in any part except for the last.
        // e.g. "/dynamic/{example}/route/{}" is OK but "/dynamic/{}/route/{example}" is not
        if (routeOverride == null || "{}" !in routeOverride.substringBeforeLast("/", missingDelimiterValue = "")) {
            val route = processRoute(
                pkg = currPackage,
                slugFromFile = file.nameWithoutExtension.lowercase(),
                routeOverride = routeOverride,
                qualifiedPackage = qualifiedPagesPackage,
                packageMappings = packageMappings,
                supportDynamicRoute = true,
            ).let { if (it.endsWith("/index")) it.removeSuffix("index") else it }

            return PageEntry(annotatedFun.qualifiedName!!.asString(), route)
        } else {
            logger.warn(
                "Skipped over `@$pageSimpleName fun ${funName}`. Route override is invalid.",
                annotatedFun
            )
        }
    } else {
        logger.warn(
            "Skipped over `@$pageSimpleName fun ${funName}`. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`.",
            annotatedFun
        )
    }
    return null
}
