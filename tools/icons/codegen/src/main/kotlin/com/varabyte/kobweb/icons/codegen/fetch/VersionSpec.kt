package com.varabyte.kobweb.icons.codegen.fetch

/**
 * A version requested for an icon set: an exact version (e.g. "1.70.0"), a prefix ending in "+" (e.g. "1.+"), or
 * [LATEST].
 *
 * This deliberately isn't built on a semantic versioning library. Upstream tags aren't all strict "X.Y.Z" versions,
 * and the semver rules for prereleases and build metadata go unused, since only plain dotted numbers count as
 * releases. Gradle's own version matching isn't public, and this module doesn't depend on Gradle anyway.
 */
internal class VersionSpec(val text: String) {
    val isLatest get() = text == LATEST

    /** True if this spec names a single version, so resolving it doesn't need to look anything up. */
    val isExact get() = !isLatest && !text.endsWith("+")

    /**
     * Returns the newest of these release [tags] that this spec allows, or null if it allows none of them.
     *
     * Only numeric tags (e.g. "1.70.0") count as releases, which skips prereleases like "1.0.0-rc.0" and unrelated
     * tags. They're compared numerically, so "1.10.0" is newer than "1.9.0".
     */
    fun pickFrom(tags: Iterable<String>): String? {
        val prefix = text.removeSuffix("+")
        return tags
            .filter { tag -> RELEASE_TAG.matches(tag) }
            .filter { tag -> if (isExact) tag == text else isLatest || tag.startsWith(prefix) }
            .maxWithOrNull(RELEASE_ORDER)
    }

    companion object {
        const val LATEST = "latest"

        private val RELEASE_TAG = Regex("""\d+(\.\d+)*""")

        /** Orders numeric release versions like "1.70.0" from oldest to newest. */
        private val RELEASE_ORDER = Comparator<String> { a, b ->
            val aParts = a.split('.').map { it.toLong() }
            val bParts = b.split('.').map { it.toLong() }
            aParts.zip(bParts).map { (aPart, bPart) -> aPart.compareTo(bPart) }.firstOrNull { it != 0 }
                ?: aParts.size.compareTo(bParts.size)
        }
    }
}
