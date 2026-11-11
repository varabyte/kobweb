package com.varabyte.kobweb.icons.codegen

import com.varabyte.kobweb.icons.codegen.fa.FaCodegen
import com.varabyte.kobweb.icons.codegen.lucide.LucideCodegen
import com.varabyte.kobweb.icons.codegen.material.MdiCodegen
import com.varabyte.kobweb.icons.codegen.material.MsCodegen
import java.io.File

/**
 * A set of icons (e.g. Font Awesome or Lucide) that we can download data for and generate Kotlin bindings from.
 *
 * Fetching and generating are separate steps so the downloaded data can be checked in, keeping builds off the network.
 */
interface IconSetCodegen {
    /** A short, unique id for this icon set, e.g. "fa" or "lucide". */
    val id: String

    /** Downloads upstream icon data for [version] and writes it to [iconsFile]. */
    fun fetch(version: String, iconsFile: File)

    /**
     * Writes Kotlin bindings for the icons in [iconsFile] under the source root [outputDir].
     *
     * Files go into the directory matching [packageName], which is deleted first to clear out
     * bindings for icons that no longer exist.
     *
     * @param iconsFile A file written by [fetch].
     */
    fun generateBindings(iconsFile: File, packageName: String, outputDir: File)

    /**
     * The URL of the stylesheet a page must load for bindings generated at [version] to render, or null if they need
     * none (e.g. because they draw SVGs instead of using an icon font).
     */
    fun stylesheetUrl(version: String): String? = null

    companion object {
        val all: List<IconSetCodegen> = listOf(FaCodegen(), LucideCodegen(), MdiCodegen(), MsCodegen())
    }
}
