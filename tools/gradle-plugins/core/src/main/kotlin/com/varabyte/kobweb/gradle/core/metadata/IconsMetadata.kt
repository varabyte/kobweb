package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * The icon sets that a module generated bindings for, which apps use to load each set's stylesheet.
 *
 * @param iconSets Icon set ids mapped to the versions their bindings were generated at, e.g. `"fa" to "6.5.2"`.
 */
@Serializable
class IconsMetadata(val iconSets: Map<String, String>)
