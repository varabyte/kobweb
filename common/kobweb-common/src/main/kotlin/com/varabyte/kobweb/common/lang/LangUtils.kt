package com.varabyte.kobweb.common.lang

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
