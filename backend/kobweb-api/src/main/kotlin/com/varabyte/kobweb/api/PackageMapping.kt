package com.varabyte.kobweb.api

/**
 * An annotation which allows for mapping the current file's package name to a different URL name.
 *
 * This is useful as package names are constrained more than URL names, and occasionally you want to be more expressive
 * than they allow.
 *
 * To declare a package mapping, create a file (the name doesn't matter, but we recommend `PackageMapping.kt` as a
 * convention) and tag the file with the mapping you want.
 *
 * ```
 * // api/aAndB/PackageMapping.kt
 * @file:PackageMapping("a+b")
 *
 * package api.aAndB
 *
 * import com.varabyte.kobweb.api.PackageMapping
 * ```
 *
 * This annotation only works on the tail of the current package (i.e. `c` in package `a.b.c`), so if you want to affect
 * multiple folders in a path, each part should have its own `PackageMapping` annotation.
 *
 * Finally, note that leading underscores are automatically removed, and camelcase package names are converted into
 * kebab-case automatically, so you don't need to do anything for those cases. For example, "pages.exampleApi._int"
 * will automatically be converted to "/example-api/int".
 *
 * @param value The part inside the final URL that this package should map to.
 */
@Target(AnnotationTarget.FILE)
annotation class PackageMapping(val value: String)
