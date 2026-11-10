package com.varabyte.kobweb.icons.codegen

import com.varabyte.kobweb.icons.codegen.fa.FaIconSet
import com.varabyte.kobweb.icons.codegen.lucide.LucideIconSet
import com.varabyte.kobweb.icons.codegen.material.MdiIconSet
import com.varabyte.kobweb.icons.codegen.material.MsIconSet
import java.io.File

/**
 * @property isPublic Whether generated bindings are public, so modules depending on this one can use them, instead of
 *   internal.
 *
 *   If set to false, bindings will be made internal, thereby staying out of the module's API. Different modules (say, a
 *   library and the app using it) can then generate the same icon set, even at different versions, without their
 *   bindings clashing or showing up next to each other in autocomplete.
 */
class IconCodegenOptions(
    val packageName: String,
    val isPublic: Boolean,
)

/** The modifier to put in front of a generated declaration (empty for public, Kotlin's default). */
internal val IconCodegenOptions.modifierPrefix: String
    get() = if (isPublic) "" else "internal "

/**
 * A set of icons (e.g. Font Awesome or Lucide) that we can download data for and generate Kotlin bindings from.
 *
 * Fetching and generating are separate steps so the downloaded data can be checked in, keeping builds off the network.
 */
interface IconSet {
    /** A short, unique id for this icon set, e.g. "fa" or "lucide". */
    val id: String

    /** The default name for this set's icons file, which [fetch] writes and [generateBindings] reads. */
    val iconsFileName: String get() = "$id-icons.txt"

    /** Downloads upstream icon data for [version] and writes it to [iconsFile]. */
    fun fetch(version: String, iconsFile: File)

    /**
     * Writes Kotlin bindings for the icons in [iconsFile] under the source root [outputDir].
     *
     * Files go into the directory matching [IconCodegenOptions.packageName], which is deleted first to clear out
     * bindings for icons that no longer exist.
     *
     * @param iconsFile A file written by [fetch].
     */
    fun generateBindings(iconsFile: File, options: IconCodegenOptions, outputDir: File)

    /**
     * The URL of the stylesheet a page must load for bindings generated at [version] to render, or null if they need
     * none (e.g. because they draw SVGs instead of using an icon font).
     */
    fun stylesheetUrl(version: String): String? = null

    companion object {
        val all: List<IconSet> = listOf(FaIconSet(), LucideIconSet(), MdiIconSet(), MsIconSet())
    }
}
