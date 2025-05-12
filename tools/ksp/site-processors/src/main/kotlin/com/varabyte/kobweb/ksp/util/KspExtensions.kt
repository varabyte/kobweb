package com.varabyte.kobweb.ksp.util

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.common.text.prefixIfNot
import com.varabyte.kobweb.ksp.symbol.nameWithoutExtension

val KSFunctionDeclaration.receiverClass: KSClassDeclaration?
    get() = extensionReceiver?.resolve()?.declaration as? KSClassDeclaration