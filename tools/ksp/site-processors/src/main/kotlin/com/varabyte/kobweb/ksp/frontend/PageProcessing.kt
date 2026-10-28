package com.varabyte.kobweb.ksp.frontend

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.common.PAGE_FQN
import com.varabyte.kobweb.ksp.common.RouteOverride
import com.varabyte.kobweb.ksp.common.toRoute
import com.varabyte.kobweb.ksp.common.reportProblems
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.getArgumentValue
import com.varabyte.kobweb.ksp.util.receiverClass
import com.varabyte.kobweb.ksp.util.toPackageName
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.kobweb.project.frontend.PageEntry

/**
 * Convert this function, which is annotated with `@Page`, into a [PageEntry].
 *
 * @return null if the page is invalid, in which case an error will have been logged.
 */
fun KSFunctionDeclaration.toPageEntry(
    layoutFqn: String?,
    initRoutes: Map<String, KSFunctionDeclaration>,
    qualifiedPagesPackage: PackageName,
    packageMappings: Map<PackageName, String>,
    logger: KSPLogger,
): PageEntry? {
    val pageAnnotation = getAnnotationsByName(PAGE_FQN).first()
    val funName = simpleName.asString()
    val pageSimpleName = pageAnnotation.shortName.asString()
    val file = containingFile ?: error("Symbol does not come from a source file")

    if (annotations.none { it.shortName.asString() == "Composable" }) {
        logger.warn("`fun ${funName}` annotated with `@$pageSimpleName` must also be `@Composable`.", this)
    }

    val currPackage = packageName.toPackageName()
    val routeOverride = pageAnnotation.getArgumentValue()?.takeIf { it.isNotBlank() }?.let { RouteOverride(it) }
    val route = file.toRoute(
        packageRoot = qualifiedPagesPackage,
        packageMappings = packageMappings,
        routeOverride = routeOverride,
    )?.let { if (it.endsWith("/index")) it.removeSuffix("index") else it } ?: run {
        logger.error(
            "`@$pageSimpleName fun $funName` is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage` (or use a route override that starts with a slash).",
            this
        )
        return null
    }
    if (routeOverride?.reportProblems(pageAnnotation, logger) == true) return null

    return PageEntry(
        qualifiedName!!.asString(),
        route,
        acceptsContext = parameters.size == 1,
        layoutFqn = layoutFqn,
        initRouteFqn = initRoutes[file.filePath]?.qualifiedName?.asString(),
        receiverFqn = receiverClass?.qualifiedName?.asString(),
    )
}
