package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * High-level metadata about this current Kobweb-using module.
 *
 * @property kobwebVersion The version of Kobweb this module was built with.
 * @property group The Gradle group of the project that produced this module, e.g. "com.example". This is the value
 *   used when resolving package shortcuts (like `@Layout(".components.layouts.PageLayout")`) in this module's code,
 *   which tools (like IDE plugins) can use to resolve those shortcuts when browsing external artifacts. May be null
 *   for modules built with older versions of Kobweb.
 */
@Serializable
class ModuleMetadata(val kobwebVersion: String, val group: String? = null)
