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
     * @param stylesheet The URL of the stylesheet a page must load for the bindings to render, or null if they need
     *   none.
     */
    @Serializable
    class IconSet(val version: String, val stylesheet: String? = null)

    companion object {
        /** Where this file lives inside an artifact. */
        const val PATH = "META-INF/kobweb/icons.json"
    }
}
