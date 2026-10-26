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
 * @property publicPath The path of public resources inside this module's resources folder, e.g. "public" (see
 *   `kobweb.publicPath`). Apps use this to find the public resources a dependency provides. May be null for modules
 *   built with older versions of Kobweb, which always used "public". See also [publicPathOrDefault].
 */
@Serializable
class ModuleMetadata(val kobwebVersion: String, val group: String? = null, val publicPath: String? = null)

/**
 * This module's [publicPath][ModuleMetadata.publicPath], falling back to "public" for modules built with older versions
 * of Kobweb (which didn't record it but always used that value).
 */
val ModuleMetadata.publicPathOrDefault get() = publicPath ?: "public"
