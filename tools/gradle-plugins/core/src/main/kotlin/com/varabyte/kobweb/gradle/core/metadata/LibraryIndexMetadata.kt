package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * Serialized data from the `kobweb { library { index { ... } } }` block.
 */
@Serializable
class LibraryIndexMetadata(val headElements: String)
