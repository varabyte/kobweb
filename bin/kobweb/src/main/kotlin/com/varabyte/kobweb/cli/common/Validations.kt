package com.varabyte.kobweb.cli.common

import java.nio.file.Files
import java.nio.file.Path

object Validations {
    fun folderName(name: String): String? {
        return if (name != "." && !name.all { it.isLetterOrDigit() || it == '-' || it == '_' }) {
            "Invalid folder name. Can only contain: letters, digits, dash, underscore"
        } else {
            null
        }
    }

    fun emptyPath(pathStr: String): String? {
        Path.of(pathStr).let { path ->
            return if (Files.exists(path)) {
                if (Files.isRegularFile(path)) {
                    "A file already exists at that location"
                } else if (Files.newDirectoryStream(path).any()) {
                    "The specified directory is not empty"
                }
                else {
                    null
                }
            } else {
                null
            }
        }
    }
}