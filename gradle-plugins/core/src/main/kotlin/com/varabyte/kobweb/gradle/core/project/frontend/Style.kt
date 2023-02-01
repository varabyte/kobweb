package com.varabyte.kobweb.gradle.core.project.frontend

import kotlinx.serialization.Serializable

/**
 * Metadata about code like `val MyStyle = ComponentStyle("my-style")`
 */
@Serializable
class ComponentStyleEntry(val fqcn: String)

/**
 * Metadata about code like `val MyVariant = MyStyle.addVariant("my-variant")`
 */
@Serializable
class ComponentVariantEntry(val fqcn: String)
