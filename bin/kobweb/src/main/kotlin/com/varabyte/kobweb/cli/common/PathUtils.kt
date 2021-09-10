package com.varabyte.kobweb.cli.common

object PathUtils {
    /**
     * Given a path, e.g. "myproject", return it OR the path with a number appended on it if there are already existing
     * folders at that path with that name, e.g. "myproject4"
     */
    fun generateEmptyPathName(name: String): String {
        require(Validations.isFileName(name.substringAfterLast('/')) == null)
        var finalName = name
        var i = 2
        while (Validations.isEmptyPath(finalName) != null) {
            finalName = "$name$i"
            i++
        }
        return finalName
    }
}

/** Convert a string like "**.txt" to a regex that would match "a.txt", "a/b.txt", "a/b/c.txt", etc. */
fun String.wildcardToRegex(): Regex {
    require(!this.contains("***")) { "Invalid wildcard string passed to regex generator: $this" }
    require(!this.contains("\\")) { "No backslashes allowed in string passed to regex generator: $this" }

    val regexStr = this
        .replace(".", """\.""")
        .replace("?", ".")
        .replace("**", "||") // Temporarily get out of way of single star replacement
        .replace("*", """[^/]*""")
        .replace("||", ".*")

    return Regex("^$regexStr\$")
}