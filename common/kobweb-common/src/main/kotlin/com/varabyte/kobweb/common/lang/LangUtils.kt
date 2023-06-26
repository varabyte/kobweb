package com.varabyte.kobweb.common.lang

/**
 * Test if this string is a reserved keyword.
 *
 * See also: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
 * See also: https://kotlinlang.org/docs/keyword-reference.html
 */
fun String.isKeyword(): Boolean {
    return when (this) {
        "abstract",
        "as",
        "assert",
        "boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extends",
        "false",
        "final",
        "finally",
        "float",
        "for",
        "fun",
        "goto",
        "if",
        "in",
        "is",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "null",
        "object",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "void",
        "volatile",
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

    if (packageName.isKeyword()) {
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
