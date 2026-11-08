package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * Library-specific metadata.
 *
 * Its existence in a jar's metadata also identifies the jar as a Kobweb library.
 *
 * @param iconSets The icon sets this library generates bindings for in `kobweb { library { icons { ... } } }`, mapped to the
 *   versions they were generated at, e.g. `"fa" to "6.5.2"`. Apps use this to load each set's stylesheet.
 */
@Serializable
class LibraryMetadata(val index: Index, val iconSets: Map<String, String> = emptyMap()) {
    /**
     * Serialized data from the `kobweb { library { index { ... } } }` block.
     *
     * @param headElements If set, a string of DOM nodes to be added to the `<head>` of the user's site.
     */
    @Serializable
    class Index(val headElements: String?)
}
