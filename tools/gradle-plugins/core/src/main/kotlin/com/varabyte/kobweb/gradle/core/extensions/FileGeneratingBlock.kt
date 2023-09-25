package com.varabyte.kobweb.gradle.core.extensions

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property

interface FileGeneratingBlock : ExtensionAware {
    /** The path to the root where generated files will be placed, relative to the project build directory. */
    val genDir: Property<String>
}
