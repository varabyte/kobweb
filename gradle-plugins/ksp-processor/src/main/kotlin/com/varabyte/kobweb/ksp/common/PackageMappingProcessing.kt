package com.varabyte.kobweb.ksp.common

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile

// pair corresponds to (current package, override)
// in theory should only be one, but we'll return a sequence just in case

// We visit the annotation per-file instead of directly as visiting directly doesn't let us read the package
fun getPackageMappings(
    file: KSFile,
    qualifiedPackage: String,
    packageMappingFqn: String,
    logger: KSPLogger,
): Sequence<Pair<String, String>> {
    // TODO: we resolve here so that we can find the annotation even if annotatedFile's import aliased.
    //  But is there a better way? And should we support this at all? See also: @Api logic
    return file.annotations
        .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == packageMappingFqn }
        .mapNotNull { packageMappingAnnotation ->
            val currPackage = file.packageName.asString()
            if (currPackage.startsWith(qualifiedPackage)) {
                val override = packageMappingAnnotation.arguments.first().value!!.let { value ->
                    // {} is a special value which means infer from the current package,
                    // e.g. "{}" under a.b.pkg resolves to "{pkg}"
                    if (value != "{}") value.toString() else "{${currPackage.substringAfterLast('.')}}"
                }
                currPackage to override
            } else {
                logger.warn(
                    "Skipped over `@file:${packageMappingAnnotation.shortName.asString()}` annotation. It is defined under package `$currPackage` but must exist under `$qualifiedPackage`.",
                    packageMappingAnnotation
                )
                null
            }
        }
}
