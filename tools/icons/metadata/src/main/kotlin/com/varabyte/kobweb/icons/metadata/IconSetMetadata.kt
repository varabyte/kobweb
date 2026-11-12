package com.varabyte.kobweb.icons.metadata

import kotlinx.serialization.Serializable

/**
 * The icon set that an artifact has bindings for, which apps use to load the set's stylesheet.
 *
 * Artifacts include this as a JSON file at [PATH].
 *
 * @param id The icon set's id, e.g. "fa".
 * @param version The version the bindings were generated at, e.g. "6.5.2".
 * @param stylesheet The URL of the stylesheet a page must load for the bindings to render, or null if they need none.
 */
@Serializable
class IconSetMetadata(val id: String, val version: String, val stylesheet: String? = null) {
    companion object {
        /** Where this file lives inside an artifact. */
        const val PATH = "META-INF/kobweb/iconset.json"
    }
}
