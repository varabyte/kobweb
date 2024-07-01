package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import com.varabyte.kobweb.project.common.PackageUtils
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
        return buildString {
            append(PackageUtils.resolvePackageShortcut(projectGroup, partiallyQualifiedName))
            if (partiallyQualifiedName.last().isLetterOrDigit() && !appendBrace) {
                append("()")
            }

            if (appendBrace) {
                append(" {")
            }
        }
    }
}
