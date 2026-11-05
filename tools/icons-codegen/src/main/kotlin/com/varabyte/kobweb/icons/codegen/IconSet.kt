package com.varabyte.kobweb.icons.codegen

import java.io.File

/**
 * The visibility of generated icon bindings.
 *
 * Kobweb's published icon artifacts use [PUBLIC], since sharing the icons is their whole point. That also suits a
 * module that generates icons for the rest of a multi-module project.
 *
 * Otherwise, when users express an intention to declare icons dynamically through their build script, [INTERNAL] keeps
 * bindings out of a module's API. Different modules (say, a library and the app using it) can then generate the same
 * icon set, even at different versions, without their bindings clashing or showing up next to each other in
 * autocomplete.
 */
enum class IconVisibility {
    PUBLIC,
    INTERNAL;

    /** The modifier to put in front of a generated declaration (empty for public, Kotlin's default). */
    internal val modifierPrefix: String
        get() = when (this) {
            PUBLIC -> ""
            INTERNAL -> "internal "
        }
}

class IconCodegenOptions(
    val packageName: String,
    val visibility: IconVisibility,
)

/**
 * A set of icons (e.g. Font Awesome or Lucide) that we can download data for and generate Kotlin bindings from.
 *
 * Fetching and generating are separate steps so the downloaded data can be checked in, keeping builds off the network.
 */
interface IconSet {
    /** A short, unique id for this icon set, e.g. "fa" or "lucide". */
    val id: String

    /** Downloads upstream icon data for [version] and writes it to [dataFile]. */
    fun fetch(version: String, dataFile: File)

    /**
     * Writes Kotlin bindings for the icons in [dataFile] under the source root [outputDir].
     *
     * Files go into the directory matching [IconCodegenOptions.packageName], which is deleted first to clear out
     * bindings for icons that no longer exist.
     *
     * @param dataFile A file written by [fetch].
     */
    fun generate(dataFile: File, options: IconCodegenOptions, outputDir: File)

    companion object {
        val all: List<IconSet> = listOf(FaIconSet(), LucideIconSet())
    }
}
