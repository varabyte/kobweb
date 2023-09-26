package com.varabyte.kobweb.ksp.util

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile

val KSFile.nameWithoutExtension: String
    get() = fileName.substringBeforeLast(".")

// resolve() so that import aliased annotations can be found as well
fun KSAnnotated.getAnnotationsByName(fqn: String) = annotations
    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == fqn }
