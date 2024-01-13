package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * Library-specific metadata.
 *
 * Its existence in a jar's metadata also identifies the jar as a Kobweb library.
 */
@Serializable
class LibraryMetadata(val index: Index) {
    /**
     * Serialized data from the `kobweb { library { index { ... } } }` block.
     *
     * @param headElements If set, a list of DOM elements to be added to the <head> of the user's site.
     */
    @Serializable
    class Index(val headElements: String?)
}
