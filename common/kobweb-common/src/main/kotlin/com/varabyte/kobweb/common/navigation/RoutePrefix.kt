package com.varabyte.kobweb.common.navigation

import com.varabyte.kobweb.common.text.ensureSurrounded

class RoutePrefix(value: String) {
    val value = value.takeIf { it.isNotBlank() }?.ensureSurrounded("/") ?: ""

    /**
     * Prepend this route prefix onto some target absolute path.
     *
     * If the path is a relative path, it will be return unchanged.
     */
    fun prependTo(path: String): String {
        if (value.isBlank()) return path
        if (!path.startsWith("/")) return path

        return value.dropLast(1) + path
    }

    override fun toString() = value
}