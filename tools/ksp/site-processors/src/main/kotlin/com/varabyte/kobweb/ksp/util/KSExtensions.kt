package com.varabyte.kobweb.ksp.util

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile

val KSFile.nameWithoutExtension: String
    get() = fileName.substringBeforeLast(".")

// resolve() so that import aliased annotations can be found as well
fun KSAnnotated.getAnnotationsByName(fqn: String) = annotations
    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == fqn }

/**
 * Returns true if the annotated element has a `@Suppress("...")` annotation matching the given suppression name.
 *
 * @param suppression The name of the suppression to look for, e.g. "UNUSED_PARAMETER"
 */
@OptIn(KspExperimental::class)
fun KSAnnotated.suppresses(suppression: String) =
    getAnnotationsByType(Suppress::class).any { suppression in it.names }
