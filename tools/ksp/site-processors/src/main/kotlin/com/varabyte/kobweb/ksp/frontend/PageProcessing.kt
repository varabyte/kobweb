package com.varabyte.kobweb.ksp.frontend

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.common.PAGE_FQN
import com.varabyte.kobweb.ksp.common.toRoute
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.getArgumentValue
import com.varabyte.kobweb.ksp.util.receiverClass
import com.varabyte.kobweb.ksp.util.toPackageName
import com.varabyte.kobweb.project.common.DynamicRouteSegment
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.kobweb.project.frontend.PageEntry

/**
 * Convert this function, which is annotated with `@Page`, into a [PageEntry].
 *
 * @return null if the page is invalid, in which case a warning will have been logged.
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
    val routeOverride = pageAnnotation.getArgumentValue()?.takeIf { it.isNotBlank() }

    if (routeOverride?.startsWith("/") == true || currPackage.isSameOrSubpackageOf(qualifiedPagesPackage)) {
        // To maintain the general association between the file name and the slug, we reject route overrides which use
        // the "{}" inferred dynamic segment syntax in any part except for the last.
        // e.g. "/dynamic/{example}/route/{}" is OK but "/dynamic/{}/route/{example}" is not.
        // (While we're here, catch all segments, e.g. "{...rest}", and optional segments, e.g. "{example?}" are also
        // only allowed in the final position.)
        if (routeOverride == null ||
            routeOverride.substringBeforeLast("/", missingDelimiterValue = "").split("/")
                .none { segment ->
                    val dynamicSegment = DynamicRouteSegment.tryCreate(segment)
                    dynamicSegment != null && (dynamicSegment.isInferred || dynamicSegment.isCatchAll || dynamicSegment.isOptional)
                }
        ) {
            val route = file.toRoute(
                packageRoot = qualifiedPagesPackage,
                packageMappings = packageMappings,
                routeOverride = routeOverride,
                supportEmptyDynamicSegments = true,
            ).let { if (it.endsWith("/index")) it.removeSuffix("index") else it }

            return PageEntry(
                qualifiedName!!.asString(),
                route,
                acceptsContext = parameters.size == 1,
                layoutFqn = layoutFqn,
                initRouteFqn = initRoutes[file.filePath]?.qualifiedName?.asString(),
                receiverFqn = receiverClass?.qualifiedName?.asString(),
            )
        } else {
            logger.warn(
                "Skipped over `@$pageSimpleName fun ${funName}`. Route override `${routeOverride}` is invalid, as it contains an inferred, catch-all, and/or optional dynamic segment in a middle location.",
                this
            )
        }
    } else {
        logger.warn(
            "Skipped over `@$pageSimpleName fun ${funName}`. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`.",
            this
        )
    }
    return null
}
