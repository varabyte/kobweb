package com.varabyte.kobweb.api

/**
 * An annotation which allows for mapping the current file's package name to a different URL name.
 *
 * This is useful as package names are constrained more than URL names, and occasionally you want to be more expressive
 * than they allow.
 *
 * For example, maybe you want your API route to contain a reserved keyword, like `int`. You could create a package
 * called `_int` and map it to `int`, so for example you could support mapping `api.id._as._int` to `api/id/as/int`
 *
 * This annotation only works on the tail of the current package, so for the `_as._int` example above, you'd need two
 * `PackageMapping` annotations, one in a file inside the `_as` package and one inside the `_int` one:
 *
 * ```
 * // api/id/_as/PackageMapping.kt
 * @file:PackageMapping("as")
 *
 * // api/id/_as/_int/PackageMapping.kt
 * @file:PackageMapping("int")
 * ```
 *
 * @param value The part inside the final URL that this package should map to.
 */
@Target(AnnotationTarget.FILE)
annotation class PackageMapping(val value: String)