package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.kobweb.icons.model.IconMethodNamer

/**
 * Throws if any two of these raw icon names map to the same method name, which would otherwise generate conflicting
 * declarations.
 *
 * For example, "arrow-1" and "arrow1" both become "Arrow1", since separators before digits disappear.
 */
internal fun IconMethodNamer.checkUnique(rawNames: Iterable<String>) {
    val clashes = rawNames.groupBy(::nameFor).filterValues { it.size > 1 }
    check(clashes.isEmpty()) { "Multiple icons map to the same method name: $clashes" }
}
