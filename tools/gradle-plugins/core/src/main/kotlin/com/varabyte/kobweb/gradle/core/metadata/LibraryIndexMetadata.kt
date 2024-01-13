package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * Serialized data from the `kobweb { library { index { ... } } }` block.
 */
@Deprecated("Migrated to LibraryMetadata")
@Serializable
class LibraryIndexMetadata(val headElements: String) {
    fun toLibraryMetadata() = LibraryMetadata(LibraryMetadata.Index(headElements))
}
