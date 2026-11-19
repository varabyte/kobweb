package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import com.varabyte.kobweb.project.common.FqNameShortcut
import com.varabyte.kobweb.project.common.PackageName
import org.commonmark.node.CustomNode

/**
 * A block which represents a method call to insert into the final output.
 *
 * The original input may look something like `{{{ .a.b.c.ExampleCall }}}`
 *
 * If no parentheses are added to the call, they will be appended automatically.
 *
 * @param partiallyQualifiedName The semi-qualified name of this method. See [toFqn] for how it gets resolved.
 * @param appendBrace If true, this call is meant to start a new block of indented code
 */
class KobwebCall(val partiallyQualifiedName: String, var appendBrace: Boolean = false) : CustomNode() {
    /**
     * Convert this class's [partiallyQualifiedName] into a fully qualified name, prefixing it with the given
     * [projectGroup] if it begins with a period.
     *
     * Examples:
     * * `test` -> `test()`
     * * `.test` -> `org.example.myproject.test()`
     * * `test()` -> `test()`
     */
    fun toFqn(projectGroup: String): String {
        // Split a call like ".widgets.VideoPlayer(src = "...")" into its name and everything after it, as only the name
        // part can be a shortcut
        val name = partiallyQualifiedName.takeWhile { it != '(' && it != '{' && it != '<' && !it.isWhitespace() }
        return buildString {
            append(PackageName(projectGroup).resolve(FqNameShortcut(name)))
            append(partiallyQualifiedName.removePrefix(name))
            // A call ending in a name or type arguments (e.g. `Picker<String>`) has no argument list yet
            val needsParentheses = partiallyQualifiedName.last().let { it.isLetterOrDigit() || it == '>' }
            if (needsParentheses && !appendBrace) {
                append("()")
            }

            if (appendBrace) {
                append(" {")
            }
        }
    }
}
