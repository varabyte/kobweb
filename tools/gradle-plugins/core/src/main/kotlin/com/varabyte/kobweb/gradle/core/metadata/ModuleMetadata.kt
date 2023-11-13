package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * High-level metadata about this current Kobweb-using module.
 */
@Serializable
class ModuleMetadata(val kobwebVersion: String)
