package com.varabyte.kobweb.ksp.util

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName

/**
 * Get the value associated with a named argument on an annotation, or the first argument if no name is specified.
 *
 * Returns null if an argument with the name could not be found.
 *
 * @param name The name of the argument to retrieve. If not specified, the value of the first argument will be returned.
 *   This is especially useful if you are sure that the annotation only has a single argument.
 */
fun KSAnnotation.getArgumentValue(name: String? = null): String? {
    return arguments.firstOrNull { name == null || it.name?.asString() == name }?.value?.toString()
}

/**
 * Get the value associated with a named argument on an annotation, or the first argument if no name is specified.
 *
 * Returns null if there is no such annotation on this symbol with the specified fqn, or if an argument with the name
 * could not be found.
 *
 * @param fqn The fully qualified name of the annotation class.
 * @param name The name of the argument to retrieve. If not specified, the value of the first argument will be returned.
 *   This is especially useful if you are sure that the annotation only has a single argument.
 */
fun KSAnnotated.getAnnotationArgumentValue(fqn: String, name: String? = null): String? {
    return this.getAnnotationsByName(fqn).firstOrNull()?.getArgumentValue(name)
}

/**
 * A receiver class that this method extends, e.g. `Scope` in `fun Scope.someFun()`, or null if none.
 */
val KSFunctionDeclaration.receiverClass: KSClassDeclaration?
    get() = extensionReceiver?.resolve()?.declaration as? KSClassDeclaration