package com.varabyte.kobweb.util.text

/**
 * A class used for checking input text against a pattern which, if it matches, will produce new text.
 *
 * @property pattern The regex pattern to match against
 * @property target The target text to produce if the pattern matches. This text can contain $1, $2, etc., which will be
 *   replaced with the actual values from the match.
 */
class PatternMapper(val pattern: Regex, val target: String) {
    constructor(pattern: String, target: String) : this(Regex(pattern), target)

    /**
     * Resolve a regex pattern against a string, replacing any $1, $2, etc. with the actual values from the match.
     *
     * For example,
     * ```
     * PatternMapper(Regex("old-folder/(.+)"), "new-folder/$1").map("old-folder/some-file")
     * ```
     * will produce: "new-folder/some-file"
     */
    fun map(text: String): String? {
        return pattern.matchEntire(text)?.let { match ->
            var final = target

            // I don't expect this to happen, but if a string has $12 in it, I don't want $1 to consume it.
            // Substituting in reverse order should avoid this.
            for (i in match.groupValues.lastIndex downTo 0) {
                final = final.replace("\$$i", match.groupValues[i])
            }

            return final
        }
    }
}

fun PatternMapper.matches(text: String) = pattern.matches(text)
