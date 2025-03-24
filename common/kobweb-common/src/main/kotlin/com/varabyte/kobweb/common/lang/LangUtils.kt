package com.varabyte.kobweb.common.lang

import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

/**
 * Test if this string is a reserved hard keyword.
 *
 * A hard keyword is one whose appearance is very constrained in where it can appear in your code. For example, they
 * cannot be used inside package names.
 *
 * See also: https://kotlinlang.org/docs/keyword-reference.html#hard-keywords
 */
fun String.isHardKeyword(): Boolean {
    return when (this) {
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while" -> true

        else -> false
    }
}

/**
 * Transform an input String into one that satisfies Java package naming rules.
 *
 * See also: https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html
 */
fun String.toPackageName(): String {
    if (this.isEmpty()) return this

    var packageName = this.replace('-', '_').filter { it.isLetterOrDigit() || it == '_' }
    check(packageName.isNotEmpty()) { "Cannot convert \"$this\" to a package name, all characters are invalid" }
    if (packageName.first().isDigit()) {
        packageName = "_$packageName"
    }

    if (packageName.isHardKeyword()) {
        packageName = "${packageName}_"
    }

    return packageName
}

fun String.packageConcat(otherPackage: String): String {
    require(this.isNotEmpty() || otherPackage.isNotEmpty()) { "Attempted to concat two empty strings as a package " }

    if (this.isEmpty()) return otherPackage
    if (otherPackage.isEmpty()) return this

    return (this.split('.') + otherPackage.split('.')).joinToString(".")
}

/**
 * Transform a path to a directory into a corresponding package name.
 *
 * This should be a path to a directory, as every part of it will be included in the final package.
 *
 * Path segments that cannot be directly represented by Kotlin package constraints will be transformed via
 * [toPackageName].
 */
fun Path.dirToPackage(): String {
    return invariantSeparatorsPathString
        .split('/')
        .joinToString(".") { it.toPackageName() }
}

/**
 * Transform a path into a corresponding package name.
 *
 * This should be a path to a file, as the filename will be excluded from the final package.
 *
 * @see [dirToPackage]
 */
fun Path.fileToPackage(): String = this.parent.dirToPackage()
