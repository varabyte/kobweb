package com.varabyte.kobweb.cli.common

import java.nio.file.Files
import java.nio.file.Path

private const val PACKAGE_PART = """[\pL]([\pL\d_]*)"""
private val PACKAGE_REGEX = Regex("""^${PACKAGE_PART}(\.${PACKAGE_PART})*${"$"}""")

object Validations {
    fun isNotEmpty(value: String): String? {
        return when {
            value.isBlank() -> "The value must not be blank"
            else -> null
        }
    }

    fun isFileName(name: String): String? {
        return when {
            name != "." && !name.all { it.isLetterOrDigit() || it == '-' || it == '_' || it == '.' } ->
                "Invalid name. Can only contain: letters, digits, dash, dots, and underscore"
            else -> null
        }
    }

    fun isEmptyPath(pathStr: String): String? {
        Path.of(pathStr).let { path ->
            return when {
                Files.exists(path) -> {
                    when {
                        Files.isRegularFile(path) -> "A file already exists at that location"
                        Files.newDirectoryStream(path).any() -> "The specified directory is not empty"
                        else -> null
                    }
                }
                else -> null
            }
        }
    }

    fun isValidPackage(value: String): String? {
        return when {
            !PACKAGE_REGEX.matches(value) ->
                "Package should be letters, numbers, and underscores, optionally separated by dots (which cannot be followed by digits)"
            // TODO: Reject protected keywords?
            else -> null
        }
    }
}
