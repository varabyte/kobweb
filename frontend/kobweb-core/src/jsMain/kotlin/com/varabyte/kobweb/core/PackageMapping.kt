package com.varabyte.kobweb.core

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
 * // pages/aAndB/PackageMapping.kt
 * @file:PackageMapping("a+b")
 *
 * package pages.aAndB
 *
 * import com.varabyte.kobweb.core.PackageMapping
 * ```
 *
 * This annotation only works on the tail of the current package (i.e. `c` in package `a.b.c`), so if you want to affect
 * multiple folders in a path, each part should have its own `PackageMapping` annotation.
 *
 * Finally, note that leading underscores are automatically removed, and camelcase package names are converted into
 * kebab-case automatically, so you don't need to do anything for those cases. For example, "pages.exampleFolder._2022"
 * will automatically be converted to "/example-folder/2022".
 *
 * @param value The part inside the final URL that this package should map to.
 */
@Target(AnnotationTarget.FILE)
annotation class PackageMapping(val value: String)
