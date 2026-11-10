package com.varabyte.kobweb.icons.metadata

import kotlinx.serialization.Serializable

/**
 * The icon sets that an artifact has bindings for, which apps use to load each set's stylesheet.
 *
 * Artifacts include this as a JSON file at [PATH].
 *
 * @param iconSets The icon sets, keyed by their id (e.g. "fa").
 */
@Serializable
class IconsMetadata(val iconSets: Map<String, IconSet>) {
    /**
     * @param version The version the bindings were generated at, e.g. "6.5.2".
     * @param packageName The package the bindings were generated into, e.g. "com.example.components.icons.fa".
     */
    @Serializable
    class IconSet(val version: String, val packageName: String)

    companion object {
        /** Where this file lives inside an artifact. */
        const val PATH = "META-INF/kobweb/icons.json"
    }
}
