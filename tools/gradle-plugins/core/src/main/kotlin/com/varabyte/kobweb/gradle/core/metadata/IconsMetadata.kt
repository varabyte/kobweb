package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * The icon sets that a module generated bindings for, which apps use to load each set's stylesheet.
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
}
