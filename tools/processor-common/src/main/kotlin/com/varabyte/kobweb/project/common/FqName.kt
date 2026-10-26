package com.varabyte.kobweb.project.common

/**
 * A fully qualified name for a declaration, e.g. "com.example.components.layouts.PageLayout".
 *
 * Note that we intentionally don't expose the package of a name, as it can't be determined from the name alone. For
 * example, "com.example.Outer.Inner" could be a class `Inner` nested inside a class `Outer`, or a top-level class
 * `Inner` inside the package "com.example.Outer".
 */
@JvmInline
value class FqName(val value: String) {
    /**
     * Create a fully qualified name for a declaration with the given (potentially nested) [name] inside [pkg].
     */
    constructor(pkg: PackageName, name: String) : this(if (pkg.isRoot) name else "$pkg.$name")

    override fun toString() = value
}
