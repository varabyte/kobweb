package com.varabyte.kobweb.core

/**
 * An annotation which allows for mapping the current file's package name to a different URL name.
 *
 * This is useful as package names are constrained more than URL names, and occasionally you want to be more expressive
 * than they allow.
 *
 * This is particularly useful for dates, since packages cannot be pure numerics. So you might find yourself wanting to
 * map `_2022._01` to the URL `2022/01`. Much cleaner!
 *
 * This annotation only works on the tail of the current package, so for the `_2022._01` example above, you'd need two
 * `PackageMapping` annotations, one in a file inside the `_2022` package and one inside the `_01` one:
 *
 * ```
 * // pages/example/subpath/_2022/PackageMapping.kt
 * @file:PackageMapping("2022")
 *
 * // pages/example/subpath/_2022/_01/PackageMapping.kt
 * @file:PackageMapping("01")
 * ```
 *
 * @param value The part inside the final URL that this package should map to.
 */
@Target(AnnotationTarget.FILE)
annotation class PackageMapping(val value: String)