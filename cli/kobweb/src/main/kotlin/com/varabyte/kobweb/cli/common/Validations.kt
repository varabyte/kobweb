package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.isKeyword
import java.nio.file.Files
import java.nio.file.Path

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
        if (value.isBlank()) return null

        if (value.startsWith('.') || value.endsWith('.') || value.contains("..")) {
            return "Package should not contain any empty parts."
        }

        value.split(".").forEach { part ->
            if (part.isKeyword()) {
                return "\"$part\" is a reserved keyword in Kotlin and cannot be used. Suggestion: Use \"${part}_\" instead."
            } else if (part.first().isDigit()) {
                return "Package parts cannot start with digits. Suggestion: Use \"_$part\" instead."
            } else if (!part.all { it.isLetterOrDigit() || it == '_' }) {
                return "Package parts can only use letters, numbers, and underscores."
            }
        }

        return null
    }
}
