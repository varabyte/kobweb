package com.varabyte.kobweb.ksp.frontend

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.common.PAGE_FQN
import com.varabyte.kobweb.ksp.common.processRoute
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.getArgumentValue
import com.varabyte.kobweb.ksp.util.receiverClass
import com.varabyte.kobweb.project.common.DynamicRouteSegment
import com.varabyte.kobweb.project.frontend.PageEntry

/**
 * Process a function marked with the `@Page` annotation
 */
fun processPagesFun(
    annotatedFun: KSFunctionDeclaration,
    layoutFqn: String?,
    initRoutes: Map<String, KSFunctionDeclaration>,
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
    val routeOverride = pageAnnotation.getArgumentValue()?.takeIf { it.isNotBlank() }

    if (routeOverride?.startsWith("/") == true || currPackage.startsWith(qualifiedPagesPackage)) {
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
            val route = processRoute(
                packageRoot = qualifiedPagesPackage,
                pkg = currPackage,
                file = file,
                routeOverride = routeOverride,
                packageMappings = packageMappings,
                supportEmptyDynamicSegments = true,
            ).let { if (it.endsWith("/index")) it.removeSuffix("index") else it }

            return PageEntry(
                annotatedFun.qualifiedName!!.asString(),
                route,
                acceptsContext = annotatedFun.parameters.size == 1,
                layoutFqn = layoutFqn,
                initRouteFqn = initRoutes[annotatedFun.containingFile!!.filePath]?.qualifiedName?.asString(),
                receiverFqn = annotatedFun.receiverClass?.qualifiedName?.asString(),
            )
        } else {
            logger.warn(
                "Skipped over `@$pageSimpleName fun ${funName}`. Route override `${routeOverride}` is invalid, as it contains an inferred, catch-all, and/or optional dynamic segment in a middle location.",
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
