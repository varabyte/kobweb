package com.varabyte.kobweb.cli.common.version

/**
 * A simple SemVar parser.
 *
 * See also: https://semver.org/
 *
 * Use [SemVer.parse] to create an instance.
 */
sealed interface SemVer {
    class Parsed(val major: Int, val minor: Int, val patch: Int) : SemVer, Comparable<Parsed> {
        init {
            require(major >= 0) { "Major version must be >= 0" }
            require(minor >= 0) { "Minor version must be >= 0" }
            require(patch >= 0) { "Patch version must be >= 0" }
        }

        override fun compareTo(other: Parsed): Int {
            if (major != other.major) return major.compareTo(other.major)
            if (minor != other.minor) return minor.compareTo(other.minor)
            return patch.compareTo(other.patch)
        }

        override fun toString(): String = "$major.$minor.$patch"
    }
    class Unparsed(val text: String) : SemVer {
        override fun toString() = text
    }

    companion object {
        /**
         * Attempt to parse a simple SemVer string.
         *
         * Returns [Parsed] if the string is a valid SemVer, otherwise [Unparsed].
         *
         * Note that this is a very simple parser, and doesn't support pre-release suffixes.
         */
        fun parse(text: String): SemVer {
            val parts = text.split('.')
            if (parts.size != 3) {
                return Unparsed(text)
            }
            return try {
                Parsed(
                    major = parts[0].toIntOrNull() ?: return Unparsed(text),
                    minor = parts[1].toIntOrNull() ?: return Unparsed(text),
                    patch = parts[2].toIntOrNull() ?: return Unparsed(text),
                )
            } catch (ex: IllegalArgumentException) {
                Unparsed(text)
            }
        }
    }
}