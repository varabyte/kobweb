package com.varabyte.kobweb.ksp.symbol

import com.google.devtools.ksp.symbol.KSTypeReference

/**
 * Resolves the type and returns its qualified name.
 *
 * This operation is expensive and should be avoided if possible.
 */
fun KSTypeReference.resolveQualifiedName() = resolve().declaration.qualifiedName?.asString()
