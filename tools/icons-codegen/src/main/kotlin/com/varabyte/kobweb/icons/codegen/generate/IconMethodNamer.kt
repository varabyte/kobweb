package com.varabyte.kobweb.icons.codegen.generate

/**
 * Turns raw icon names into the names of their generated functions, by capitalizing each part between [separator]s and
 * adding [prefix], e.g. "arrow-right" -> "LucideArrowRight".
 */
internal class IconMethodNamer(private val prefix: String, private val separator: Char) {
    fun methodNameFor(rawName: String): String {
        return prefix + rawName.split(separator).joinToString("") { part -> part.replaceFirstChar { it.uppercase() } }
    }
}

/**
 * Throws if any two of these raw icon names map to the same method name, which would otherwise generate conflicting
 * declarations.
 *
 * For example, "arrow-1" and "arrow1" both become "Arrow1", since separators before digits disappear.
 */
internal fun IconMethodNamer.checkUnique(rawNames: Iterable<String>) {
    val clashes = rawNames.groupBy(::methodNameFor).filterValues { it.size > 1 }
    check(clashes.isEmpty()) { "Multiple icons map to the same method name: $clashes" }
}
