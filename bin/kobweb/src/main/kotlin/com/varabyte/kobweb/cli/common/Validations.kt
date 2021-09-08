package com.varabyte.kobweb.cli.common

object Validations {
    fun folderName(name: String): String? {
        return if (!name.all { it.isLetterOrDigit() || it == '-' || it == '_' }) {
            "Invalid folder name. Can only contain: letters, digits, dash, underscore"
        } else {
            null
        }
    }
}