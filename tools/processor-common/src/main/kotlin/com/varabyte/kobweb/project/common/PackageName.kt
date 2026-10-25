package com.varabyte.kobweb.project.common

/**
 * A fully qualified package name, e.g. "com.example.pages".
 *
 * An empty value represents the root package.
 *
 * Note that this class does not validate its value. Packages from user code are reported by KSP, which has already
 * validated them, but without any backticks (e.g. package com.example.`my-site` is reported as
 * "com.example.my-site"), so we can't hold them to Kotlin's syntax rules ourselves. Values that come from elsewhere,
 * e.g. a package configured in a Gradle block, should be checked by whatever code reads them.
 */
@JvmInline
value class PackageName(val value: String) {
    val isRoot get() = value.isEmpty()

    /**
     * Return true if this package is the same as [other] or lives somewhere underneath it.
     *
     * This compares whole package segments, so "a.b.c" is under "a.b", but "a.bc" is not.
     */
    fun isSameOrSubpackageOf(other: PackageName): Boolean {
        return other.isRoot || value == other.value || value.startsWith("${other.value}.")
    }

    override fun toString() = value
}
